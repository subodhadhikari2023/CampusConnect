use campusConnect;
-- Creating department table to store different departments --
DROP TABLE IF EXISTS department;
DROP TABLE IF EXISTS department_details;
CREATE TABLE `department` (
    `department_id` INT(20) NOT NULL AUTO_INCREMENT UNIQUE,
    `department_name` VARCHAR(50)  NOT NULL UNIQUE,
     PRIMARY KEY (`department_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `department_details` (
    `department_member_id` INT(20) NOT NULL AUTO_INCREMENT UNIQUE,
	`department_id` INT(20) NOT NULL, 
    `user_name` VARCHAR(50) NOT NULL UNIQUE,
    `role` VARCHAR(50) NOT NULL,
     PRIMARY KEY (`department_member_id`),
	UNIQUE (`user_name`, `department_id`),
     FOREIGN KEY(`department_id`) REFERENCES department(department_id),
     FOREIGN KEY(`user_name`) REFERENCES members(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
