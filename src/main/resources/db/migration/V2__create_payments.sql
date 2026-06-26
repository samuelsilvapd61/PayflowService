CREATE TABLE payments
(
    id          UUID PRIMARY KEY,
    sender_id   UUID           NOT NULL,
    receiver_id UUID           NOT NULL,
    amount      NUMERIC(19, 2) NOT NULL,
    status      VARCHAR(20)    NOT NULL,
    description VARCHAR(255),
    created_at  TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_payment_sender
        FOREIGN KEY (sender_id)
            REFERENCES customers (id),

    CONSTRAINT fk_payment_receiver
        FOREIGN KEY (receiver_id)
            REFERENCES customers (id)
);