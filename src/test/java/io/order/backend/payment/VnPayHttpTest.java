package io.order.backend.payment;
import com.sun.net.httpserver.HttpServer;
import io.order.backend.payment.application.port.PaymentGateway;
import io.order.backend.payment.config.VNPayConfig;
import io.order.backend.payment.domain.Payment;
import io.order.backend.payment.infrastructure.provider.VnPayProvider;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;
import java.net.InetSocketAddress;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.assertj.core.api.Assertions.*;
class VnPayHttpTest {
    @Test void refund_andQuery_verifyResponseSignatureAmountAndSettlement() throws Exception {
        var json=JsonMapper.builder().build(); var corrupt=new AtomicBoolean(false);
        var server=HttpServer.create(new InetSocketAddress("127.0.0.1",0),0);
        String endpoint="http://127.0.0.1:"+server.getAddress().getPort()+"/";
        var provider=new VnPayProvider(new VNPayConfig("merchant","01234567890123456789012345678901","https://example.test/pay",endpoint,"https://example.test/return","127.0.0.1"),new OkHttpClient(),json);
        server.createContext("/",exchange->{
            var request=json.readTree(exchange.getRequestBody()); String command=request.get("vnp_Command").asString();
            var response=new LinkedHashMap<String,String>();
            for(String key:List.of("ResponseId","Command","ResponseCode","Message","TmnCode","TxnRef","Amount","BankCode","PayDate","TransactionNo","TransactionType","TransactionStatus","OrderInfo")) response.put("vnp_"+key,"");
            response.put("vnp_ResponseId","response");response.put("vnp_Command",command);response.put("vnp_ResponseCode","00");response.put("vnp_TmnCode","merchant");response.put("vnp_TxnRef","ref");response.put("vnp_Amount",corrupt.get()?"999":"1000");response.put("vnp_TransactionNo","123");response.put("vnp_TransactionType","02");response.put("vnp_TransactionStatus","00");
            String data=String.join("|",response.values());if("querydr".equals(command))data+="||";
            response.put("vnp_SecureHash",provider.sign(data));
            byte[] body=json.writeValueAsBytes(response);exchange.sendResponseHeaders(200,body.length);exchange.getResponseBody().write(body);exchange.close();
        });
        server.start();
        try {
            var p=PaymentDomainTest.pending(BigDecimal.TEN).observe(true,"123").requestRefund("stable","admin","reason",Instant.now()).refundState(Payment.RefundStatus.DISPATCHING);
            assertThat(provider.refund(p)).isEqualTo(PaymentGateway.RefundOutcome.ACCEPTED);
            assertThat(provider.query(p).state()).isEqualTo(PaymentGateway.Observation.State.REFUNDED);
            corrupt.set(true);
            assertThat(provider.refund(p)).isEqualTo(PaymentGateway.RefundOutcome.UNCERTAIN);
            assertThat(provider.query(p).state()).isEqualTo(PaymentGateway.Observation.State.UNKNOWN);
        } finally { server.stop(0); }
    }
}
