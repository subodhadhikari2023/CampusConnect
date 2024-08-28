package com.bitsunisage.campusconnect.project.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

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

    @Override
    public String toString() {
        return "DepartmentDetails{" + "memberId=" + memberId + ", departmentId=" + departmentId + ", userName='" + userName + '\'' + ", role='" + role + '\'' + '}';
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
