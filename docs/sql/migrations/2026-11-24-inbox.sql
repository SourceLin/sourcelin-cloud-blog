CREATE TABLE IF NOT EXISTS b_inbox_event (
  id BIGINT NOT NULL AUTO_INCREMENT,
  recipient_id BIGINT NOT NULL,
  actor_id BIGINT NOT NULL,
  channel VARCHAR(16) NOT NULL,
  event_type VARCHAR(16) NOT NULL,
  target_type VARCHAR(16) NOT NULL,
  target_id BIGINT NOT NULL,
  source_id BIGINT NOT NULL,
  is_read TINYINT NOT NULL DEFAULT 0,
  active TINYINT NOT NULL DEFAULT 1,
  create_time DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (id),
  UNIQUE KEY uk_inbox_source (recipient_id, actor_id, channel, target_type, source_id),
  KEY idx_inbox_page (recipient_id, channel, active, create_time, id),
  KEY idx_inbox_unread (recipient_id, active, is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
