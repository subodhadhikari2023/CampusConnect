package com.bitsunisage.campusconnect.entities;

import jakarta.persistence.*;

/**
 * JPA entity for the {@code semester} table.
 * Semesters are global — they are shared across all departments and courses.
 */
@Entity(name = "semester")
public class Semester {

    @Id
    @Column(name = "semester_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long semesterId;

    @Column(name = "semester_name")
    private String semesterName;

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

    @Override
    public String toString() {
        return "Semester{" +
                "semesterId=" + semesterId +
                ", semesterName='" + semesterName + '\'' +
                '}';
    }
}
