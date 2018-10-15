package com.migangqui.localstack.kotlin.example

import com.migangqui.spring.aws.s3.bean.UploadFileResult
import com.migangqui.spring.aws.s3.service.AmazonS3Service
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

@EnableAsync
@ComponentScan("com.migangqui")
@SpringBootApplication
class LocalstackTestApplication

fun main(args: Array<String>) {
    SpringApplication.run(LocalstackTestApplication::class.java, *args)
}

@RestController
@RequestMapping("/api/files")
class MediaController(private val amazonS3Service: AmazonS3Service) {
    @PostMapping
    @Throws(IOException::class)
    fun uploadFile(@RequestBody file: MultipartFile, @RequestParam folder: String, @RequestParam name: String): UploadFileResult {
        return amazonS3Service!!.uploadFile(file.bytes, folder, name, file.contentType!!)
    }
}


