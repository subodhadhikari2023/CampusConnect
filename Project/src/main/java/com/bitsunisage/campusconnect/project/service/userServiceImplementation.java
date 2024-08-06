package com.bitsunisage.campusconnect.project.service;

import com.bitsunisage.campusconnect.project.dataAccessObject.RoleDAO;
import com.bitsunisage.campusconnect.project.dataAccessObject.UserDAO;
import com.bitsunisage.campusconnect.project.entities.Roles;
import com.bitsunisage.campusconnect.project.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class userServiceImplementation implements UserService {
    private UserDAO userDAO;
    private RoleDAO roleDAO;

    @Autowired
    public userServiceImplementation(UserDAO userDAO, RoleDAO roleDAO) {
        this.userDAO = userDAO;
        this.roleDAO = roleDAO;
    }

    @Override
    public List<User> findAllUsers() {

        return userDAO.findAll();
    }

    @Override
    public List<Roles> findAllRoles() {
        return roleDAO.findAll();
    }

    @Override
    public User findById(int userId) {
        return null;
    }

    @Override
    public User save(User user) {
        return null;
    }

    @Override
    public void deleteUser(int userId) {

    }
}
