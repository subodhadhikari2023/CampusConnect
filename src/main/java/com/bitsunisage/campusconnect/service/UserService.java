package com.bitsunisage.campusconnect.service;

import com.bitsunisage.campusconnect.entities.*;

import java.util.List;

/**
 * Service interface for all user and academic-catalogue operations.
 * Abstracts persistence details from the controllers; implemented by
 * {@link UserServiceImplementation}.
 */
public interface UserService {

    /**
     * Returns every registered user regardless of role.
     *
     * @return list of all {@link User} records; empty list if none exist
     */
    List<User> findAllUsers();

    /**
     * Returns every role record in the system.
     *
     * @return list of all {@link Roles} records; empty list if none exist
     */
    List<Roles> findAllRoles();

    /**
     * Looks up a user by their login username.
     *
     * @param userId login username
     * @return the matching {@link User}, or {@code null} if not found
     */
    User findUserByUserId(String userId);

    /**
     * Looks up the role record for a given user.
     *
     * @param userId login username
     * @return the {@link Roles} record, or {@code null} if not found
     */
    Roles findRoleByUserId(String userId);

    /**
     * Persists a new or updated user record.
     *
     * @param user the {@link User} to save
     * @return the saved entity (may have generated ID populated)
     */
    User save(User user);

    /**
     * Persists a new or updated role record.
     *
     * @param roles the {@link Roles} to save
     * @return the saved entity
     */
    Roles save(Roles roles);

    /**
     * Deletes a user from the {@code members} table.
     * The caller must delete the user's {@link Roles} record first to satisfy the FK constraint.
     *
     * @param user the {@link User} to delete
     */
    void deleteUser(User user);

    /**
     * Deletes a role record from the {@code roles} table.
     *
     * @param roles the {@link Roles} to delete
     */
    void deleteRole(Roles roles);

    /**
     * Returns the total number of registered users across all roles.
     *
     * @return count of all users
     */
    Integer totalUsers();

    /**
     * Returns the number of users holding a specific role.
     *
     * @param role Spring Security role string, e.g. {@code "ROLE_STUDENT"}
     * @return count of users with that role
     */
    Integer totalUsers(String role);

    /**
     * Returns all role records for users that hold the specified role.
     *
     * @param role Spring Security role string, e.g. {@code "ROLE_TEACHER"}
     * @return list of matching {@link Roles} records; empty list if none
     */
    List<Roles> findByRole(String role);

    /**
     * Returns all departments in the system.
     *
     * @return list of all {@link Department} records; empty list if none
     */
    List<Department> getAllDepartments();

    /**
     * Returns all courses across all departments.
     *
     * @return list of all {@link CourseDetails} records; empty list if none
     */
    List<CourseDetails> getAllCourses();

    /**
     * Returns all semesters in the system.
     *
     * @return list of all {@link Semester} records; empty list if none
     */
    List<Semester> getAllSemesters();

    /**
     * Returns all semesters belonging to the given course, ordered by ID.
     *
     * @param courseId the course primary key
     * @return list of semesters for that course; empty if none defined
     */
    List<Semester> getSemestersByCourseId(Long courseId);

    /**
     * Returns all subjects across all courses and semesters.
     *
     * @return list of all {@link SubjectDetails} records; empty list if none
     */
    List<SubjectDetails> getAllSubjects();

    /**
     * Looks up a department by its display name.
     *
     * @param name the department name
     * @return the matching {@link Department}, or {@code null} if not found
     */
    Department getDepartmentIdByDepartmentName(String name);

    /**
     * Looks up a department by its numeric ID.
     *
     * @param id numeric department ID
     * @return the matching {@link Department}, or {@code null} if not found
     */
    Department getDepartmentNameByDepartmentId(Integer id);

    /**
     * Returns courses matching the given list of IDs.
     *
     * @param id list of course IDs to fetch
     * @return list of matching {@link CourseDetails}; may be empty
     */
    List<CourseDetails> getCourseName(List<Long> id);

    /**
     * Returns semesters matching the given list of IDs.
     *
     * @param semesterIds list of semester IDs to fetch
     * @return list of matching {@link Semester}; may be empty
     */
    List<Semester> getSemesterName(List<Long> semesterIds);

