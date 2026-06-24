-- Docker init script — runs automatically on first container startup.
-- CREATE DATABASE and CREATE USER are handled by the MySQL image via env vars;
-- this script only sets up the schema and seed data.

/*!40101 SET @OLD_CHARACTER_SET_CLIENT = @@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS = @@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION = @@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE = @@TIME_ZONE */;
/*!40103 SET TIME_ZONE = '+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0 */;
/*!40101 SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES = @@SQL_NOTES, SQL_NOTES = 0 */;

-- Table structure for table `department`
DROP TABLE IF EXISTS `department`;
CREATE TABLE `department`
(
    `department_id`   INT         NOT NULL AUTO_INCREMENT,
    `department_name` VARCHAR(50) NOT NULL,
    PRIMARY KEY (`department_id`),
    UNIQUE KEY `department_name` (`department_name`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1004
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

INSERT INTO `department`
VALUES (1001, 'Computer Applications'),
       (1002, 'Computer Science'),
       (1003, 'Physics');

-- Table structure for table `members`
DROP TABLE IF EXISTS `members`;
CREATE TABLE `members`
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
  AUTO_INCREMENT = 8
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

INSERT INTO `members`
VALUES (1, 'hod1', 'hod1@campus.com', '{noop}password', 1, 'Computer Applications', 1001),
       (2, 'hod2', 'hod2@campus.com', '{noop}password', 1, 'Computer Science', 1002),
       (3, 'teacher1', 'teacher1@campus.com', '{noop}password', 1, 'Computer Applications', 1001),
       (4, 'teacher2', 'teacher2@campus.com', '{noop}password', 1, 'Physics', 1003),
       (5, 'student1', 'student1@campus.com', '{noop}password', 1, 'Computer Science', 1002),
       (6, 'student2', 'student2@campus.com', '{noop}password', 1, 'Physics', 1003),
       (7, 'admin1', 'admin1@campus.com', '{noop}password', 1, 'Computer Applications', 1001),
       (8, 'admin2', 'admin2@campus.com', '{noop}password', 1, 'Physics', 1003);

-- Table structure for table `roles`
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles`
(
    `user_id` VARCHAR(50) NOT NULL,
    `role`    VARCHAR(50) NOT NULL,
    PRIMARY KEY (`user_id`),
    CONSTRAINT `fk_user_roles` FOREIGN KEY (`user_id`) REFERENCES `members` (`user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

INSERT INTO `roles`
VALUES ('hod1', 'ROLE_HOD'),
       ('hod2', 'ROLE_HOD'),
       ('teacher1', 'ROLE_TEACHER'),
       ('teacher2', 'ROLE_TEACHER'),
       ('student1', 'ROLE_STUDENT'),
       ('student2', 'ROLE_STUDENT'),
       ('admin1', 'ROLE_ADMIN'),
       ('admin2', 'ROLE_ADMIN');

-- Table structure for table `department_details`
DROP TABLE IF EXISTS `department_details`;
CREATE TABLE `department_details`
(
    `department_member_id` INT         NOT NULL AUTO_INCREMENT,
    `department_id`        INT         NOT NULL,
    `user_name`            VARCHAR(50) NOT NULL,
    `role`                 VARCHAR(50) NOT NULL,
    PRIMARY KEY (`department_member_id`),
    UNIQUE KEY `user_name` (`user_name`),
    KEY `department_id` (`department_id`),
    CONSTRAINT `fk_department` FOREIGN KEY (`department_id`) REFERENCES `department` (`department_id`),
    CONSTRAINT `fk_member` FOREIGN KEY (`user_name`) REFERENCES `members` (`user_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 10
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

INSERT INTO `department_details`
VALUES (1, 1001, 'hod1', 'HOD'),
       (2, 1002, 'hod2', 'HOD'),
       (3, 1001, 'teacher1', 'TEACHER'),
       (4, 1003, 'teacher2', 'TEACHER'),
       (5, 1002, 'student1', 'STUDENT'),
       (6, 1003, 'student2', 'STUDENT'),
       (7, 1001, 'admin1', 'ADMIN'),
       (8, 1003, 'admin2', 'ADMIN');

-- Table structure for table `course_details`
DROP TABLE IF EXISTS `course_details`;
CREATE TABLE IF NOT EXISTS `course_details`
(
    `course_id`     INT          NOT NULL AUTO_INCREMENT,
    `course_name`   VARCHAR(100) NOT NULL,
    `department_id` INT          NOT NULL,
    PRIMARY KEY (`course_id`),
    CONSTRAINT `fk_department_course` FOREIGN KEY (`department_id`) REFERENCES `department` (`department_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- Table structure for table `semester`
-- Semesters are now course-scoped: each HOD defines the semesters for their own courses.
DROP TABLE IF EXISTS `semester`;
CREATE TABLE IF NOT EXISTS `semester`
(
    `semester_id`   INT         NOT NULL AUTO_INCREMENT,
    `semester_name` VARCHAR(50) NOT NULL,
    `course_id`     INT         NOT NULL,
    PRIMARY KEY (`semester_id`),
    CONSTRAINT `fk_course_semester` FOREIGN KEY (`course_id`) REFERENCES `course_details` (`course_id`) ON DELETE CASCADE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- Table structure for table `subject_details`
DROP TABLE IF EXISTS `subject_details`;
CREATE TABLE `subject_details`
(
    `subject_id`   INT          NOT NULL AUTO_INCREMENT,
    `subject_name` VARCHAR(100) NOT NULL,
    `course_id`    INT          NOT NULL,
    `semester_id`  INT          NOT NULL,
    PRIMARY KEY (`subject_id`),
    CONSTRAINT `fk_course_subject` FOREIGN KEY (`course_id`) REFERENCES `course_details` (`course_id`),
    CONSTRAINT `fk_semester_subject` FOREIGN KEY (`semester_id`) REFERENCES `semester` (`semester_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- Table structure for table `file_data`
DROP TABLE IF EXISTS `file_data`;
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
    CONSTRAINT `fk_department_file` FOREIGN KEY (`uploader_department_id`) REFERENCES `department` (`department_id`),
    CONSTRAINT `fk_course_file` FOREIGN KEY (`course_id`) REFERENCES `course_details` (`course_id`),
    CONSTRAINT `fk_semester_file` FOREIGN KEY (`semester_id`) REFERENCES `semester` (`semester_id`),
    CONSTRAINT `fk_subject_file` FOREIGN KEY (`subject_id`) REFERENCES `subject_details` (`subject_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

/*!40101 SET SQL_MODE = @OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT = @OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS = @OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION = @OLD_COLLATION_CONNECTION */;
