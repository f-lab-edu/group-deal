SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(255) UNIQUE,
    created_time DATETIME,
    updated_time DATETIME,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `groups` (
    group_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    description VARCHAR(500),
    original_price INT NOT NULL,
    target_participants INT NOT NULL,
    divided_unit VARCHAR(100),
    recruitment_minutes INT NOT NULL,
    deadline_at DATETIME NOT NULL,
    meeting_location VARCHAR(255) NOT NULL,
    meeting_at DATETIME NOT NULL,
    host_member_id BIGINT NOT NULL,
    host_member_name VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    current_participants INT NOT NULL,
    created_time DATETIME,
    updated_time DATETIME,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS group_members (
    group_member_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    group_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    nickname VARCHAR(255),
    group_member_type VARCHAR(20) NOT NULL,
    group_member_status VARCHAR(20) NOT NULL,
    queue_number INT DEFAULT NULL COMMENT '참여 순번',
    joined_at DATETIME NOT NULL,
    left_at DATETIME,
    created_time DATETIME,
    updated_time DATETIME,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    CONSTRAINT uk_group_user_status UNIQUE (group_id, user_id, group_member_status),
    CONSTRAINT uk_group_queue_number UNIQUE (group_id, queue_number),
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;