    /**
     * Returns subjects matching the given list of IDs.
     *
     * @param subjectIds list of subject IDs to fetch
     * @return list of matching {@link SubjectDetails}; may be empty
     */
    List<SubjectDetails> getSubjectName(List<Long> subjectIds);

    /**
     * Returns departments matching the given list of IDs.
     *
     * @param deptIds list of department IDs to fetch
     * @return list of matching {@link Department}; may be empty
     */
    List<Department> getDepartmentNames(List<Long> deptIds);

    /**
     * Returns all courses belonging to the given department.
     *
     * @param deptId department ID
     * @return list of courses; empty if none exist
     */
    List<CourseDetails> getCoursesByDepartmentId(Long deptId);

    /**
     * Persists a new or updated course record.
     *
     * @param course the {@link CourseDetails} to save
     * @return the saved entity
     */
    CourseDetails saveCourse(CourseDetails course);

    /**
     * Looks up a course by its primary key.
     *
     * @param courseId the course ID
     * @return the matching {@link CourseDetails}, or {@code null} if not found
     */
    CourseDetails getCourseById(Long courseId);

    /**
     * Deletes a course by its primary key.
     *
     * @param courseId the course ID to delete
     */
    void deleteCourseById(Long courseId);

    /**
     * Returns all subjects belonging to the given course.
     *
     * @param courseId course ID
     * @return list of subjects; empty if none exist
     */
    List<SubjectDetails> getSubjectsByCourseId(int courseId);

    /**
     * Persists a new or updated subject record.
     *
     * @param subject the {@link SubjectDetails} to save
     * @return the saved entity
     */
    SubjectDetails saveSubject(SubjectDetails subject);

    /**
     * Looks up a subject by its primary key.
     *
     * @param subjectId the subject ID
     * @return the matching {@link SubjectDetails}, or {@code null} if not found
     */
    SubjectDetails getSubjectById(Long subjectId);

    /**
     * Deletes a subject by its primary key.
     *
     * @param subjectId the subject ID to delete
     */
    void deleteSubjectById(Long subjectId);

    /**
     * Counts members in a department that hold the given role label.
     *
     * @param deptId department ID
     * @param role   role label as stored in {@code department_details} (e.g. {@code "TEACHER"})
     * @return count of matching members
     */
    int countMembersByDepartmentAndRole(Long deptId, String role);

    /**
     * Persists a new or updated department-details membership record.
     *
     * @param details the {@link DepartmentDetails} to save
     * @return the saved entity with generated ID populated
     */
    DepartmentDetails saveDepartmentDetails(DepartmentDetails details);

    /**
     * Deletes the department-details record for the given user.
     * Must be called before deleting the user to satisfy FK constraints.
     *
     * @param userName login username whose membership record should be removed
     */
    void deleteDepartmentDetailsByUserName(String userName);

    /**
     * Persists a new or updated department record.
     *
     * @param dept the {@link Department} to save
     * @return the saved entity (ID populated for new records)
     */
    Department saveDepartment(Department dept);

    /**
     * Deletes a department record.
     *
     * @param dept the {@link Department} to delete
     */
    void deleteDepartment(Department dept);

    /**
     * Counts all members assigned to the given department.
     *
     * @param deptId department primary key
     * @return count of members in that department
     */
    int countMembersByDepartment(Long deptId);

    /**
     * Persists a new or updated semester record.
     *
     * @param semester the {@link Semester} to save
     * @return the saved entity (ID populated for new records)
     */
    Semester saveSemester(Semester semester);

    /**
     * Deletes a semester record.
     *
     * @param semester the {@link Semester} to delete
     */
    void deleteSemester(Semester semester);

    /**
     * Looks up a semester by its primary key.
     *
     * @param semesterId the semester ID
     * @return the matching {@link Semester}, or {@code null} if not found
     */
    Semester getSemesterById(Long semesterId);

    /**
     * Counts all subjects assigned to the given semester.
     *
     * @param semesterId the semester primary key
     * @return count of subjects in that semester
     */
    int countSubjectsBySemester(Long semesterId);
}
