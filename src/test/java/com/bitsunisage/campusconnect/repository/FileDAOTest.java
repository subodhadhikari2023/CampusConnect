package com.bitsunisage.campusconnect.repository;

import com.bitsunisage.campusconnect.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class FileDAOTest {

    @Autowired private FileDAO fileDAO;
    @Autowired private DepartmentDAO departmentDAO;
    @Autowired private CourseDetailsDAO courseDetailsDAO;
    @Autowired private SemesterDAO semesterDAO;
    @Autowired private SubjectDetailsDAO subjectDetailsDAO;
    @Autowired private UserDAO userDAO;

    private Long deptId;
    private Long courseId;
    private Long semesterId;
    private Long subjectId;

    @BeforeEach
    void setUp() {
        Department dept = new Department();
        dept.setId(1001L);
        dept.setName("Computer Science");
        departmentDAO.save(dept);
        deptId = 1001L;

        CourseDetails course = new CourseDetails();
        course.setCourseName("BSc CS");
        course.setDepartmentId(deptId);
        courseDetailsDAO.save(course);
        courseId = course.getCourseId();

        Semester semester = new Semester();
        semester.setSemesterName("Semester 1");
        semesterDAO.save(semester);
        semesterId = semester.getSemesterId();

        SubjectDetails subject = new SubjectDetails();
        subject.setSubjectName("Data Structures");
        subject.setCourseId(courseId.intValue());
        subject.setSemesterId(semesterId.intValue());
        subjectDetailsDAO.save(subject);
        subjectId = subject.getSubjectId();
    }

    private FileData buildFileData(String fileName, String fileRole) {
        FileData fd = new FileData();
        fd.setFileName(fileName);
        fd.setFilePath("/uploads/test");
        fd.setFileType(".pdf");
        fd.setFileSize(1024L);
        fd.setOwnersName("teacher1");
        fd.setFileDepartmentId(deptId);
        fd.setCourseId(courseId);
        fd.setSemesterId(semesterId);
        fd.setSubjectId(subjectId);
        fd.setFileRole(fileRole);
        return fd;
    }

    @Test
    void findFilesByFiltersReturnsMatchingRecords() {
        fileDAO.save(buildFileData("slides.pdf", "PPTs"));
        fileDAO.save(buildFileData("notes.pdf", "Notes"));

        List<FileData> result = fileDAO.findFilesByFilters(deptId, courseId, semesterId, subjectId, "PPTs");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFileName()).isEqualTo("slides.pdf");
    }

    @Test
    void findFilesByFiltersReturnsEmptyWhenNoMatch() {
        fileDAO.save(buildFileData("slides.pdf", "PPTs"));

        List<FileData> result = fileDAO.findFilesByFilters(deptId, courseId, semesterId, subjectId, "Videos");
        assertThat(result).isEmpty();
    }

    @Test
    void findFilesByFiltersReturnsMultipleMatchingRecords() {
        fileDAO.save(buildFileData("slides1.pdf", "PPTs"));
        fileDAO.save(buildFileData("slides2.pdf", "PPTs"));

        List<FileData> result = fileDAO.findFilesByFilters(deptId, courseId, semesterId, subjectId, "PPTs");
        assertThat(result).hasSize(2);
    }

    @Test
    void findAllByOwnersNameReturnsOnlyThatOwnerFiles() {
        FileData fd1 = buildFileData("teacher1file.pdf", "Notes");
        fd1.setOwnersName("teacher1");
        FileData fd2 = buildFileData("teacher2file.pdf", "Notes");
        fd2.setOwnersName("teacher2");
        fileDAO.save(fd1);
        fileDAO.save(fd2);

        List<FileData> result = fileDAO.findAllByOwnersName("teacher1");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getOwnersName()).isEqualTo("teacher1");
    }

    @Test
    void findByIdReturnsFileData() {
        FileData saved = fileDAO.save(buildFileData("test.pdf", "Notes"));
        Optional<FileData> result = fileDAO.findById(saved.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getFileName()).isEqualTo("test.pdf");
    }

    @Test
    void deleteByIdRemovesRecord() {
        FileData saved = fileDAO.save(buildFileData("delete-me.pdf", "Notes"));
        fileDAO.deleteById(saved.getId());
        assertThat(fileDAO.findById(saved.getId())).isEmpty();
    }
}
