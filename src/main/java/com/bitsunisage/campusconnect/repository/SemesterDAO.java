package com.bitsunisage.campusconnect.repository;

import com.bitsunisage.campusconnect.entities.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for {@link Semester} (the {@code semester} table).
 * Semesters are course-scoped — each course has its own set of semesters
 * defined by the HOD responsible for that course.
 */
@Repository
public interface SemesterDAO extends JpaRepository<Semester, Integer> {

    /**
     * Returns all semesters whose IDs are in the given list.
     *
     * @param semesterIds list of semester IDs to fetch
     * @return list of matching {@link Semester} entities; may be empty
     */
    List<Semester> findBySemesterIdIn(List<Long> semesterIds);

    /**
     * Returns all semesters belonging to the given course, ordered by ID.
     *
     * @param courseId the course primary key
     * @return list of semesters for that course; empty if none defined
     */
    List<Semester> findByCourseIdOrderBySemesterIdAsc(Long courseId);

    /**
     * Deletes a semester by its primary key.
     *
     * @param semesterId the semester ID to delete
     */
    void deleteBySemesterId(Long semesterId);
}
