package com.bitsunisage.campusconnect.project.dataAccessObject;

import com.bitsunisage.campusconnect.project.entities.SubjectDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubjectDetailsDAO extends JpaRepository<SubjectDetails, String> {
List<SubjectDetails> findBySubjectIdIn(List<Long> subjectIds);
}
