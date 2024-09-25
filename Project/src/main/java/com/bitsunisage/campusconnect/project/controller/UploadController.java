package com.bitsunisage.campusconnect.project.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;

import java.io.File;
import java.io.IOException;

@Controller
public class UploadController {

    @PostMapping("/uploadPPTs/upload")
    public String uploadPPTs(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload.");
            return "redirect:/uploadPPTsPage";
        }

        try {
            // Save the uploaded file to a local directory
            String uploadDirectory = "uploads/";
            File uploadDir = new File(uploadDirectory);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();  // Create directory if it doesn't exist
            }

            // Save the file
            file.transferTo(new File(uploadDirectory + file.getOriginalFilename()));
            redirectAttributes.addFlashAttribute("message", "File uploaded successfully: " + file.getOriginalFilename());

        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "File upload failed. Please try again.");
        }

        return "redirect:/uploadPPTsPage";
    }
    @PostMapping("/uploadLectureNotes")
    public String handleLectureNotesUpload(@RequestParam("file") MultipartFile file, Model model) {
        if (!file.isEmpty()) {
            // Logic to save the file (you can save it to a directory or database)
            // Example: file.transferTo(new File("/path/to/save/" + file.getOriginalFilename()));
            model.addAttribute("message", "Lecture notes uploaded successfully!");
        } else {
            model.addAttribute("message", "Please select a file to upload.");
        }
        return "teacherViewPages/uploadNotes";
    }

}
