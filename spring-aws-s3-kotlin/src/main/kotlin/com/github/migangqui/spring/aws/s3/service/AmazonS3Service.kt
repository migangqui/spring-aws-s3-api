package com.github.migangqui.spring.aws.s3.service

import com.github.migangqui.spring.aws.s3.bean.*
import java.io.InputStream
import java.util.concurrent.Future

interface AmazonS3Service {
    fun uploadFile(request: UploadFileRequest): UploadFileResponse

    fun uploadFileAsync(request: UploadFileRequest): Future<UploadFileResponse>

    fun getFile(request: GetFileRequest): GetFileResponse

    fun deleteFile(request: DeleteFileRequest): DeleteFileResponse
}