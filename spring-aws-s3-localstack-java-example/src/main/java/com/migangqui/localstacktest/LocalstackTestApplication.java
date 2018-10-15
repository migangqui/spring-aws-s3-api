package com.migangqui.localstacktest;

import com.amazonaws.services.s3.AmazonS3;
import com.migangqui.spring.aws.s3.bean.UploadFileResult;
import com.migangqui.spring.aws.s3.service.AmazonS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/media")
@ComponentScan("com.migangqui")
@SpringBootApplication
public class LocalstackTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(LocalstackTestApplication.class, args);
	}

	@Autowired
	private AmazonS3Service mediaService;

	@PostMapping
	public UploadFileResult uploadFile(@RequestBody MultipartFile file, @RequestParam String name) throws IOException {
		return mediaService.uploadFile(file.getBytes(), "folder",name,file.getContentType());
	}
}
