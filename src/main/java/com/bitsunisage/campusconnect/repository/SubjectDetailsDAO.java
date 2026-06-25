package com.bitsunisage.campusconnect.repository;

import com.bitsunisage.campusconnect.entities.SubjectDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for {@link SubjectDetails} (the {@code subject_details} table).
 * Each subject is linked to exactly one course and one semester.
 */
@Repository
public interface SubjectDetailsDAO extends JpaRepository<SubjectDetails, String> {

    /**
     * Returns all subjects whose IDs are in the given list.
     *
     * @param subjectIds list of subject IDs to fetch
     * @return list of matching {@link SubjectDetails}; may be empty
     */
    List<SubjectDetails> findBySubjectIdIn(List<Long> subjectIds);

    /**
     * Returns all subjects belonging to the given course.
     *
     * @param courseId the course primary key
     * @return list of matching {@link SubjectDetails}; empty if none exist
     */
    List<SubjectDetails> findByCourseId(int courseId);

    /**
     * Deletes a subject by its primary key.
     *
     * @param subjectId the subject ID to delete
     */
    void deleteBySubjectId(Long subjectId);

    /**
     * Returns a single subject by its primary key.
     *
     * @param subjectId the subject primary key
     * @return the matching {@link SubjectDetails}, or {@code null} if not found
     */
    SubjectDetails findBySubjectId(Long subjectId);

    /**
     * Counts all subjects assigned to the given semester.
     *
     * @param semesterId the semester primary key
     * @return count of subjects in that semester
     */
    int countBySemesterId(int semesterId);

    /**
     * Returns {@code true} if a subject with the given name already exists in the given course and semester.
     * Used to prevent duplicate subject names within the same course-semester slot.
     *
     * @param courseId    course primary key
     * @param semesterId  semester primary key
     * @param subjectName name to test (case-sensitive)
     * @return {@code true} if a matching subject exists
     */
    boolean existsByCourseIdAndSemesterIdAndSubjectName(int courseId, int semesterId, String subjectName);

    /**
     * Counts all subjects belonging to the given course.
     *
     * @param courseId course primary key
     * @return number of subjects in that course
     */
    int countByCourseId(int courseId);
}
