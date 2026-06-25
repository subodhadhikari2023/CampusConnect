package com.bitsunisage.campusconnect.repository;

import com.bitsunisage.campusconnect.entities.TeacherSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link TeacherSubject} (the {@code teacher_subject} table).
 * Each row records that one teacher is assigned to teach one subject.
 */
@Repository
public interface TeacherSubjectDAO extends JpaRepository<TeacherSubject, Long> {

    /**
     * Finds all assignments whose subject ID is in the given list.
     *
     * @param subjectIds list of subject primary keys to query
     * @return list of matching assignments; empty if none
     */
    List<TeacherSubject> findBySubjectIdIn(List<Long> subjectIds);

    /**
     * Finds the assignment for a specific subject, if one exists.
     *
     * @param subjectId subject primary key
     * @return an {@link Optional} containing the assignment, or empty if unassigned
     */
    Optional<TeacherSubject> findBySubjectId(Long subjectId);

    /**
     * Deletes the assignment for the given subject.
     * No-op if no assignment exists for that subject.
     *
     * @param subjectId subject primary key
     */
    void deleteBySubjectId(Long subjectId);

    /**
     * Returns all assignments where the teacher is one of the given user IDs.
     * Used to build a per-teacher workload summary.
     *
     * @param teacherIds list of teacher login usernames
     * @return list of matching assignments; empty if none
     */
    List<TeacherSubject> findByTeacherIdIn(List<String> teacherIds);
}
