package com.bitsunisage.campusconnect.service;

import com.bitsunisage.campusconnect.repository.*;
import com.bitsunisage.campusconnect.entities.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    private final DepartmentDetailsDAO departmentDetailsDAO;
    private final CourseDetailsDAO courseDetailsDAO;
    private final SemesterDAO semesterDAO;
    private final SubjectDetailsDAO subjectDetailsDAO;
    private final FileDAO fileDAO;
    private final TeacherSubjectDAO teacherSubjectDAO;
    private final AnnouncementDAO announcementDAO;

    /**
     * Constructs the service with all required repositories injected by Spring.
     *
     * @param userDAO               repository for {@link User}
     * @param roleDAO               repository for {@link Roles}
     * @param departmentDAO         repository for {@link Department}
     * @param departmentDetailsDAO  repository for {@link DepartmentDetails}
     * @param courseDetailsDAO      repository for {@link CourseDetails}
     * @param semesterDAO           repository for {@link Semester}
     * @param subjectDetailsDAO     repository for {@link SubjectDetails}
     * @param fileDAO               repository for {@link FileData}
     * @param teacherSubjectDAO     repository for {@link TeacherSubject}
     * @param announcementDAO       repository for {@link Announcement}
     */
    @Autowired
    public UserServiceImplementation(UserDAO userDAO, RoleDAO roleDAO,
                                     DepartmentDAO departmentDAO,
                                     DepartmentDetailsDAO departmentDetailsDAO,
                                     CourseDetailsDAO courseDetailsDAO,
                                     SemesterDAO semesterDAO,
                                     SubjectDetailsDAO subjectDetailsDAO,
                                     FileDAO fileDAO,
                                     TeacherSubjectDAO teacherSubjectDAO,
                                     AnnouncementDAO announcementDAO) {
        this.userDAO = userDAO;
        this.roleDAO = roleDAO;
        this.departmentDAO = departmentDAO;
        this.departmentDetailsDAO = departmentDetailsDAO;
        this.courseDetailsDAO = courseDetailsDAO;
        this.semesterDAO = semesterDAO;
        this.subjectDetailsDAO = subjectDetailsDAO;
        this.fileDAO = fileDAO;
        this.teacherSubjectDAO = teacherSubjectDAO;
        this.announcementDAO = announcementDAO;
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
    public List<Semester> getSemestersByCourseId(Long courseId) {
        return semesterDAO.findByCourseIdOrderBySemesterIdAsc(courseId);
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

    /** {@inheritDoc} */
    @Override
    public List<CourseDetails> getCoursesByDepartmentId(Long deptId) {
        return courseDetailsDAO.findByDepartmentId(deptId);
    }

    /** {@inheritDoc} */
    @Override
    public CourseDetails saveCourse(CourseDetails course) {
        return courseDetailsDAO.save(course);
    }

    /** {@inheritDoc} */
    @Override
    public CourseDetails getCourseById(Long courseId) {
        return courseDetailsDAO.findByCourseId(courseId);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteCourseById(Long courseId) {
        courseDetailsDAO.deleteByCourseId(courseId);
    }

    /** {@inheritDoc} */
    @Override
    public List<SubjectDetails> getSubjectsByCourseId(int courseId) {
        return subjectDetailsDAO.findByCourseId(courseId);
    }

    /** {@inheritDoc} */
    @Override
    public SubjectDetails saveSubject(SubjectDetails subject) {
        return subjectDetailsDAO.save(subject);
    }

    /** {@inheritDoc} */
    @Override
    public SubjectDetails getSubjectById(Long subjectId) {
        return subjectDetailsDAO.findBySubjectId(subjectId);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteSubjectById(Long subjectId) {
        subjectDetailsDAO.deleteBySubjectId(subjectId);
    }

    /** {@inheritDoc} */
    @Override
    public int countMembersByDepartmentAndRole(Long deptId, String role) {
        return departmentDetailsDAO.countByDepartmentIdAndRole(deptId.intValue(), role);
    }

    /** {@inheritDoc} */
    @Override
    public DepartmentDetails saveDepartmentDetails(DepartmentDetails details) {
        return departmentDetailsDAO.save(details);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteDepartmentDetailsByUserName(String userName) {
        departmentDetailsDAO.deleteByUserName(userName);
    }

    /** {@inheritDoc} */
    @Override
    public Department saveDepartment(Department dept) {
        return departmentDAO.save(dept);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteDepartment(Department dept) {
        departmentDAO.delete(dept);
    }

    /** {@inheritDoc} */
    @Override
    public int countMembersByDepartment(Long deptId) {
        return departmentDetailsDAO.countByDepartmentId(deptId.intValue());
    }

    /** {@inheritDoc} */
    @Override
    public Semester saveSemester(Semester semester) {
        return semesterDAO.save(semester);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteSemester(Semester semester) {
        semesterDAO.delete(semester);
    }

    /** {@inheritDoc} */
    @Override
    public Semester getSemesterById(Long semesterId) {
        return semesterDAO.findById(semesterId.intValue()).orElse(null);
    }

    /** {@inheritDoc} */
    @Override
    public int countSubjectsBySemester(Long semesterId) {
        return subjectDetailsDAO.countBySemesterId(semesterId.intValue());
    }

    /** {@inheritDoc} */
    @Override
    public List<User> getMembersByDepartmentAndRole(Long deptId, String role) {
        List<DepartmentDetails> records = departmentDetailsDAO.findByDepartmentIdAndRole(deptId.intValue(), role);
        List<String> userIds = records.stream()
                .map(DepartmentDetails::getUserName)
                .toList();
        return userDAO.findByUserIdIn(userIds);
    }

    /** {@inheritDoc} */
    @Override
    public List<FileData> getFilesByDepartmentId(Long deptId) {
        return fileDAO.findByFileDepartmentIdOrderByUploadDateDesc(deptId);
    }

    /** {@inheritDoc} */
    @Override
    public List<TeacherSubject> getAssignmentsBySubjectIds(List<Long> subjectIds) {
        if (subjectIds == null || subjectIds.isEmpty()) return List.of();
        return teacherSubjectDAO.findBySubjectIdIn(subjectIds);
    }

    /** {@inheritDoc} */
    @Override
    public void upsertTeacherAssignment(String teacherId, Long subjectId) {
        TeacherSubject assignment = teacherSubjectDAO.findBySubjectId(subjectId).orElse(new TeacherSubject());
        assignment.setTeacherId(teacherId);
        assignment.setSubjectId(subjectId);
        teacherSubjectDAO.save(assignment);
    }

    /** {@inheritDoc} */
    @Override
    public void removeTeacherAssignmentBySubjectId(Long subjectId) {
        teacherSubjectDAO.findBySubjectId(subjectId).ifPresent(teacherSubjectDAO::delete);
    }

    /** {@inheritDoc} */
    @Override
    public List<TeacherSubject> getAssignmentsByTeacherIds(List<String> teacherIds) {
        if (teacherIds == null || teacherIds.isEmpty()) return List.of();
        return teacherSubjectDAO.findByTeacherIdIn(teacherIds);
    }

    /** {@inheritDoc} */
    @Override
    public List<Announcement> getAnnouncementsByDeptId(Long deptId) {
        return announcementDAO.findByDeptIdOrderByCreatedAtDesc(deptId);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Announcement> getAnnouncementById(Long id) {
        return announcementDAO.findById(id);
    }

    /** {@inheritDoc} */
    @Override
    public Announcement saveAnnouncement(Announcement announcement) {
        return announcementDAO.save(announcement);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteAnnouncementById(Long id) {
        announcementDAO.deleteById(id);
    }

    /** {@inheritDoc} */
    @Override
    public boolean courseNameExistsInDepartment(Long deptId, String courseName) {
        return courseDetailsDAO.existsByDepartmentIdAndCourseName(deptId, courseName);
    }

    /** {@inheritDoc} */
    @Override
    public boolean semesterNameExistsInCourse(Long courseId, String semesterName) {
        return semesterDAO.existsByCourseIdAndSemesterName(courseId, semesterName);
    }

    /** {@inheritDoc} */
    @Override
    public boolean subjectNameExistsInSlot(int courseId, int semesterId, String subjectName) {
        return subjectDetailsDAO.existsByCourseIdAndSemesterIdAndSubjectName(courseId, semesterId, subjectName);
    }

    /** {@inheritDoc} */
    @Override
    public int countSubjectsByDepartmentId(Long deptId) {
        List<CourseDetails> courses = courseDetailsDAO.findByDepartmentId(deptId);
        return courses.stream()
                .mapToInt(c -> subjectDetailsDAO.countByCourseId(c.getCourseId().intValue()))
                .sum();
    }
}
