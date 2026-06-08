package com.bitsunisage.campusconnect.project.dataAccessObject;

import com.bitsunisage.campusconnect.project.entities.Roles;
import com.bitsunisage.campusconnect.project.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class RoleDAOTest {

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        saveUserAndRole("student1", "student1@campus.com", "ROLE_STUDENT");
        saveUserAndRole("student2", "student2@campus.com", "ROLE_STUDENT");
        saveUserAndRole("teacher1", "teacher1@campus.com", "ROLE_TEACHER");
    }

    private void saveUserAndRole(String userId, String email, String role) {
        User user = new User();
        user.setUserId(userId);
        user.setPassword("{noop}password");
        user.setActive(true);
        user.setEmail(email);
        user.setDepartment("Computer Science");
        userDAO.save(user);

        Roles r = new Roles();
        r.setUserId(userId);
        r.setRole(role);
        roleDAO.save(r);
    }

    @Test
    void readRolesByRoleReturnsOnlyMatchingRoles() {
        List<Roles> students = roleDAO.readRolesByRole("ROLE_STUDENT");
        assertThat(students).hasSize(2);
        assertThat(students).allMatch(r -> r.getRole().equals("ROLE_STUDENT"));
    }

    @Test
    void readRolesByRoleReturnsEmptyWhenNoMatch() {
        List<Roles> hods = roleDAO.readRolesByRole("ROLE_HOD");
        assertThat(hods).isEmpty();
    }

    @Test
    void findByUserIdReturnsCorrectRole() {
        Roles role = roleDAO.findByUserId("teacher1");
        assertThat(role).isNotNull();
        assertThat(role.getRole()).isEqualTo("ROLE_TEACHER");
    }

    @Test
    void findByUserIdReturnsNullForUnknownUser() {
        Roles role = roleDAO.findByUserId("unknown");
        assertThat(role).isNull();
    }
}
