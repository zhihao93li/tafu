-- Protocol: PostgreSQL
-- Generated from Prisma Schema

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Users Table
CREATE TABLE users (
    id VARCHAR(191) PRIMARY KEY,
    phone VARCHAR(191) UNIQUE,
    username VARCHAR(191) UNIQUE,
    password_hash VARCHAR(191),
    created_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Points Account
CREATE TABLE points_accounts (
    id VARCHAR(191) PRIMARY KEY,
    user_id VARCHAR(191) NOT NULL UNIQUE,
    balance INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_points FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Points Transactions
CREATE TABLE points_transactions (
    id VARCHAR(191) PRIMARY KEY,
    user_id VARCHAR(191) NOT NULL,
    type VARCHAR(191) NOT NULL, -- recharge, consume, gift
    amount INTEGER NOT NULL,
    balance INTEGER NOT NULL,
    description VARCHAR(191) NOT NULL,
    order_id VARCHAR(191),
    created_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_transactions FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
CREATE INDEX idx_transactions_user_created ON points_transactions(user_id, created_at);

-- Payment Orders
CREATE TABLE payment_orders (
    id VARCHAR(191) PRIMARY KEY,
    user_id VARCHAR(191) NOT NULL,
    order_no VARCHAR(191) NOT NULL UNIQUE,
    amount INTEGER NOT NULL,
    points INTEGER NOT NULL,
    payment_method VARCHAR(191) NOT NULL,
    status VARCHAR(191) NOT NULL DEFAULT 'pending',
    transaction_id VARCHAR(191),
    stripe_session_id VARCHAR(191),
    created_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    paid_at TIMESTAMP(3),
    CONSTRAINT fk_user_orders FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
CREATE INDEX idx_orders_user_created ON payment_orders(user_id, created_at);
CREATE INDEX idx_orders_orderno ON payment_orders(order_no);
CREATE INDEX idx_orders_stripesession ON payment_orders(stripe_session_id);

-- Subjects
CREATE TABLE subjects (
    id VARCHAR(191) PRIMARY KEY,
    user_id VARCHAR(191) NOT NULL,
    name VARCHAR(191) NOT NULL,
    gender VARCHAR(191) NOT NULL,
    calendar_type VARCHAR(191) NOT NULL,
    birth_year INTEGER NOT NULL,
    birth_month INTEGER NOT NULL,
    birth_day INTEGER NOT NULL,
    birth_hour INTEGER NOT NULL,
    birth_minute INTEGER NOT NULL,
    is_leap_month BOOLEAN NOT NULL DEFAULT false,
    location VARCHAR(191) NOT NULL,
    bazi_data JSONB,
    relationship VARCHAR(191),
    note VARCHAR(191),
    created_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    initial_analysis JSONB,
    initial_analyzed_at TIMESTAMP(3),
    CONSTRAINT fk_user_subjects FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
CREATE INDEX idx_subjects_user_created ON subjects(user_id, created_at);

-- Verification Codes
CREATE TABLE verification_codes (
    id VARCHAR(191) PRIMARY KEY,
    phone VARCHAR(191) NOT NULL,
    code VARCHAR(191) NOT NULL,
    expires_at TIMESTAMP(3) NOT NULL,
    attempts INTEGER NOT NULL DEFAULT 0,
    used BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id VARCHAR(191),
    CONSTRAINT fk_user_codes FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
CREATE INDEX idx_codes_phone_created ON verification_codes(phone, created_at);

-- Fortune Reports
CREATE TABLE fortune_reports (
    id VARCHAR(191) PRIMARY KEY,
    user_id VARCHAR(191) NOT NULL,
    subject_id VARCHAR(191),
    birth_info JSONB NOT NULL,
    bazi_chart JSONB NOT NULL,
    analysis JSONB NOT NULL,
    points_cost INTEGER NOT NULL,
    created_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP(3),
    CONSTRAINT fk_user_reports FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_subject_reports FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE
);
CREATE INDEX idx_reports_user_created ON fortune_reports(user_id, created_at);
CREATE INDEX idx_reports_subject_created ON fortune_reports(subject_id, created_at);

-- Theme Analyses
CREATE TABLE theme_analyses (
    id VARCHAR(191) PRIMARY KEY,
    user_id VARCHAR(191) NOT NULL,
    subject_id VARCHAR(191) NOT NULL,
    theme VARCHAR(191) NOT NULL,
    content JSONB NOT NULL,
    points_cost INTEGER NOT NULL,
    created_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_theme_analyses FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_subject_theme_analyses FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE,
    CONSTRAINT uq_theme_subject UNIQUE (subject_id, theme)
);
CREATE INDEX idx_theme_analyses_user_subject ON theme_analyses(user_id, subject_id);

-- Tasks
CREATE TABLE tasks (
    id VARCHAR(191) PRIMARY KEY,
    type VARCHAR(191) NOT NULL,
    status VARCHAR(191) NOT NULL DEFAULT 'pending',
    payload JSONB NOT NULL,
    result TEXT,
    error TEXT,
    user_id VARCHAR(191) NOT NULL,
    created_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    started_at TIMESTAMP(3),
    completed_at TIMESTAMP(3),
    CONSTRAINT fk_user_tasks FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE -- Assuming user relation exists based on context, otherwise remove Constraint
);
CREATE INDEX idx_tasks_user_created ON tasks(user_id, created_at);
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_tasks_type_status ON tasks(type, status);

-- Admins
CREATE TABLE admins (
    id VARCHAR(191) PRIMARY KEY,
    username VARCHAR(191) NOT NULL UNIQUE,
    password_hash VARCHAR(191) NOT NULL,
    role VARCHAR(191) NOT NULL DEFAULT 'admin',
    is_active BOOLEAN NOT NULL DEFAULT true,
    last_login_at TIMESTAMP(3),
    created_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Theme Pricing (Static Data Support)
CREATE TABLE theme_pricing (
    id VARCHAR(191) PRIMARY KEY,
    theme VARCHAR(191) NOT NULL UNIQUE,
    name VARCHAR(191) NOT NULL,
    description VARCHAR(191),
    price INTEGER NOT NULL,
    original_price INTEGER,
    is_active BOOLEAN NOT NULL DEFAULT true,
    sort_order INTEGER NOT NULL DEFAULT 0
);

-- Points Packages (Static Data Support)
CREATE TABLE points_packages (
    id VARCHAR(191) PRIMARY KEY,
    name VARCHAR(191) NOT NULL,
    points INTEGER NOT NULL,
    price INTEGER NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    sort_order INTEGER NOT NULL DEFAULT 0
);
