package com.bitsunisage.campusconnect.project.service;

import com.bitsunisage.campusconnect.project.dataAccessObject.*;
import com.bitsunisage.campusconnect.project.entities.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Transactional
@Service
public class userServiceImplementation implements UserService {
    private final UserDAO userDAO;
    private final RoleDAO roleDAO;
    private final DepartmentDAO departmentDAO;
    private final CourseDetailsDAO courseDetailsDAO;
    private final SemesterDAO semesterDAO;
    private final SubjectDetailsDAO subjectDetailsDAO;

    @Autowired
    public userServiceImplementation(UserDAO userDAO, RoleDAO roleDAO, DepartmentDAO departmentDAO, CourseDetailsDAO courseDetailsDAO, SemesterDAO semesterDAO, SubjectDetailsDAO subjectDetailsDAO) {
        this.userDAO = userDAO;
        this.roleDAO = roleDAO;
        this.departmentDAO = departmentDAO;
        this.courseDetailsDAO = courseDetailsDAO;
        this.semesterDAO = semesterDAO;
        this.subjectDetailsDAO = subjectDetailsDAO;
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
    public void deleteRole(Roles roles) {
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

    @Override
    public List<Department> getAllDepartments() {
        return departmentDAO.findAll();
    }

    @Override
    public List<CourseDetails> getAllCourses() {
        return courseDetailsDAO.findAll();
    }

    @Override
    public List<Semester> getAllSemesters() {
        return semesterDAO.findAll();
    }

    @Override
    public List<SubjectDetails> getAllSubjects() {
        return subjectDetailsDAO.findAll();
    }

    @Override
    public Integer getDepartmentIdByDepartmentName(String name) {
       return departmentDAO.findIdByName(name);
    }


}
