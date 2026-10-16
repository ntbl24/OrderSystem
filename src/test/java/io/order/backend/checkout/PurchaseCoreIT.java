package io.order.backend.checkout;
import io.order.backend.checkout.application.CheckoutService;
import io.order.backend.checkout.dto.CheckoutRequest;
import io.order.backend.common.application.port.CurrentBuyerPort;
import io.order.backend.order.application.*;
import io.order.backend.order.application.port.out.OrderRepository;
import io.order.backend.product.application.port.out.ProductRepository;
import io.order.backend.product.domain.Product;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.testcontainers.postgresql.PostgreSQLContainer;
import java.util.*;
import java.math.BigDecimal;
import java.util.concurrent.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
@SpringBootTest(properties={"app.workers.enabled=false","spring.jpa.hibernate.ddl-auto=validate","spring.security.oauth2.resourceserver.jwt.jwk-set-uri=https://issuer.example/jwks","spring.security.oauth2.resourceserver.jwt.issuer-uri=https://issuer.test","spring.security.oauth2.resourceserver.jwt.audiences=orders","payment.vnpay.tmn-code=TEST","payment.vnpay.secret-key=abcdefghijklmnopqrstuvwxyz0123456789","payment.vnpay.pay-url=https://sandbox.vnpayment.vn/paymentv2/vpcpay.html","payment.vnpay.api-url=https://sandbox.vnpayment.vn/merchant_webapi/api/transaction","payment.vnpay.return-url=https://merchant.example/return","payment.vnpay.server-ip=127.0.0.1"})
class PurchaseCoreIT {
 static PostgreSQLContainer container;
 @DynamicPropertySource static void database(DynamicPropertyRegistry r){String url=System.getProperty("test.db.url");if(url!=null){r.add("spring.datasource.url",() -> url);r.add("spring.datasource.username",() -> System.getProperty("test.db.username","orders_test"));r.add("spring.datasource.password",() -> System.getProperty("test.db.password",""));}else{container=new PostgreSQLContainer("postgres:17-alpine");container.start();r.add("spring.datasource.url",container::getJdbcUrl);r.add("spring.datasource.username",container::getUsername);r.add("spring.datasource.password",container::getPassword);}}
 @AfterAll static void stop(){if(container!=null)container.stop();}
 @MockitoBean CurrentBuyerPort buyer;
 @MockitoBean org.springframework.security.oauth2.jwt.JwtDecoder jwtDecoder;
 @Autowired CheckoutService checkout;@Autowired ProductRepository products;@Autowired OrderRepository orders;@Autowired OrderService orderService;@Autowired OrderExpiryService expiry;@Autowired PaymentSucceededHandler paid;@Autowired PaymentRefundedHandler refunded;@Autowired PlatformTransactionManager manager;
 @BeforeEach void owner(){when(buyer.buyerId()).thenReturn("buyer-core");}
 Product product(int stock,String seller){Product p=new Product();p.setTitle("Snapshot name");p.setSku(UUID.randomUUID().toString());p.setSellerId(seller);p.setPrice(new BigDecimal("100000"));p.setStock(stock);return products.save(p);}
 CheckoutRequest request(Product p,int quantity){return new CheckoutRequest(List.of(new CheckoutRequest.Item(p.getId(),quantity)),"Buyer shipping address",null);}
 String event(String orderId,String paymentId){return "{\"orderId\":\""+orderId+"\",\"paymentId\":\""+paymentId+"\",\"amount\":130000,\"currency\":\"VND\"}";}
 @Test void checkout_whenReplayed_reservesOnceAndSnapshotsServerPrice(){var p=product(5,"seller");var req=request(p,2);String key=UUID.randomUUID().toString();var first=checkout.checkout(key,req);var second=checkout.checkout(key,req);assertThat(second.getId()).isEqualTo(first.getId());assertThat(first.getSubtotal()).isEqualByComparingTo("200000");assertThat(first.getTotal()).isEqualByComparingTo("230000");assertThat(products.findById(p.getId()).orElseThrow().getReserved()).isEqualTo(2);when(buyer.buyerId()).thenReturn("other-buyer");assertThatThrownBy(() -> orderService.getOrderById(first.getId())).isInstanceOf(io.order.backend.common.exception.ResourceNotFoundException.class);}
 @Test void cart_usesServerPricesAndCanBeRecreatedAfterDelete(){var p=product(3,"seller");var dto=new io.order.backend.cart.dto.CartDTO();dto.setUserId("buyer-core");var item=new io.order.backend.cart.dto.CartItemDTO();item.setProductId(p.getId());item.setQuantity(2);item.setProductName("Forged");item.setUnitPrice(BigDecimal.ONE);dto.setItems(List.of(item));var cart=cartService.createCart(dto);assertThat(cart.getSubTotal()).isEqualByComparingTo("200000");assertThat(cart.getItems().getFirst().getProductName()).isEqualTo("Snapshot name");cartService.deleteCart(cart.getId());assertThat(cartService.createCart(dto).getId()).isNotEqualTo(cart.getId());cartService.clearCart("buyer-core");}
 @Test void cancellation_releasesReservationAndRejectsRepeatedCancel(){var p=product(2,"seller");var order=checkout.checkout(UUID.randomUUID().toString(),request(p,1));orderService.cancel(order.getId());assertThat(products.findById(p.getId()).orElseThrow().getReserved()).isZero();assertThatThrownBy(() -> orderService.cancel(order.getId())).isInstanceOf(IllegalStateException.class);paid.handle("late-cancel","late-cancel-payment",event(order.getId(),"late-cancel-payment"));assertThat(orderService.getOrderById(order.getId()).getOrderStatus()).isEqualTo("PAYMENT_REVIEW");assertThat(products.findById(p.getId()).orElseThrow().getStock()).isEqualTo(2);}
 @Test void idempotency_conflictingPayloadDoesNotReserveAgain(){var p=product(5,"seller");String key=UUID.randomUUID().toString();checkout.checkout(key,request(p,1));assertThatThrownBy(() -> checkout.checkout(key,request(p,2))).isInstanceOf(io.order.backend.common.exception.ConflictException.class);assertThat(products.findById(p.getId()).orElseThrow().getReserved()).isEqualTo(1);}
 @Autowired io.order.backend.cart.application.CartService cartService;
 @Autowired io.order.backend.shipping.application.port.out.ShipmentRepository shipments;
 @Test void shipping_legacyReplacementPreservesAssignedIdAndFields(){var shipment=new io.order.backend.shipping.domain.Shipment();shipment.setOrderId(UUID.randomUUID().toString());shipment.setTrackingNumber(UUID.randomUUID().toString());shipment.setWeight(new BigDecimal("2.50"));shipment.setStatus("created");shipment=shipments.save(shipment);var replacement=new io.order.backend.shipping.domain.Shipment();replacement.setId(shipment.getId());replacement.setOrderId(shipment.getOrderId());replacement.setWeight(new BigDecimal("3.25"));replacement.setStatus("label_generated");var saved=shipments.save(replacement);assertThat(saved.getId()).isEqualTo(shipment.getId());assertThat(shipments.findByOrderId(shipment.getOrderId()).orElseThrow().getWeight()).isEqualByComparingTo("3.25");}
 @Test void product_rejectsFractionalVndAndStaleVersion(){var p=product(3,"seller");var stale=products.findById(p.getId()).orElseThrow();p.setStock(4);products.save(p);stale.setStock(5);assertThatThrownBy(() -> products.save(stale)).isInstanceOf(org.springframework.orm.ObjectOptimisticLockingFailureException.class);p.setPrice(new BigDecimal("1.25"));assertThatThrownBy(p::validate).isInstanceOf(IllegalArgumentException.class);}
 @Test void checkout_mixedSellers_rollsBackAllReservations(){var a=product(2,"a");var b=product(2,"b");var req=new CheckoutRequest(List.of(new CheckoutRequest.Item(a.getId(),1),new CheckoutRequest.Item(b.getId(),1)),"address",null);assertThatThrownBy(() -> checkout.checkout(UUID.randomUUID().toString(),req)).isInstanceOf(IllegalStateException.class);assertThat(products.findById(a.getId()).orElseThrow().getReserved()).isZero();assertThat(products.findById(b.getId()).orElseThrow().getReserved()).isZero();}
 @Test void checkout_concurrentLastItem_onlyOneSucceeds() throws Exception {var p=product(1,"seller");var gate=new CountDownLatch(1);try(var pool=Executors.newFixedThreadPool(2)){Callable<Boolean> action=() -> {gate.await();try{checkout.checkout(UUID.randomUUID().toString(),request(p,1));return true;}catch(IllegalStateException ex){return false;}};var a=pool.submit(action);var b=pool.submit(action);gate.countDown();assertThat(List.of(a.get(20,TimeUnit.SECONDS),b.get(20,TimeUnit.SECONDS))).containsExactlyInAnyOrder(true,false);}assertThat(products.findById(p.getId()).orElseThrow().getReserved()).isEqualTo(1);}
 @Test void payment_andFullRefund_commitAndReturnInventoryExactlyOnce(){var p=product(2,"seller");var order=checkout.checkout(UUID.randomUUID().toString(),request(p,1));String payload=event(order.getId(),"pay-one");paid.handle("event-one","pay-one",payload);paid.handle("event-two","pay-one",payload);assertThat(products.findById(p.getId()).orElseThrow().getStock()).isEqualTo(1);assertThatThrownBy(() -> orderService.cancel(order.getId())).isInstanceOf(IllegalStateException.class);refunded.handle("refund-one","pay-one",payload);refunded.handle("refund-two","pay-one",payload);assertThat(products.findById(p.getId()).orElseThrow().getStock()).isEqualTo(2);}
 @Test void expiredOrder_releasesStockAndLatePaymentPersistsReview(){var p=product(2,"seller");var order=checkout.checkout(UUID.randomUUID().toString(),request(p,1));new TransactionTemplate(manager).executeWithoutResult(status -> {var o=orders.lockById(order.getId()).orElseThrow();o.setExpiresAt(java.time.Instant.now().minusSeconds(1));orders.save(o);});expiry.expire(order.getId());paid.handle("late","late-payment",event(order.getId(),"late-payment"));assertThat(orderService.getOrderById(order.getId()).getOrderStatus()).isEqualTo("PAYMENT_REVIEW");assertThat(products.findById(p.getId()).orElseThrow().getReserved()).isZero();assertThat(products.findById(p.getId()).orElseThrow().getStock()).isEqualTo(2);}
}




