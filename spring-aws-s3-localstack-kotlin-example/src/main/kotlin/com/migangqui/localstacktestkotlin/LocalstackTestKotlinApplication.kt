package com.migangqui.localstacktestkotlin

import com.migangqui.spring.aws.s3.bean.UploadFileResult
import com.migangqui.spring.aws.s3.service.AmazonS3Service
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

@EnableAsync
@ComponentScan("com.migangqui")
@SpringBootApplication
class LocalstackTestKotlinApplication

fun main(args: Array<String>) {
    runApplication<LocalstackTestKotlinApplication>(*args)
}

@RestController
@RequestMapping("/api/media")
class MediaController(val mediaService: AmazonS3Service) {
    @PostMapping
    @Throws(IOException::class)
    fun uploadFile(@RequestBody file: MultipartFile, @RequestParam name: String): UploadFileResult {
        val result = mediaService.uploadFile(file.bytes, "folder", name, file.contentType!!)
        return result
    }
}
