package com.bitsunisage.campusconnect.project.dataAccessObject;

import com.bitsunisage.campusconnect.project.entities.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for {@link Semester} (the {@code semester} table).
 * Semesters are global — they are not scoped to a particular department or course.
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
}
