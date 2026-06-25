package com.bitsunisage.campusconnect.entities;

import jakarta.persistence.*;

import java.sql.Timestamp;

/**
 * JPA entity for the {@code announcements} table.
 * Represents a department-scoped notice posted by an HOD.
 * Teachers and students in the same department can read these announcements.
 */
@Entity
@Table(name = "announcements")
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** Numeric ID of the department this announcement belongs to (FK to {@code department.department_id}). */
    @Column(name = "dept_id", nullable = false)
    private Long deptId;

    /** Login username of the HOD who posted this announcement (FK to {@code members.user_id}). */
    @Column(name = "author", nullable = false)
    private String author;

    /** Short headline for the announcement; displayed in summary lists. */
    @Column(name = "title", nullable = false)
    private String title;

    /** Full announcement body text. */
    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    private String body;

    /** Timestamp when the row was inserted; set by the database default. */
    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;

    /** @return surrogate primary key */
    public Long getId() {
        return id;
    }

    /** @param id surrogate primary key */
    public void setId(Long id) {
        this.id = id;
    }

    /** @return department ID this announcement is scoped to */
    public Long getDeptId() {
        return deptId;
    }

    /** @param deptId department ID */
    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    /** @return login username of the posting HOD */
    public String getAuthor() {
        return author;
    }

    /** @param author login username */
    public void setAuthor(String author) {
        this.author = author;
    }

    /** @return announcement headline */
    public String getTitle() {
        return title;
    }

    /** @param title announcement headline */
    public void setTitle(String title) {
        this.title = title;
    }

    /** @return full announcement body */
    public String getBody() {
        return body;
    }

    /** @param body announcement body */
    public void setBody(String body) {
        this.body = body;
    }

    /** @return creation timestamp (database-managed) */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /** @param createdAt creation timestamp */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
