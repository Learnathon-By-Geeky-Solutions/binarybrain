START TRANSACTION;

INSERT INTO `classroom` (
                         `id`,
                         `description`,
                         `start_date`,
                         `teacher_id`,
                         `title`) VALUES
    (1,'Classroom 1 description','2025-04-14',1,'Classroom 1 ')
    ON DUPLICATE KEY UPDATE
                         `description` = VALUES(`description`),
                         `start_date` = VALUES(`start_date`),
                         `teacher_id` = VALUES(`teacher_id`),
                         `title` = VALUES(`title`);

INSERT INTO `classroom_courses` (`classroom_id`, `course_ids`) VALUES
    (1,2),
    (1,1)
    ON DUPLICATE KEY UPDATE
                         `course_ids` = VALUES(`course_ids`);

INSERT INTO `classroom_students` (`classroom_id`, `student_ids`) VALUES
    (1,2)
    ON DUPLICATE KEY UPDATE
                         `student_ids` = VALUES(`student_ids`);

COMMIT;