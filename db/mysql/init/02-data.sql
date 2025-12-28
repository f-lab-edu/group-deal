SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

INSERT INTO users (email, password, nickname, created_time, updated_time, created_by, updated_by)
VALUES ('host1@test.com', '$2a$10$FAkvDPjLjmwJhd9o6YDStePThk5E1ZKHNNFFcjKqSFj.VzF6qbWQa', '호스트1', NOW(), NOW(), 'system', 'system'),
       ('member1@test.com', '$2a$10$FAkvDPjLjmwJhd9o6YDStePThk5E1ZKHNNFFcjKqSFj.VzF6qbWQa', '멤버1', NOW(), NOW(), 'system', 'system');

INSERT INTO `groups` (product_name, category, description, original_price, target_participants, divided_unit, recruitment_minutes, deadline_at, meeting_location, meeting_at, host_member_id, host_member_name, status, current_participants, created_time, updated_time, created_by, updated_by)
VALUES ('코스트코 견과류 믹스 3kg', '식품', '신선한 견과류입니다.', 30000, 300, '500g씩', 1440, NOW() + INTERVAL 1 DAY, '강남역 3번 출구', NOW() + INTERVAL 2 DAY, 1, '호스트1', 'RECRUITING', 200, NOW(), NOW(), 'system', 'system');

INSERT INTO group_members (group_id, user_id, nickname, group_member_type, group_member_status, joined_at, created_time, updated_time, created_by, updated_by)
VALUES (1, 1, '호스트1', 'HOST', 'JOINED', NOW(), NOW(), NOW(), 'system', 'system'),
       (1, 2, '멤버1', 'MEMBER', 'JOINED', NOW(), NOW(), NOW(), 'system', 'system');

DELIMITER $$

DROP PROCEDURE IF EXISTS generate_dummy_users$$
CREATE PROCEDURE generate_dummy_users()
BEGIN
  DECLARE i INT DEFAULT 1;
  WHILE i <= 20000 DO
    INSERT INTO users (email, password, nickname, created_time, updated_time, created_by, updated_by)
    VALUES (
      CONCAT('test', i, '@test.com'),
      '$2a$10$FAkvDPjLjmwJhd9o6YDStePThk5E1ZKHNNFFcjKqSFj.VzF6qbWQa',
      CONCAT('테스트', i),
      NOW(),
      NOW(),
      'system',
      'system'
    );
    SET i = i + 1;
END WHILE;
END$$

DELIMITER ;

CALL generate_dummy_users();
DROP PROCEDURE generate_dummy_users;

-- 확인
SELECT CONCAT('Total users: ', COUNT(*)) AS result FROM users;
