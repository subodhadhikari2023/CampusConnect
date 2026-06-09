package com.bitsunisage.campusconnect.project.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA entity representing a platform user stored in the {@code members} table.
 * Covers all roles: Admin, HOD, Teacher, and Student.
 */
@Entity
@Table(name = "members")
public class User {

    @Column(name = "id")
    Long id;

    /** @return auto-incremented surrogate key (distinct from the login {@code userId}) */
    public Long getId() {
        return id;
    }

    /** @param id surrogate key */
    public void setId(Long id) {
        this.id = id;
    }

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "pw")
    private String password;

    @Column(name = "active")
    private boolean active;

    @Column(name = "email")
    private String email;

    @Column(name = "department")
    private String department;

    @Column(name = "dept_id")
    private Long deptID;

    /** @return the numeric ID of the user's home department */
    public Long getDeptID() {
        return deptID;
    }

    /** @param deptID numeric department ID */
    public void setDeptID(Long deptID) {
        this.deptID = deptID;
    }

    /** @return {@code true} if the account is active and allowed to log in */
    public boolean isActive() {
        return active;
    }

    /** @return name of the user's home department (denormalised copy) */
    public String getDepartment() {
        return department;
    }

    /** @param department department name */
    public void setDepartment(String department) {
        this.department = department;
    }

    /** Required by JPA; use field setters to populate before persisting. */
    public User() {
    }

    /** @return login username (primary key) */
    public String getUserId() {
        return userId;
    }

    /** @param userId login username */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Returns the stored password string.
     * In dev/test environments this is prefixed with {@code {noop}} for plain-text storage.
     *
     * @return encoded or plain-text password
     */
    public String getPassword() {
        return password;
    }

    /** @param password encoded or plain-text password */
    public void setPassword(String password) {
        this.password = password;
    }

    /** @return {@code true} if the account is active */
    public boolean getActive() {
        return active;
    }

    /** @param active account active flag */
    public void setActive(boolean active) {
        this.active = active;
    }

    /** @return user's email address */
    public String getEmail() {
        return email;
    }

    /** @param email user's email address */
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" + "userId='" + userId + '\'' + ", password='" + password + '\'' + ", active=" + active + ", email='" + email + '\'' + '}';
    }
}
