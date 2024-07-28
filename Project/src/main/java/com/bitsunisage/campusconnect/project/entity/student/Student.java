package com.bitsunisage.campusconnect.project.entity.student;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Student {
private String firstName;
private String lastName;
    @Id
    private Long id;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
