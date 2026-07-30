CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255),
    password_hash VARCHAR(255)
);

CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,

    user_id BIGINT NOT NULL,

    category VARCHAR(255) NOT NULL,

    url TEXT NOT NULL,

    og_image VARCHAR(255),

    og_title VARCHAR(255),

    price INTEGER,

    is_sold_out BOOLEAN DEFAULT FALSE,

    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP

    -- CONSTRAINT fk_products_user
    --     FOREIGN KEY (user_id)
    --     REFERENCES users(id)
    -- 제약조건은 개념적으로만 -> 변경비용/장애전파/레이턴시를 고려한 트레이드 오프
);