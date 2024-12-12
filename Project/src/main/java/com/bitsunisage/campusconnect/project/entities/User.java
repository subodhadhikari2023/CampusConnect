package com.bitsunisage.campusconnect.project.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "members")
public class User {
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

    public Long getDeptID() {
        return deptID;
    }

    public void setDeptID(Long deptID) {
        this.deptID = deptID;
    }

    public boolean isActive() {
        return active;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    // No Argument Constructor
    public User() {

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String toString() {
        return "User{" + "userId='" + userId + '\'' + ", password='" + password + '\'' + ", active=" + active + ", email='" + email + '\'' + '}';
    }

}
