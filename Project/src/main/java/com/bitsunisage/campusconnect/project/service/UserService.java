package com.bitsunisage.campusconnect.project.service;

import com.bitsunisage.campusconnect.project.entities.*;

import java.util.List;

/**
 * Service interface for all user and academic-catalogue operations.
 * Abstracts persistence details from the controllers; implemented by
 * {@link userServiceImplementation}.
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
}
