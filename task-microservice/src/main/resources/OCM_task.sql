START TRANSACTION;

INSERT INTO `task` (
                    `id`,
                    `attachment_url`,
                    `created_at`,
                    `deadline`,
                    `description`,
                    `status`,
                    `teacher_id`,
                    `title`) VALUES
    (1,'www.example.com','2025-04-14 23:44:20.939852','2025-05-07 23:59:59.000000','Assignment 1 for team BinaryBrain','OPEN',1,'Assignment 1'),
    (2,'www.example.com','2025-04-14 23:57:13.565781','2025-04-20 23:59:59.000000','Assignment 2 for team BinaryBrain','CLOSED',1,'Assignment 2')
    ON DUPLICATE KEY UPDATE
                         `attachment_url` = VALUES(`attachment_url`),
                         `created_at` = VALUES(`created_at`),
                         `deadline` = VALUES(`deadline`),
                         `description` = VALUES(`description`),
                         `status` = VALUES(`status`),
                         `teacher_id` = VALUES(`teacher_id`),
                         `title` = VALUES(`title`);

COMMIT;