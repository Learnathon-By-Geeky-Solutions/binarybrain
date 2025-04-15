START TRANSACTION;

INSERT INTO `course` (
                      `id`,
                      `code`,
                      `created_by`,
                      `description`,
                      `status`,
                      `title`) VALUES
    (1,'dbms',1,'Learn DMBS',0,'Learn DBMS'),
    (2,'java',1,'Learn JAVA',0,'Learn Spring Boot')
    ON DUPLICATE KEY UPDATE
                         `code` = VALUES(`code`),
                         `created_by` = VALUES(`created_by`),
                         `description` = VALUES(`description`),
                         `status` = VALUES(`status`),
                         `title` = VALUES(`title`);

INSERT INTO `course_tasks` (`course_id`, `task_ids`) VALUES
    (1,1),
    (1,2)
    ON DUPLICATE KEY UPDATE
                         `task_ids` = VALUES(`task_ids`);

COMMIT;