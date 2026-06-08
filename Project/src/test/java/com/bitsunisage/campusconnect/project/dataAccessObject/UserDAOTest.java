package com.bitsunisage.campusconnect.project.dataAccessObject;

import com.bitsunisage.campusconnect.project.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserDAOTest {

    @Autowired
    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUserId("student1");
        user.setPassword("{noop}password");
        user.setActive(true);
        user.setEmail("student1@campus.com");
        user.setDepartment("Computer Science");
        userDAO.save(user);
    }

    @Test
    void findByUserIdReturnsCorrectUser() {
        User result = userDAO.findByUserId("student1");
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo("student1");
    }

    @Test
    void findByUserIdReturnsNullForUnknownUser() {
        User result = userDAO.findByUserId("nonexistent");
        assertThat(result).isNull();
    }

    @Test
    void findAllReturnsAllSavedUsers() {
        User another = new User();
        another.setUserId("teacher1");
        another.setPassword("{noop}password");
        another.setActive(true);
        another.setEmail("teacher1@campus.com");
        another.setDepartment("Physics");
        userDAO.save(another);

        assertThat(userDAO.findAll()).hasSize(2);
    }

    @Test
    void deleteRemovesUser() {
        User user = userDAO.findByUserId("student1");
        userDAO.delete(user);
        assertThat(userDAO.findByUserId("student1")).isNull();
    }
}
