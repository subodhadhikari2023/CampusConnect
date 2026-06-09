package com.bitsunisage.campusconnect.entities;

import jakarta.persistence.*;

/**
 * JPA entity for the {@code subject_details} table.
 * Each subject belongs to one course and one semester.
 */
@Entity(name = "subject_details")
public class SubjectDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subject_id")
    private Long subjectId;

    @Column(name = "subject_name")
    private String subjectName;

    @Column(name = "course_id")
    private int courseId;

    @Column(name = "semester_id")
    private int semesterId;

    /** @return numeric subject ID (primary key) */
    public Long getSubjectId() {
        return subjectId;
    }

    /** @param subjectId numeric subject ID */
    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    /** @return display name of the subject */
    public String getSubjectName() {
        return subjectName;
    }

    /** @param subjectName display name of the subject */
    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    /** @return ID of the course this subject belongs to */
    public int getCourseId() {
        return courseId;
    }

    /** @param courseId course ID */
    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    /** @return ID of the semester this subject is taught in */
    public int getSemesterId() {
        return semesterId;
    }

    /** @param semesterId semester ID */
    public void setSemesterId(int semesterId) {
        this.semesterId = semesterId;
    }
}
