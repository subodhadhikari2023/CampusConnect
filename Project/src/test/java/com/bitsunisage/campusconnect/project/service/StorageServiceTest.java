package com.bitsunisage.campusconnect.project.service;

import com.bitsunisage.campusconnect.project.dataAccessObject.DepartmentDAO;
import com.bitsunisage.campusconnect.project.dataAccessObject.FileDAO;
import com.bitsunisage.campusconnect.project.entities.FileData;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StorageServiceTest {

    @Mock private FileDAO fileDAO;
    @Mock private DepartmentDAO departmentDAO;

    @InjectMocks
    private StorageServiceImplementation storageService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(storageService, "uploadDir", tempDir.toString());
    }

    @Test
    void getCurrentOwnersNameReturnsAuthenticatedUsername() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("teacher1", "password",
                        List.of(new SimpleGrantedAuthority("ROLE_TEACHER")))
        );
        assertThat(storageService.getCurrentOwnersName()).isEqualTo("teacher1");
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentOwnersNameReturnsNullWhenNotAuthenticated() {
        SecurityContextHolder.clearContext();
        assertThat(storageService.getCurrentOwnersName()).isNull();
    }

    @Test
    void deleteResourceRemovesFileAndDatabaseRecord() throws Exception {
        Path fileDir = tempDir.resolve("dept/role/course/sem/sub");
        Files.createDirectories(fileDir);
        Path file = fileDir.resolve("test.pdf");
        Files.writeString(file, "dummy content");

        FileData fileData = new FileData();
        fileData.setId(1L);
        fileData.setFileName("test.pdf");
        fileData.setFilePath(fileDir.toString());

        when(fileDAO.findById(1L)).thenReturn(Optional.of(fileData));

        storageService.deleteResource(1L);

        assertThat(file).doesNotExist();
        verify(fileDAO).deleteById(1L);
    }

    @Test
    void deleteResourceThrowsWhenFileNotFound() {
        when(fileDAO.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> storageService.deleteResource(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void findAllReturnsAllFileData() {
        FileData fd = new FileData();
        when(fileDAO.findAll()).thenReturn(List.of(fd));
        assertThat(storageService.findAll()).hasSize(1);
    }

    @Test
    void findFilesByFiltersDelegatesToDAO() {
        FileData fd = new FileData();
        when(fileDAO.findFilesByFilters(1L, 2L, 3L, 4L, "PPTs")).thenReturn(List.of(fd));

        List<FileData> result = storageService.findFilesByFilters(1L, 2L, 3L, 4L, "PPTs");
        assertThat(result).hasSize(1);
        verify(fileDAO).findFilesByFilters(1L, 2L, 3L, 4L, "PPTs");
    }

    @Test
    void findResourcesUploadedDelegatesToDAO() {
        FileData fd = new FileData();
        when(fileDAO.findAllByOwnersName("teacher1")).thenReturn(List.of(fd));

        List<FileData> result = storageService.findResourcesUploaded("teacher1");
        assertThat(result).hasSize(1);
    }

    @Test
    void getFileByIdDelegatesToDAO() {
        FileData fd = new FileData();
        when(fileDAO.findById(1L)).thenReturn(Optional.of(fd));

        Optional<FileData> result = storageService.getFileById(1L);
        assertThat(result).isPresent();
    }

    @Test
    void compressFileWithGzipProducesNonEmptyStream() throws IOException {
        Path file = tempDir.resolve("sample.txt");
        Files.writeString(file, "hello world test content");

        InputStream compressed = storageService.compressFileWithGzip(file.toString());
        assertThat(compressed.available()).isGreaterThan(0);
    }

    @Test
    void compressFileWithZipProducesNonEmptyStream() throws IOException {
        Path file = tempDir.resolve("sample.txt");
        Files.writeString(file, "hello world test content");

        InputStream compressed = storageService.compressFileWithZip(file.toString());
        assertThat(compressed.available()).isGreaterThan(0);
    }
}
