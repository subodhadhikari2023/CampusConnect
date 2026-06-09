package com.bitsunisage.campusconnect.project.dataAccessObject;

import com.bitsunisage.campusconnect.project.entities.SubjectDetails;
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
}
