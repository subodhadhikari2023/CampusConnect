package com.bitsunisage.campusconnect.project.dataAccessObject;

import com.bitsunisage.campusconnect.project.entities.CourseDetails;
import com.bitsunisage.campusconnect.project.entities.Semester;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseDetailsDAO extends JpaRepository<CourseDetails, Integer> {
    List<CourseDetails> findByDepartmentId(Long departmentId);

    List<CourseDetails> findByCourseIdIn(List<Long> courseId);






}
