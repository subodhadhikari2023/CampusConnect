package com.bitsunisage.campusconnect.project.dataAccessObject;

import com.bitsunisage.campusconnect.project.entities.CourseDetails;
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
}
