package com.bitsunisage.campusconnect.entities;

import jakarta.persistence.*;

/**
 * JPA entity for the {@code teacher_subject} table.
 * Records which teacher is assigned to teach a given subject.
 * At most one teacher may be assigned per subject (enforced by the unique key on {@code subject_id}).
 */
@Entity
@Table(name = "teacher_subject")
public class TeacherSubject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** Login username of the assigned teacher (FK to {@code members.user_id}). */
    @Column(name = "teacher_id", nullable = false)
    private String teacherId;

    /** Primary key of the subject being taught (FK to {@code subject_details.subject_id}). */
    @Column(name = "subject_id", nullable = false, unique = true)
    private Long subjectId;

    /** @return surrogate primary key of this assignment record */
    public Long getId() {
        return id;
    }

    /** @param id surrogate primary key */
    public void setId(Long id) {
        this.id = id;
    }

    /** @return login username of the assigned teacher */
    public String getTeacherId() {
        return teacherId;
    }

    /** @param teacherId login username of the teacher */
    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    /** @return subject primary key this assignment applies to */
    public Long getSubjectId() {
        return subjectId;
    }

    /** @param subjectId subject primary key */
    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }
}
