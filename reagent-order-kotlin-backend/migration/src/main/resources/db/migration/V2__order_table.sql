CREATE TABLE user_order (
  id BIGINT PRIMARY KEY,
  app_user_id VARCHAR(50),
  title VARCHAR(50),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  deleted_at TIMESTAMP,
  FOREIGN KEY (app_user_id) REFERENCES app_user(id) ON DELETE CASCADE
);

CREATE TABLE order_detail (
  id BIGINT PRIMARY KEY,
  reagent_name VARCHAR(50),
  url TEXT,
  count INT,
  status VARCHAR(30) CHECK (status IN ('pending', 'completed', 'cancelled')),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP,
  deleted_at TIMESTAMP
);

CREATE TABLE order_set (
  id BIGINT PRIMARY KEY,
  order_id BIGINT,
  order_detail_id BIGINT,
  FOREIGN KEY (order_id) REFERENCES user_order(id) ON DELETE CASCADE,
  FOREIGN KEY (order_detail_id) REFERENCES order_detail(id) ON DELETE CASCADE,
  UNIQUE (order_id, order_detail_id)
);

CREATE INDEX idx_user_order_app_user_id ON user_order(app_user_id);

-- 各テーブルにソフトデリート用インデックスを追加
CREATE INDEX idx_app_user_not_deleted ON app_user(deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX idx_user_order_not_deleted ON user_order(deleted_at) WHERE deleted_at IS NULL;
CREATE INDEX idx_order_detail_not_deleted ON order_detail(deleted_at) WHERE deleted_at IS NULL;
