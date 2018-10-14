package com.migangqui.spring.aws.s3.service

import com.migangqui.spring.aws.s3.bean.UploadFileResult
import java.io.InputStream

interface AmazonS3Service {

    fun uploadFile(stream: InputStream, folder: String, name: String, contentType: String): UploadFileResult

    fun uploadFile(bytes: ByteArray, folder: String, name: String, contentType: String): UploadFileResult

    fun uploadFileAsync(stream: InputStream, folder: String, name: String, contentType: String): UploadFileResult

    fun uploadFileAsync(bytes: ByteArray, folder: String, name: String, contentType: String): UploadFileResult

    fun getFile(path: String): InputStream

    fun deleteFile(path: String): Boolean
}