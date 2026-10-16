package io.order.backend.payment;

import io.order.backend.common.application.port.CurrentBuyerPort;
import io.order.backend.common.idempotency.IdempotencyService;
import io.order.backend.common.messaging.OutboxService;
import io.order.backend.payment.application.PaymentService;
import io.order.backend.payment.application.port.*;
import io.order.backend.payment.domain.Payment;
import io.order.backend.payment.infrastructure.persistence.JpaPaymentStore;
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
    @Import({JpaPaymentStore.class,PaymentService.class})
    static class Config {
        @Bean PayableOrderPort orders(EntityManager em) { return new PayableOrderPort() {
            public PayableOrder lockForPayment(String id,String buyer) { lockForCallback(id); throw new UnsupportedOperationException(); }
            public void lockForCallback(String id) { em.createNativeQuery("select 1 from pg_advisory_xact_lock(123456789)").getSingleResult(); }
        }; }
    }
    @MockitoBean org.springframework.security.oauth2.jwt.JwtDecoder jwtDecoder;
    @MockitoBean CurrentBuyerPort buyer; @MockitoBean IdempotencyService keys;
    @MockitoBean OutboxService outbox; @MockitoBean PaymentGateway gateway;
    @Autowired PaymentStore store; @Autowired PaymentService payments; @Autowired PlatformTransactionManager manager;
    @Test void persistence_assignedIdAndConcurrentIpn_singleSettlementEvent() throws Exception {
        var p=PaymentDomainTest.pending(BigDecimal.TEN); var tx=new TransactionTemplate(manager);
        tx.executeWithoutResult(s->store.save(p));
        Payment loaded=tx.execute(s->store.find(p.id()).orElseThrow());
        assertThat(loaded).isEqualTo(p);
        var params=new PaymentNotification(true,true,"ref",BigDecimal.TEN,"VND",true,"123");
        try(var pool=Executors.newFixedThreadPool(2)) {
            var start=new CountDownLatch(1);
            Callable<String> callback=()->{ start.await(); return payments.ipn(params).name(); };
            var a=pool.submit(callback);var b=pool.submit(callback);start.countDown();
            assertThat(List.of(a.get(15,TimeUnit.SECONDS),b.get(15,TimeUnit.SECONDS))).containsExactlyInAnyOrder("CONFIRMED","ALREADY_CONFIRMED");
        }
        assertThat(tx.execute(s->store.find(p.id()).orElseThrow()).status()).isEqualTo(Payment.Status.SUCCEEDED);
        verify(outbox,times(1)).enqueue(eq("PAYMENT_SUCCEEDED"),eq("payment"),anyString());
    }
    @AfterAll static void cleanup() { if(postgres!=null) postgres.stop(); }
}
