# OrderSystem

API mua hàng và thanh toán VNPay, Java 21 / Spring Boot 4.1 / PostgreSQL 17.

## Kiến trúc

Modular monolith theo business module: `product`, `cart`, `inventory`, `checkout`, `order`, `payment`, `shipping`. Domain không phụ thuộc Spring/JPA; persistence và provider integration nằm trong infrastructure. Các module trao đổi qua application ports và outbox/inbox, có thể tách adapter sang HTTP/message broker khi triển khai microservice. Hiện tại một transaction PostgreSQL điều phối checkout và giữ tồn kho; khi tách database cần thay bằng saga, không coi transaction local là transaction phân tán.

### Ranh giới module

- `application.port.in`: use case/query công khai của từng module; controller và local adapter gọi interface thay vì service cụ thể.
- `application.port.out`: repository và dependency do module tiêu thụ định nghĩa; implementation nằm trong `infrastructure`. Spring Data/JPA không đi vào application/domain; pagination Spring Data hiện chỉ được dùng ở application contract, không ở domain.
- Module không truy cập domain, repository hoặc mapper của module khác. Local adapter chuyển đổi snapshot/command bất biến giữa các public input port. Checkout điều phối reservation qua inventory và tạo đơn qua order; product sở hữu thao tác stock, order sở hữu vòng đời reservation.
- Payment application nhận notification có kiểu và trả kết quả trung lập; adapter VNPay xử lý chữ ký, trường giao thức và acknowledgment. Idempotency/outbox được gọi qua shared ports, implementation JDBC vẫn dùng database local.
- DTO API hiện hữu được giữ để tương thích; chưa coi là contract HTTP/message phân tán độc lập. Khi tách microservice cần version hóa contract, thay local adapter và thiết kế saga/compensation thay vì chỉ đổi transport.

ArchUnit kiểm tra domain độc lập framework, application không phụ thuộc adapter/JDBC, controller không gọi implementation và module không vượt qua ranh giới nội bộ của nhau.

## Cấu hình và chạy

Tạo database PostgreSQL riêng và cung cấp biến môi trường. Có thể dùng `docker compose up -d postgres` với `DATABASE_PASSWORD` đã thiết lập; compose chỉ expose database trên loopback và giữ dữ liệu trong named volume:

| Biến | Ý nghĩa |
|---|---|
| `DATABASE_URL` | JDBC URL, mặc định `jdbc:postgresql://localhost:5432/orders` |
| `DATABASE_USERNAME` | Tài khoản database, mặc định `orders` |
| `DATABASE_PASSWORD` | Mật khẩu database (bắt buộc) |
| `JWT_ISSUER_URI` | Issuer của authorization server bên ngoài (bắt buộc) |
| `JWT_AUDIENCE` | Audience được API chấp nhận (bắt buộc) |
| `WORKERS_ENABLED` | Bật xử lý hết hạn, outbox và worker thanh toán, mặc định `true` |
| `VNPAY_TMN_CODE` | Merchant terminal code |
| `VNPAY_SECRET_KEY` | Secret ký và kiểm tra chữ ký (bắt buộc) |
| `VNPAY_RETURN_URL` | URL public GET `/api/v1/payments/vnpay/return` |
| `VNPAY_SERVER_IP` | IP outbound server đăng ký với VNPay |
| `VNPAY_PAY_URL`, `VNPAY_API_URL` | Mặc định sandbox; đổi sang endpoint production khi được VNPay duyệt |
| `PAYMENT_WORKER_DELAY_MS` | Chu kỳ worker thanh toán, mặc định 10.000 ms |

Xem `.env.example` cho tên biến và placeholder. Docker Compose đọc `.env`; Spring Boot không tự nạp `.env`, cần export biến vào process hoặc cấu hình trong IDE. Đăng ký URL IPN `/api/v1/payments/vnpay/ipn` với VNPay; chỉ nhận thông báo từ provider sau kiểm tra chữ ký.

```powershell
mvn spring-boot:run
```

Flyway tạo schema mới; Hibernate chỉ validate, không tự sửa schema. **Không chuyển dữ liệu MongoDB cũ**. Backup dữ liệu cũ và xây dựng migration riêng nếu cần. Không đặt credential trong source hay thư mục static.

JWT được kiểm tra chữ ký, issuer, audience và thời hạn. Buyer lấy từ claim `sub`, không từ request body. Claim `roles` là array chứa `ADMIN`/`BUYER`; quản lý sản phẩm, shipment và hoàn tiền cần `ADMIN`. Không có API phát hành token/password login trong ứng dụng này.

## Quy tắc mua và thanh toán

