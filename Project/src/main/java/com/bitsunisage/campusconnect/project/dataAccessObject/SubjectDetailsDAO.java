package com.bitsunisage.campusconnect.project.dataAccessObject;

import com.bitsunisage.campusconnect.project.entities.SubjectDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface SubjectDetailsDAO extends JpaRepository<SubjectDetails, String> {
List<SubjectDetails> findBySubjectIdIn(List<Long> subjectIds);
}
