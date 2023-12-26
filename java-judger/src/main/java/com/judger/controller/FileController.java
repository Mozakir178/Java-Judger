package com.judger.controller;

import com.judger.service.FileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class FileController {

    @PostMapping("/upload")
    public ResponseEntity<String> takeResponses(@RequestParam("file") MultipartFile file){


        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload");
        }

        try {
            // Get the root directory of the project
            String rootDirectory = System.getProperty("user.dir");

            // Specify the path where you want to save the file in the "resources" folder
            Path destination = Paths.get(rootDirectory, "src", "main", "resources", file.getOriginalFilename());

            // Save the file
            Files.write(destination, file.getBytes());

            return ResponseEntity.ok("File uploaded successfully!");
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception according to your needs
            return ResponseEntity.status(500).body("Error occurred while uploading the file");
        }
    }


//    @PostMapping("/uploadcsv")
//    public ResponseEntity<String> takeResponses(@RequestParam("file") MultipartFile file){
//        String result = FileService.uploadFile(file);
//        return new ResponseEntity<>(result, HttpStatus.OK);
//    }
//
//    @PostMapping("/uploadtestcases")
//    public ResponseEntity<String> takeTestCases(@RequestParam("file") MultipartFile file){
//        String result = FileService.uploadFile(file);
//        return new ResponseEntity<>(result, HttpStatus.OK);
//    }
//
//    @PostMapping("/uploadpom")
//    public ResponseEntity<String> takePom(@RequestParam("file") MultipartFile file){
//        String result = FileService.uploadFile(file);
//        return new ResponseEntity<>(result, HttpStatus.OK);
//    }
}
