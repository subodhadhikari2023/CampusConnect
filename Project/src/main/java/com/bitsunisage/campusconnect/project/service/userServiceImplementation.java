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
    public User findUserByUserId(String userId) {
        return userDAO.findByUserId(userId);
    }

    @Override
    public Roles findRoleByUserId(String userId) {
        return roleDAO.findByUserId(userId);
    }


    @Override
    public User save(User user) {
        return userDAO.save(user);
    }

    @Override
    public Roles save(Roles roles) {
        return roleDAO.save(roles);
    }

    @Override
    public void deleteUser(User user) {
        userDAO.delete(user);

    }
    @Override
    public void deleteRole(Roles roles){
        roleDAO.delete(roles);
    }


    @Override
    public Integer totalUsers() {
        List<User> users = userDAO.findAll();
        return users.size();
    }

    @Override
    public Integer totalUsers(String role) {
        List<Roles> roles = roleDAO.readRolesByRole(role);
        return roles.size();
    }

    @Override
    public List<Roles> findByRole(String role) {
        return roleDAO.readRolesByRole(role);
    }


}
