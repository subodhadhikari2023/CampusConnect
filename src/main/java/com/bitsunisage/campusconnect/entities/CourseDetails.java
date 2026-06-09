package com.bitsunisage.campusconnect.entities;

import jakarta.persistence.*;

/**
 * JPA entity for the {@code course_details} table.
 * A course belongs to exactly one department and can have many subjects.
 */
@Entity(name = "course_details")
public class CourseDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "course_name")
    private String courseName;

    @Column(name = "department_id")
    private Long departmentId;

    /** @return numeric course ID (primary key) */
    public Long getCourseId() {
        return courseId;
    }

    /** @param courseId numeric course ID */
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    /** @return display name of the course */
    public String getCourseName() {
        return courseName;
    }

    /** @param courseName display name of the course */
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    /** @return ID of the department that owns this course */
    public Long getDepartmentId() {
        return departmentId;
    }

    /** @param departmentId department ID */
    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    @Override
    public String toString() {
        return "CourseDetailsDAO{" + "courseId=" + courseId + ", courseName='" + courseName + '\'' + ", departmentId=" + departmentId + '}';
    }
}
