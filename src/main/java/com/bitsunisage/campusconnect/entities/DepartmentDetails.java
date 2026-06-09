package com.bitsunisage.campusconnect.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * JPA entity for the {@code department_details} join table.
 * Maps a member ({@code user_name}) to a department and records their role label
 * within that department (e.g. HOD, TEACHER, STUDENT, ADMIN).
 */
@Entity(name = "department_details")
public class DepartmentDetails {

    @Id
    @Column(name = "department_member_id")
    private Integer memberId;

    @Column(name = "department_id")
    private Integer departmentId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "role")
    private String role;

    /** @return surrogate primary key for this membership record */
    public Integer getMemberId() {
        return memberId;
    }

    /** @param memberId surrogate primary key */
    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    /** @return ID of the department this member belongs to */
    public Integer getDepartmentId() {
        return departmentId;
    }

    /** @param departmentId department ID */
    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    /** @return login username of the member */
    public String getUserName() {
        return userName;
    }

    /** @param userName login username */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /** @return role label within the department (e.g. {@code "HOD"}, {@code "TEACHER"}) */
    public String getRole() {
        return role;
    }

    /** @param role role label */
    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "DepartmentDetails{" + "memberId=" + memberId + ", departmentId=" + departmentId + ", userName='" + userName + '\'' + ", role='" + role + '\'' + '}';
    }
}
