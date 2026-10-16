CREATE TABLE payment_attempts (
 id varchar(255) PRIMARY KEY, version bigint NOT NULL DEFAULT 0,
 order_id varchar(255) NOT NULL, buyer_id varchar(255) NOT NULL,
 amount numeric(19,0) NOT NULL CHECK(amount>0 AND amount<=92233720368547758),
 currency varchar(3) NOT NULL CHECK(currency='VND'),
 transaction_ref varchar(32) NOT NULL UNIQUE,
 created_at timestamptz NOT NULL, expires_at timestamptz NOT NULL,
 status varchar(24) NOT NULL CHECK(status IN ('PENDING','FAILED','SUCCEEDED')),
 provider_transaction_no varchar(255),
 refund_status varchar(24) NOT NULL CHECK(refund_status IN ('NONE','REQUESTED','DISPATCHING','UNCERTAIN','ACCEPTED','REFUNDED','REJECTED','MANUAL_REVIEW')),
 refund_request_id varchar(32) UNIQUE, refund_actor varchar(255), refund_reason varchar(255), refund_created_at timestamptz,
 next_attempt timestamptz NOT NULL, lease_until timestamptz, lease_token varchar(255),
 attempts integer NOT NULL DEFAULT 0 CHECK(attempts>=0), last_outcome varchar(64),
 CHECK(expires_at>created_at),
 CHECK(status<>'SUCCEEDED' OR provider_transaction_no IS NOT NULL),
 CHECK(refund_status='NONE' OR (status='SUCCEEDED' AND refund_request_id IS NOT NULL))
);
CREATE INDEX payment_order_idx ON payment_attempts(order_id);
CREATE INDEX payment_work_idx ON payment_attempts(next_attempt) WHERE attempts<8;
CREATE UNIQUE INDEX payment_provider_no_idx ON payment_attempts(provider_transaction_no) WHERE provider_transaction_no IS NOT NULL;