- Mỗi checkout chỉ gồm sản phẩm của một seller. Giá và tồn kho được đọc trên server; client không quyết định giá, trạng thái đơn hay tổng thanh toán.
- Tiền VND là số nguyên chính xác. Phí vận chuyển cố định 30.000 VND/đơn. Chưa có đa tiền tệ, voucher, phí vận chuyển/tax động hay chia đơn đa seller.
- Checkout nhận danh sách sản phẩm trực tiếp, không tự xóa giỏ hàng. Đơn thanh toán thành công chuyển `PAID`; chưa tự tạo shipment.
- Checkout giữ tồn kho 15 phút. Chỉ đơn chưa thanh toán được hủy; hủy/hết hạn giải phóng reservation đúng một lần.
- Payment attempt gắn với order/buyer, amount, currency và provider reference. VNPay IPN đã kiểm tra chữ ký mới là tín hiệu thanh toán; browser return không tự xác nhận paid.
- Thành công đến muộn sau khi đơn hết hạn/hủy được giữ ở trạng thái review, không tự giao hàng hay tái giữ tồn kho.
- Chỉ hoàn tiền toàn phần. Không tự retry một refund có kết quả không xác định vì có thể hoàn hai lần; phải đối soát với VNPay trước quyết định tiếp theo. Không coi timeout là bằng chứng provider chưa hoàn tiền.
- External HTTP không chạy trong transaction database. Đối soát/refund dùng durable state; order nhận payment event qua outbox/inbox để xử lý lại sau crash.

## API chính

Contract máy đọc nằm tại `src/main/resources/openapi.yml` (không expose thành static route).

| Method | Endpoint | Ý nghĩa |
|---|---|---|
| POST | `/api/v1/checkouts` | Đặt mua, cần `Idempotency-Key` |
| GET | `/api/v1/orders/{id}` | Xem đơn thuộc buyer |
| GET | `/api/v1/orders/number/{number}` | Tìm theo mã đơn |
| GET | `/api/v1/orders/user/{userId}` | Danh sách đơn, chỉ chính buyer |
| POST | `/api/v1/orders/{id}/cancel` | Hủy đơn chưa trả tiền, không body/header idempotency; gọi lại trả conflict |
| POST | `/api/v1/payments` | Tạo VNPay attempt: `{ "orderId": "..." }`, cần key, trả 201 |
| GET | `/api/v1/payments/{id}` | Trạng thái payment đã lưu, thuộc buyer |
| POST | `/api/v1/payments/{id}/refunds` | ADMIN, `{ "reason": "..." }`, cần key, trả 202 cho intent (chưa phải settlement) |
| GET | `/api/v1/payments/vnpay/ipn` | VNPay authoritative callback, raw `{RspCode,Message}` |
| GET | `/api/v1/payments/vnpay/return` | Xem kết quả, không tự xác nhận paid |

Payment aliases được giữ: POST `/attempts`, `/process`; POST `/{id}/refund`; GET `/callback/vnpay`, cùng base `/api/v1/payments`. Query/đối soát được worker thực hiện, không có endpoint công khai.

```json
{
  "items": [{"productId": "product-uuid", "quantity": 1}],
  "shippingAddress": "123 Street, Hanoi",
  "notes": null
}
```

Checkout trả 201 với `ApiResponse<OrderDTO>` chứa `id`, `orderNumber`, snapshot sản phẩm/giá, `total`, `expiresAt`, `orderStatus`, `paymentStatus`. Legacy API tạo/sửa/xóa/trực tiếp đổi trạng thái order đã loại bỏ; dùng checkout/cancel/payment thay thế. Cart và product API được giữ với kiểm tra ownership/quyền tương ứng.

## Idempotency và delivery

Các command yêu cầu `Idempotency-Key` gồm 1–128 ký tự chữ/số hoặc `._:-`. Key được scope theo buyer và operation. Cùng key + payload trả lại DTO kết quả ban đầu; khác payload trả 409. Claim, business write và DTO replay commit cùng transaction. Không dùng replay này để suy ra trạng thái hiện tại: GET resource để xem trạng thái mới nhất. Không tái dùng key cho command khác; không có cơ chế tự xóa idempotency record.

Outbox delivery retry với backoff, tối đa 10 lần. Inbox và consumer effect commit cùng transaction để tránh lặp tác động local. Event lỗi đạt giới hạn được giữ lại trong `outbox_events`; cần cảnh báo vận hành và xử lý/replay có kiểm soát, chưa có admin dead-letter API. Schema này không thay thế cơ chế dedup của consumer bên ngoài khi tách microservice.

## Kiểm thử

Maven ghi build output vào `target-maven` để tránh xung đột bytecode/MapStruct generated classes với incremental compiler của IDE tại `target`. Không commit build output.

```powershell
mvn test
mvn verify
```

Integration tests dùng PostgreSQL qua Testcontainers (cần Docker). Có thể chạy trên **database riêng dành cho test**, không dùng database production:

```powershell
mvn verify '-Dtest.db.url=jdbc:postgresql://127.0.0.1:55432/orders_test' '-Dtest.db.username=orders_test' '-Dtest.db.password='
```

Test suite kiểm tra concurrency/rollback/idempotency/outbox trên PostgreSQL thật, domain invariants, quyền API và chữ ký VNPay. Test có thể xóa dữ liệu/schema test; không trỏ vào database chứa dữ liệu cần giữ.
