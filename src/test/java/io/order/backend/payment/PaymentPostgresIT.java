package io.order.backend.payment;

import io.order.backend.common.application.port.CurrentBuyerPort;
import io.order.backend.common.application.port.IdempotencyPort;
import io.order.backend.common.application.port.OutboxPort;
import io.order.backend.payment.application.usecase.*;
import io.order.backend.payment.application.port.in.*;
import io.order.backend.payment.application.port.out.*;
import io.order.backend.payment.domain.Payment;
import io.order.backend.payment.infrastructure.persistence.JpaPaymentAdapter;
import io.order.backend.payment.infrastructure.persistence.JpaPaymentRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.postgresql.PostgreSQLContainer;
import java.math.BigDecimal;
import java.sql.DriverManager;
import java.util.*;
import java.util.concurrent.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes=PaymentPostgresIT.Config.class,webEnvironment=SpringBootTest.WebEnvironment.NONE,
    properties={"spring.flyway.enabled=false","spring.jpa.hibernate.ddl-auto=validate","spring.jpa.properties.hibernate.default_schema=payment_it","app.workers.enabled=false","spring.security.oauth2.resourceserver.jwt.issuer-uri=https://issuer.test","spring.security.oauth2.resourceserver.jwt.audiences=orders"})
class PaymentPostgresIT {
    static PostgreSQLContainer postgres;
    @DynamicPropertySource static void database(DynamicPropertyRegistry r) throws Exception {
        String url=System.getProperty("test.db.url"),user=System.getProperty("test.db.username","orders_test"),password=System.getProperty("test.db.password","");
        if(url==null) { postgres=new PostgreSQLContainer("postgres:17-alpine"); postgres.start(); url=postgres.getJdbcUrl(); user=postgres.getUsername(); password=postgres.getPassword(); }
        try(var c=DriverManager.getConnection(url,user,password);var s=c.createStatement()) {
            s.execute("CREATE SCHEMA IF NOT EXISTS payment_it"); s.execute("SET search_path TO payment_it");
            s.execute("DROP TABLE IF EXISTS payment_attempts");
            org.springframework.jdbc.datasource.init.ScriptUtils.executeSqlScript(c,new org.springframework.core.io.ClassPathResource("db/migration/V2__payment.sql"));
        }
        String jdbc=url,u=user,p=password; r.add("spring.datasource.url",()->jdbc); r.add("spring.datasource.username",()->u); r.add("spring.datasource.password",()->p);
    }
    @SpringBootConfiguration @EnableAutoConfiguration @EntityScan("io.order.backend.payment.infrastructure.persistence")
    @EnableJpaRepositories(basePackageClasses = JpaPaymentRepository.class)
    @Import({JpaPaymentAdapter.class,PaymentCallbackService.class,RefundPaymentService.class,PaymentQueryService.class,io.order.backend.payment.application.usecase.CreatePaymentService.class})
    static class Config {
        @Bean PayableOrderPort orders(EntityManager em) { return new PayableOrderPort() {
            public PayableOrder lockForPayment(io.order.backend.order.domain.OrderId id,io.order.backend.user.domain.UserId buyer) { lockForCallback(id); throw new UnsupportedOperationException(); }
            public void lockForCallback(io.order.backend.order.domain.OrderId id) { em.createNativeQuery("select 1 from pg_advisory_xact_lock(123456789)").getSingleResult(); }
        }; }
    }
    @MockitoBean org.springframework.security.oauth2.jwt.JwtDecoder jwtDecoder;
    @MockitoBean CurrentBuyerPort buyer; @MockitoBean IdempotencyPort keys;
    @MockitoBean OutboxPort outbox; @MockitoBean PaymentGateway gateway;
    @Autowired PaymentRepository store; @Autowired PaymentCallbackService payments; @Autowired RefundPaymentService refunds; @Autowired PlatformTransactionManager manager;
    @Test void persistence_assignedIdAndConcurrentIpn_singleSettlementEvent() throws Exception {
        var p=PaymentDomainTest.pending(BigDecimal.TEN); var tx=new TransactionTemplate(manager);
        tx.executeWithoutResult(s->store.save(p));
        Payment loaded=tx.execute(s->store.find(p.getId()).orElseThrow());
        assertThat(loaded).usingRecursiveComparison().isEqualTo(p);
        var params=new PaymentNotification(true,true,"ref",BigDecimal.TEN,"VND",true,"123");
        try(var pool=Executors.newFixedThreadPool(2)) {
            var start=new CountDownLatch(1);
            Callable<String> callback=()->{ start.await(); return payments.ipn(params).name(); };
            var a=pool.submit(callback);var b=pool.submit(callback);start.countDown();
            assertThat(List.of(a.get(15,TimeUnit.SECONDS),b.get(15,TimeUnit.SECONDS))).containsExactlyInAnyOrder("CONFIRMED","ALREADY_CONFIRMED");
        }
        assertThat(tx.execute(s->store.find(p.getId()).orElseThrow()).getStatus()).isEqualTo(Payment.Status.SUCCEEDED);
        verify(outbox,times(1)).enqueue(eq("PAYMENT_SUCCEEDED"),eq("payment"),anyString());
    }
    @AfterAll static void cleanup() { if(postgres!=null) postgres.stop(); }
}
