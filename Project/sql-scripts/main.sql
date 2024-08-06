-- Creating the the user for this project -- 
-- This user creation should be run by root user 
create user 'campusConnect'@'localhost' identified by 'Frpsxwhu2001@';
-- Giving all the privileges of the database to the user 
grant all privileges on campusconnect.* to 'campusConnect'@'localhost';
Flush privileges;

-- Now after switching the user the following scripts can be executed

create database if not exists `campusConnect`;
USE `campusConnect`;

DROP TABLE IF EXISTS `roles`;
DROP TABLE IF EXISTS `members`;
--
-- Table structure for table `members`
--

CREATE TABLE `members` (
    `id` INT(20) NOT NULL AUTO_INCREMENT UNIQUE,
    `user_id` VARCHAR(50)  NOT NULL UNIQUE,
    `email` VARCHAR(255) NOT NULL UNIQUE,
    `pw` CHAR(68) NOT NULL,
    `active` TINYINT NOT NULL,
    PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


CREATE TABLE `roles` (

    `user_id` VARCHAR(50)  NOT NULL,
    `role` VARCHAR(50) NOT NULL,
    CONSTRAINT `authorities5_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `members` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


INSERT INTO members  VALUES (1, "Subodh", "subodhadhikari2023@outlook.com", "{noop}test123", 1);
INSERT INTO members  VALUES (2, "Ganesh", "Ganesh@outlook.com", "{noop}test123", 1);
INSERT INTO members  VALUES (3, "Shristi", "Shristi@outlook.com", "{noop}test123", 1);
SELECT id FROM members WHERE user_id = "Subodh";
INSERT INTO roles VALUES("Subodh","ROLE_STUDENT");
INSERT INTO roles VALUES("Ganesh","ROLE_ADMIN");
INSERT INTO roles VALUES("Shristi","ROLE_TEACHER");
show tables;
SELECT * FROM members;
SELECT * FROM roles ;




