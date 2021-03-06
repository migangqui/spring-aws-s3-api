package com.github.migangqui.localstack.java.example;

import com.github.migangqui.spring.aws.s3.bean.UploadFileRequest;
import com.github.migangqui.spring.aws.s3.bean.UploadFileResponse;
import com.github.migangqui.spring.aws.s3.service.AmazonS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
@RequestMapping
@EnableAsync
@SpringBootApplication
public class LocalstackTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(LocalstackTestApplication.class, args);
    }

    @Autowired
    private AmazonS3Service amazonS3Service;

    @PostMapping("/api/files")
    public UploadFileResponse uploadFile(@RequestBody MultipartFile file, @RequestParam String folder, @RequestParam String name) throws IOException {
        return amazonS3Service.uploadFile(UploadFileRequest.builder().stream(new ByteArrayInputStream(file.getBytes())).folder(folder).name(name).contentType(file.getContentType()).build());
    }

}
