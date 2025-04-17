START TRANSACTION;

INSERT IGNORE INTO `role` (`id`, `name`) VALUES
                                      (1,'ADMIN'),
                                      (2,'TEACHER'),
                                      (3,'STUDENT');

INSERT IGNORE INTO `user` (
    `id`,
    `country`,
    `current_institute`,
    `email`,
    `first_name`,
    `gender`,
    `last_name`,
    `password`,
    `profile_picture`,
    `username`) VALUES
                    (100,'BD','PUST','test_teacher@gmail.com','Test','Male','Teacher','$2a$10$30.p8EzC9Wp6oPXq6skWwuW1kHkDOxHNmpgqzk3EkWoYVjNBjqp8W',NULL,'test_teacher'),
                    (200,'BD','PUST','test_student@gmail.com','Test','Male','Student','$2a$10$MEY3biCwBk0iULfBaTPaBOWeRwiEGxCWzHLmG69x8I/iEcNDq4ZoK',NULL,'test_student'),
                    (300,'BD','PUST','test_admmin@gmail.com','Test','Male','Admin','$2a$10$Lwu7NQjZAS308HYD90.S5.2KDlk6Eao5wsGVAbqwGVx/WCKeE9tN2',NULL,'test_admin');

INSERT IGNORE INTO `user_roles` (`user_id`, `role_id`) VALUES
                                                    (300,1),
                                                    (100,2),
                                                    (200,3);

COMMIT;