package com.bitsunisage.campusconnect.project.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA entity for the {@code roles} table.
 * Each row grants exactly one Spring Security role to a member.
 * Read by {@link org.springframework.security.provisioning.JdbcUserDetailsManager}
 * during authentication.
 */
@Entity
@Table(name = "roles")
public class Roles {

    /** Required by JPA. */
    public Roles() {
    }

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "role")
    private String role;

    /** @return login username (primary key, FK to {@code members.user_id}) */
    public String getUserId() {
        return userId;
    }

    /** @param userId login username */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /** @return Spring Security role string, e.g. {@code "ROLE_STUDENT"} */
    public String getRole() {
        return role;
    }

    /** @param role Spring Security role string */
    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Roles{" +
                "userId='" + userId + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
