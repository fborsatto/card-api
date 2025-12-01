package com.hyperativa.card_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/cards/batch")
@RequiredArgsConstructor
public class BatchController {

    private final JobLauncher jobLauncher;
    private final Job job;

    @PostMapping
    public ResponseEntity<String> importCsv(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = "upload_" + UUID.randomUUID() + ".txt";
            Path tempPath = Paths.get(System.getProperty("java.io.tmpdir"), fileName);
            Files.copy(file.getInputStream(), tempPath);

            var jobParameters = new JobParametersBuilder()
                    .addString("fullPathFileName", tempPath.toString())
                    .addLong("startAt", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(job, jobParameters);

            return ResponseEntity.accepted().body("Processing file via Spring Batch! ID: " + fileName);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Initialize batch error: " + e.getMessage());
        }
    }
}