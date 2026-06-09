package com.bitsunisage.campusconnect.service;

import com.bitsunisage.campusconnect.repository.*;
import com.bitsunisage.campusconnect.entities.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * {@link UserService} implementation backed by Spring Data JPA repositories.
 * Every public method runs inside a transaction provided by {@link Transactional}.
 */
@Transactional
@Service
public class UserServiceImplementation implements UserService {

    private final UserDAO userDAO;
    private final RoleDAO roleDAO;
    private final DepartmentDAO departmentDAO;
    private final CourseDetailsDAO courseDetailsDAO;
    private final SemesterDAO semesterDAO;
    private final SubjectDetailsDAO subjectDetailsDAO;

    /**
     * Constructs the service with all required repositories injected by Spring.
     *
     * @param userDAO            repository for {@link User}
     * @param roleDAO            repository for {@link Roles}
     * @param departmentDAO      repository for {@link Department}
     * @param courseDetailsDAO   repository for {@link CourseDetails}
     * @param semesterDAO        repository for {@link Semester}
     * @param subjectDetailsDAO  repository for {@link SubjectDetails}
     */
    @Autowired
    public UserServiceImplementation(UserDAO userDAO, RoleDAO roleDAO,
                                     DepartmentDAO departmentDAO,
                                     CourseDetailsDAO courseDetailsDAO,
                                     SemesterDAO semesterDAO,
                                     SubjectDetailsDAO subjectDetailsDAO) {
        this.userDAO = userDAO;
        this.roleDAO = roleDAO;
        this.departmentDAO = departmentDAO;
        this.courseDetailsDAO = courseDetailsDAO;
        this.semesterDAO = semesterDAO;
        this.subjectDetailsDAO = subjectDetailsDAO;
    }

    /** {@inheritDoc} */
    @Override
    public List<User> findAllUsers() {
        return userDAO.findAll();
    }

    /** {@inheritDoc} */
    @Override
    public List<Roles> findAllRoles() {
        return roleDAO.findAll();
    }

    /** {@inheritDoc} */
    @Override
    public User findUserByUserId(String userId) {
        return userDAO.findByUserId(userId);
    }

    /** {@inheritDoc} */
    @Override
    public Roles findRoleByUserId(String userId) {
        return roleDAO.findByUserId(userId);
    }

    /** {@inheritDoc} */
    @Override
    public User save(User user) {
        return userDAO.save(user);
    }

    /** {@inheritDoc} */
    @Override
    public Roles save(Roles roles) {
        return roleDAO.save(roles);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteUser(User user) {
        userDAO.delete(user);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteRole(Roles roles) {
        roleDAO.delete(roles);
    }

    /** {@inheritDoc} */
    @Override
    public Integer totalUsers() {
        return userDAO.findAll().size();
    }

    /** {@inheritDoc} */
    @Override
    public Integer totalUsers(String role) {
        return roleDAO.readRolesByRole(role).size();
    }

    /** {@inheritDoc} */
    @Override
    public List<Roles> findByRole(String role) {
        return roleDAO.readRolesByRole(role);
    }

    /** {@inheritDoc} */
    @Override
    public List<Department> getAllDepartments() {
        return departmentDAO.findAll();
    }

    /** {@inheritDoc} */
    @Override
    public List<CourseDetails> getAllCourses() {
        return courseDetailsDAO.findAll();
    }

    /** {@inheritDoc} */
    @Override
    public List<Semester> getAllSemesters() {
        return semesterDAO.findAll();
    }

    /** {@inheritDoc} */
    @Override
    public List<SubjectDetails> getAllSubjects() {
        return subjectDetailsDAO.findAll();
    }

    /** {@inheritDoc} */
    @Override
    public Department getDepartmentIdByDepartmentName(String name) {
        return departmentDAO.findByName(name);
    }

    /** {@inheritDoc} */
    @Override
    public Department getDepartmentNameByDepartmentId(Integer id) {
        return departmentDAO.findById(id);
    }

    /** {@inheritDoc} */
    @Override
    public List<CourseDetails> getCourseName(List<Long> id) {
        return courseDetailsDAO.findByCourseIdIn(id);
    }

    /** {@inheritDoc} */
    @Override
    public List<Semester> getSemesterName(List<Long> semesterIds) {
        return semesterDAO.findBySemesterIdIn(semesterIds);
    }

    /** {@inheritDoc} */
    @Override
    public List<SubjectDetails> getSubjectName(List<Long> subjectIds) {
        return subjectDetailsDAO.findBySubjectIdIn(subjectIds);
    }

    /** {@inheritDoc} */
    @Override
    public List<Department> getDepartmentNames(List<Long> deptIds) {
        return departmentDAO.findByIdIn(deptIds);
    }
}
