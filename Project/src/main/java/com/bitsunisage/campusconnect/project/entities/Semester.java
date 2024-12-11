package com.bitsunisage.campusconnect.project.entities;

import jakarta.persistence.*;

@Entity(name = "semester")
public class Semester {

    @Id
    @Column(name = "semester_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long semesterId;


    @Column(name ="semester_name")
    private String semesterName;

    public Long getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(Long semesterId) {
        this.semesterId = semesterId;
    }

    public String getSemesterName() {
        return semesterName;
    }

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
