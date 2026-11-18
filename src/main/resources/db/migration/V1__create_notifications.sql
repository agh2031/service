CREATE TABLE notifications (
  id BIGSERIAL PRIMARY KEY,
  notification_key VARCHAR(128) NOT NULL UNIQUE,
  recipient VARCHAR(320) NOT NULL,
  message_body VARCHAR(4000) NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  attempt_count INTEGER NOT NULL DEFAULT 0,
  next_attempt_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT notifications_status_valid CHECK (status IN ('PENDING', 'SENT')),
  CONSTRAINT notifications_attempt_count_non_negative CHECK (attempt_count >= 0)
);
CREATE INDEX notifications_due_idx ON notifications(status, next_attempt_at, id);
