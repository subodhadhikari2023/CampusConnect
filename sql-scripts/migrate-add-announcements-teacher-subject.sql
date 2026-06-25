-- Migration: add announcements and teacher_subject tables
-- Safe to run on an existing campusConnect database — uses IF NOT EXISTS.

USE `campusConnect`;

-- Announcements: department-scoped notices posted by HODs.
-- author and title use latin1 to match the existing members/subject_details tables.
CREATE TABLE IF NOT EXISTS `announcements`
(
    `id`         INT                         NOT NULL AUTO_INCREMENT,
    `dept_id`    INT                         NOT NULL,
    `author`     VARCHAR(50)  CHARACTER SET latin1 NOT NULL,
    `title`      VARCHAR(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `body`       TEXT         CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `created_at` TIMESTAMP                   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `dept_id` (`dept_id`),
    CONSTRAINT `fk_dept_announcement`   FOREIGN KEY (`dept_id`) REFERENCES `department` (`department_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_author_announcement` FOREIGN KEY (`author`)  REFERENCES `members`    (`user_id`)       ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;

-- Teacher-subject assignments: one teacher per subject (UNIQUE on subject_id).
-- teacher_id uses latin1 to match members.user_id.
CREATE TABLE IF NOT EXISTS `teacher_subject`
(
    `id`         INT                        NOT NULL AUTO_INCREMENT,
    `teacher_id` VARCHAR(50) CHARACTER SET latin1 NOT NULL,
    `subject_id` INT                        NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uq_subject` (`subject_id`),
    CONSTRAINT `fk_teacher_ts` FOREIGN KEY (`teacher_id`) REFERENCES `members`         (`user_id`)    ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_subject_ts` FOREIGN KEY (`subject_id`) REFERENCES `subject_details` (`subject_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;
