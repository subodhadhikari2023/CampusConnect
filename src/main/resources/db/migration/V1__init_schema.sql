-- CampusConnect initial schema — all DDL in FK dependency order.
-- Applied once by Flyway on first boot against an empty database.

CREATE TABLE IF NOT EXISTS `department`
(
    `department_id`   INT         NOT NULL AUTO_INCREMENT,
    `department_name` VARCHAR(50) NOT NULL,
    PRIMARY KEY (`department_id`),
    UNIQUE KEY `department_name` (`department_name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `members`
(
    `id`         INT          NOT NULL AUTO_INCREMENT,
    `user_id`    VARCHAR(50)  NOT NULL,
    `email`      VARCHAR(255) NOT NULL,
    `pw`         CHAR(68)     NOT NULL,
    `active`     TINYINT      NOT NULL,
    `department` VARCHAR(100) NOT NULL,
    `dept_id`    INT,
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `id` (`id`),
    UNIQUE KEY `email` (`email`),
    FOREIGN KEY (`dept_id`) REFERENCES `department` (`department_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `roles`
(
    `user_id` VARCHAR(50) NOT NULL,
    `role`    VARCHAR(50) NOT NULL,
    PRIMARY KEY (`user_id`),
    CONSTRAINT `fk_user_roles` FOREIGN KEY (`user_id`) REFERENCES `members` (`user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `department_details`
(
    `department_member_id` INT         NOT NULL AUTO_INCREMENT,
    `department_id`        INT         NOT NULL,
    `user_name`            VARCHAR(50) NOT NULL,
    `role`                 VARCHAR(50) NOT NULL,
    PRIMARY KEY (`department_member_id`),
    UNIQUE KEY `user_name` (`user_name`),
    KEY `department_id` (`department_id`),
    CONSTRAINT `fk_department` FOREIGN KEY (`department_id`) REFERENCES `department` (`department_id`),
    CONSTRAINT `fk_member`     FOREIGN KEY (`user_name`)     REFERENCES `members`    (`user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `course_details`
(
    `course_id`     INT          NOT NULL AUTO_INCREMENT,
    `course_name`   VARCHAR(100) NOT NULL,
    `department_id` INT          NOT NULL,
    PRIMARY KEY (`course_id`),
    CONSTRAINT `fk_department_course` FOREIGN KEY (`department_id`) REFERENCES `department` (`department_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `semester`
(
    `semester_id`   INT         NOT NULL AUTO_INCREMENT,
    `semester_name` VARCHAR(50) NOT NULL,
    `course_id`     INT         NOT NULL,
    PRIMARY KEY (`semester_id`),
    CONSTRAINT `fk_course_semester` FOREIGN KEY (`course_id`) REFERENCES `course_details` (`course_id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `subject_details`
(
    `subject_id`   INT          NOT NULL AUTO_INCREMENT,
    `subject_name` VARCHAR(100) NOT NULL,
    `course_id`    INT          NOT NULL,
    `semester_id`  INT          NOT NULL,
    PRIMARY KEY (`subject_id`),
    CONSTRAINT `fk_course_subject`   FOREIGN KEY (`course_id`)   REFERENCES `course_details` (`course_id`),
    CONSTRAINT `fk_semester_subject` FOREIGN KEY (`semester_id`) REFERENCES `semester`       (`semester_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `file_data`
(
    `file_id`                INT          NOT NULL AUTO_INCREMENT,
    `file_name`              VARCHAR(255) NOT NULL,
    `file_type`              VARCHAR(100) NOT NULL,
    `file_path`              VARCHAR(255) NOT NULL,
    `file_size`              BIGINT       NOT NULL,
    `uploader_department_id` INT          NOT NULL,
    `uploader_name`          VARCHAR(255) NOT NULL,
    `course_id`              INT          NOT NULL,
    `semester_id`            INT          NOT NULL,
    `subject_id`             INT          NOT NULL,
    `uploaded_at`            TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `file_role`              VARCHAR(255) NOT NULL,
    PRIMARY KEY (`file_id`),
    KEY `department_id` (`uploader_department_id`),
    KEY `course_id` (`course_id`),
    KEY `semester_id` (`semester_id`),
    KEY `subject_id` (`subject_id`),
    CONSTRAINT `fk_department_file` FOREIGN KEY (`uploader_department_id`) REFERENCES `department`      (`department_id`),
    CONSTRAINT `fk_course_file`     FOREIGN KEY (`course_id`)              REFERENCES `course_details`  (`course_id`),
    CONSTRAINT `fk_semester_file`   FOREIGN KEY (`semester_id`)            REFERENCES `semester`        (`semester_id`),
    CONSTRAINT `fk_subject_file`    FOREIGN KEY (`subject_id`)             REFERENCES `subject_details` (`subject_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `announcements`
(
    `id`         INT          NOT NULL AUTO_INCREMENT,
    `dept_id`    INT          NOT NULL,
    `author`     VARCHAR(50)  NOT NULL,
    `title`      VARCHAR(200) NOT NULL,
    `body`       TEXT         NOT NULL,
    `created_at` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `dept_id` (`dept_id`),
    CONSTRAINT `fk_dept_announcement`   FOREIGN KEY (`dept_id`) REFERENCES `department` (`department_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_author_announcement` FOREIGN KEY (`author`)  REFERENCES `members`    (`user_id`)       ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `teacher_subject`
(
    `id`         INT         NOT NULL AUTO_INCREMENT,
    `teacher_id` VARCHAR(50) NOT NULL,
    `subject_id` INT         NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uq_subject` (`subject_id`),
    CONSTRAINT `fk_teacher_ts` FOREIGN KEY (`teacher_id`) REFERENCES `members`         (`user_id`)    ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_subject_ts` FOREIGN KEY (`subject_id`) REFERENCES `subject_details` (`subject_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;
