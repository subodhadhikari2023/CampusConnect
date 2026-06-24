package com.bitsunisage.campusconnect.entities;

import jakarta.persistence.*;

/**
 * JPA entity for the {@code semester} table.
 * Semesters are scoped to a single course — the HOD for that course defines them.
 */
@Entity(name = "semester")
public class Semester {

    @Id
    @Column(name = "semester_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long semesterId;

    @Column(name = "semester_name")
    private String semesterName;

    @Column(name = "course_id")
    private Long courseId;

    /** @return numeric semester ID (primary key) */
    public Long getSemesterId() {
        return semesterId;
    }

    /** @param semesterId numeric semester ID */
    public void setSemesterId(Long semesterId) {
        this.semesterId = semesterId;
    }

    /** @return display name of the semester */
    public String getSemesterName() {
        return semesterName;
    }

    /** @param semesterName display name of the semester */
    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }

    /** @return ID of the course this semester belongs to */
    public Long getCourseId() {
        return courseId;
    }

    /** @param courseId course ID */
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    @Override
    public String toString() {
        return "Semester{" +
                "semesterId=" + semesterId +
                ", semesterName='" + semesterName + '\'' +
                ", courseId=" + courseId +
                '}';
    }
}
