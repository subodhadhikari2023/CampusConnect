package com.bitsunisage.campusconnect.project.dataAccessObject;

import com.bitsunisage.campusconnect.project.entities.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface SemesterDAO extends JpaRepository<Semester, Integer> {
    List<Semester> findBySemesterIdIn(List<Long> semesterIds);

}
