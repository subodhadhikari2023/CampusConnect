package com.bitsunisage.campusconnect.project.dataAccessObject;

import com.bitsunisage.campusconnect.project.entities.CourseDetails;
import com.bitsunisage.campusconnect.project.entities.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CourseDetailsDAO extends JpaRepository<CourseDetails, Integer> {
    List<CourseDetails> findByDepartmentId(Long departmentId);

    List<CourseDetails> findByCourseIdIn(List<Long> courseId);






}
