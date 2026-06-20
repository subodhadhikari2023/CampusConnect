package com.bitsunisage.campusconnect.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * JPA entity representing an academic department stored in the {@code department} table.
 */
@Entity(name = "department")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "department_id")
    private Long id;

    @Column(name = "department_name")
    private String name;

    /** @return numeric department ID (primary key) */
    public Long getId() {
        return id;
    }

    /** @param id numeric department ID */
    public void setId(Long id) {
        this.id = id;
    }

    /** @return unique department name */
    public String getName() {
        return name;
    }

    /** @param name department name */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Department{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
