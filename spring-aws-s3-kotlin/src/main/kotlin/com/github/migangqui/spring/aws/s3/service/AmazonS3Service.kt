package com.github.migangqui.spring.aws.s3.service

import com.github.migangqui.spring.aws.s3.bean.UploadFileRequest
import com.github.migangqui.spring.aws.s3.bean.UploadFileResponse
import java.io.InputStream
import java.util.concurrent.Future

interface AmazonS3Service {
    fun uploadFile(uploadFileRequest: UploadFileRequest): UploadFileResponse

    fun uploadFileAsync(uploadFileRequest: UploadFileRequest): Future<UploadFileResponse>

    fun getFile(path: String): InputStream

    fun deleteFile(path: String): Boolean
}