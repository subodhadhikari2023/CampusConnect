package com.bitsunisage.campusconnect.repository;

import com.bitsunisage.campusconnect.entities.CourseDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for {@link CourseDetails} (the {@code course_details} table).
 * Each course belongs to one department.
 */
@Repository
public interface CourseDetailsDAO extends JpaRepository<CourseDetails, Integer> {

    /**
     * Returns all courses that belong to the given department.
     *
     * @param departmentId the department's primary key
     * @return list of courses; empty list if none exist for this department
     */
    List<CourseDetails> findByDepartmentId(Long departmentId);

    /**
     * Returns all courses whose IDs are in the given list.
     *
     * @param courseId list of course IDs to fetch
     * @return list of matching {@link CourseDetails}; may be empty
     */
    List<CourseDetails> findByCourseIdIn(List<Long> courseId);

    /**
     * Looks up a single course by its primary key.
     *
     * @param courseId the course ID
     * @return the matching {@link CourseDetails}, or {@code null} if not found
     */
    CourseDetails findByCourseId(Long courseId);

    /**
     * Deletes a course by its primary key.
     *
     * @param courseId the course ID to delete
     */
    void deleteByCourseId(Long courseId);

    /**
     * Returns {@code true} if a course with the given name already exists in the given department.
     * Used to prevent duplicate course names within a department.
     *
     * @param departmentId department to scope the check to
     * @param courseName   name to test (case-sensitive)
     * @return {@code true} if a matching course exists
     */
    boolean existsByDepartmentIdAndCourseName(Long departmentId, String courseName);
}
