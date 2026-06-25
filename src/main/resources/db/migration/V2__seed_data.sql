-- CampusConnect seed data — all dummy records for local/Docker development.
-- Passwords stored as {noop}password (plain-text dev only; never use in production).

INSERT INTO `department` VALUES
    (1001, 'Computer Applications'),
    (1002, 'Computer Science'),
    (1003, 'Physics');

INSERT INTO `members` VALUES
    (1, 'hod1',     'hod1@campus.com',     '{noop}password', 1, 'Computer Applications', 1001),
    (2, 'hod2',     'hod2@campus.com',     '{noop}password', 1, 'Computer Science',      1002),
    (3, 'teacher1', 'teacher1@campus.com', '{noop}password', 1, 'Computer Applications', 1001),
    (4, 'teacher2', 'teacher2@campus.com', '{noop}password', 1, 'Physics',               1003),
    (5, 'student1', 'student1@campus.com', '{noop}password', 1, 'Computer Science',      1002),
    (6, 'student2', 'student2@campus.com', '{noop}password', 1, 'Physics',               1003),
    (7, 'admin1',   'admin1@campus.com',   '{noop}password', 1, 'Computer Applications', 1001),
    (8, 'admin2',   'admin2@campus.com',   '{noop}password', 1, 'Physics',               1003);

INSERT INTO `roles` VALUES
    ('hod1',     'ROLE_HOD'),
    ('hod2',     'ROLE_HOD'),
    ('teacher1', 'ROLE_TEACHER'),
    ('teacher2', 'ROLE_TEACHER'),
    ('student1', 'ROLE_STUDENT'),
    ('student2', 'ROLE_STUDENT'),
    ('admin1',   'ROLE_ADMIN'),
    ('admin2',   'ROLE_ADMIN');

INSERT INTO `department_details` VALUES
    (1, 1001, 'hod1',     'HOD'),
    (2, 1002, 'hod2',     'HOD'),
    (3, 1001, 'teacher1', 'TEACHER'),
    (4, 1003, 'teacher2', 'TEACHER'),
    (5, 1002, 'student1', 'STUDENT'),
    (6, 1003, 'student2', 'STUDENT'),
    (7, 1001, 'admin1',   'ADMIN'),
    (8, 1003, 'admin2',   'ADMIN');

-- Courses: one per department (HODs can add more at runtime)
INSERT INTO `course_details` (`course_name`, `department_id`) VALUES
    ('Bachelor of Computer Applications (BCA)',         1001),
    ('Bachelor of Science in Computer Science (BSc CS)',1002),
    ('Bachelor of Science in Physics (BSc Physics)',    1003);
-- Auto-assigned course_id: 1 = BCA, 2 = BSc CS, 3 = BSc Physics

-- Semesters: 6 per course
INSERT INTO `semester` (`semester_name`, `course_id`) VALUES
    -- BCA (course_id=1): semester_id 1-6
    ('Semester I',   1), ('Semester II',  1), ('Semester III', 1),
    ('Semester IV',  1), ('Semester V',   1), ('Semester VI',  1),
    -- BSc CS (course_id=2): semester_id 7-12
    ('Semester I',   2), ('Semester II',  2), ('Semester III', 2),
    ('Semester IV',  2), ('Semester V',   2), ('Semester VI',  2),
    -- BSc Physics (course_id=3): semester_id 13-18
    ('Semester I',   3), ('Semester II',  3), ('Semester III', 3),
    ('Semester IV',  3), ('Semester V',   3), ('Semester VI',  3);

-- Subjects
INSERT INTO `subject_details` (`subject_name`, `course_id`, `semester_id`) VALUES
    -- BCA Sem I (course=1, sem=1)
    ('Programming Fundamentals in C', 1, 1),
    ('Mathematics I',                 1, 1),
    ('English Communication',         1, 1),
    ('Computer Fundamentals',         1, 1),
    -- BCA Sem II (course=1, sem=2)
    ('Data Structures',               1, 2),
    ('Mathematics II',                1, 2),
    ('Database Management',           1, 2),
    ('Web Technologies',              1, 2),
    -- BCA Sem III (course=1, sem=3)
    ('Object-Oriented Programming with Java', 1, 3),
    ('Operating Systems',                     1, 3),
    ('Computer Networks',                     1, 3),
    ('Software Engineering',                  1, 3),
    -- BSc CS Sem I (course=2, sem=7)
    ('C Programming',          2, 7),
    ('Discrete Mathematics',   2, 7),
    ('Computer Organization',  2, 7),
    ('English for Science',    2, 7),
    -- BSc CS Sem II (course=2, sem=8)
    ('Data Structures and Algorithms', 2, 8),
    ('Digital Electronics',            2, 8),
    ('Probability and Statistics',     2, 8),
    ('Python Programming',             2, 8),
    -- BSc Physics Sem I (course=3, sem=13)
    ('Classical Mechanics',  3, 13),
    ('Thermodynamics',       3, 13),
    ('Mathematical Physics', 3, 13),
    ('Electronics Basics',   3, 13),
    -- BSc Physics Sem II (course=3, sem=14)
    ('Electromagnetism',     3, 14),
    ('Waves and Optics',     3, 14),
    ('Quantum Mechanics I',  3, 14),
    ('Statistical Mechanics',3, 14);

-- Announcements
INSERT INTO `announcements` (`dept_id`, `author`, `title`, `body`) VALUES
    (1001, 'hod1', 'Welcome to the New Academic Year',
     'Welcome back, everyone! The new academic year begins this Monday. Please ensure all course materials are uploaded to CampusConnect by the end of the first week.'),
    (1001, 'hod1', 'Internal Examination Schedule',
     'Internal examinations for BCA Semester I will be held from the 3rd week of this month. Teachers are requested to share question banks with students at least one week before the exam.'),
    (1002, 'hod2', 'Lab Schedule Update',
     'The Computer Science lab sessions have been rescheduled. New timetables are available on the notice board. Please check with your faculty for updated slot assignments.');

-- Teacher-subject assignments
INSERT INTO `teacher_subject` (`teacher_id`, `subject_id`) VALUES
    ('teacher1', 1),   -- Programming Fundamentals in C
    ('teacher1', 2),   -- Mathematics I
    ('teacher1', 5),   -- Data Structures
    ('teacher1', 7),   -- Database Management
    ('teacher1', 9),   -- Object-Oriented Programming with Java
    ('teacher1', 11),  -- Computer Networks
    ('teacher2', 21),  -- Classical Mechanics
    ('teacher2', 22),  -- Thermodynamics
    ('teacher2', 25),  -- Electromagnetism
    ('teacher2', 26);  -- Waves and Optics
