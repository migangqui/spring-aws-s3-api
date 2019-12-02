package com.github.migangqui.spring.aws.s3.service

import com.amazonaws.AmazonClientException
import com.amazonaws.AmazonServiceException
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.*
import com.amazonaws.util.IOUtils
import com.github.migangqui.spring.aws.s3.bean.UploadFileRequest
import com.github.migangqui.spring.aws.s3.bean.UploadFileResponse
import mu.KotlinLogging
import org.apache.http.HttpStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.AsyncResult
import org.springframework.util.StringUtils
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.*
import java.util.concurrent.Future

class AmazonS3ServiceImpl(private val s3Client: AmazonS3) : AmazonS3Service {

    private val log = KotlinLogging.logger {}

    @Value("\${amazon.s3.bucket.name:us-east-1}")
    private lateinit var defaultBucketName: String

    override fun uploadFile(uploadFileRequest: UploadFileRequest): UploadFileResponse {
        val bucketName = Optional.ofNullable(
                Optional.ofNullable(uploadFileRequest.bucketName).orElse(defaultBucketName))
                .orElseThrow { RuntimeException("Bucket name not indicated") }
        return try {
            val streamToUpload = uploadFileRequest.stream.clone()

            val metadata = ObjectMetadata()
            metadata.contentLength = IOUtils.toByteArray(uploadFileRequest.stream).size.toLong()

            if (!StringUtils.isEmpty(uploadFileRequest.contentType)) {
                metadata.contentType = uploadFileRequest.contentType
                metadata.cacheControl = "s-maxage"
            }

            val path = "$uploadFileRequest.folder/$uploadFileRequest.name"

            val request = PutObjectRequest(bucketName, path, streamToUpload, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead)

            log.debug("Uploading file to $path")

            s3Client.putObject(request)

            UploadFileResponse(fileName = uploadFileRequest.name, status = HttpStatus.SC_OK)
        } catch (ase: AmazonServiceException) {
            showAmazonServiceExceptionUploadFileLogs(ase)
            UploadFileResponse(uploadFileRequest.name, HttpStatus.SC_INTERNAL_SERVER_ERROR, ase.errorMessage, ase)
        } catch (ace: AmazonClientException) {
            showAmazonClientExceptionUploadFileLogs(ace)
            UploadFileResponse(uploadFileRequest.name, HttpStatus.SC_INTERNAL_SERVER_ERROR, ace.message, ace)
        } catch (e: Exception) {
            log.error(e.message, e)
            UploadFileResponse(uploadFileRequest.name, HttpStatus.SC_INTERNAL_SERVER_ERROR, e.message, e)
        }
    }

    @Async
    override fun uploadFileAsync(uploadFileRequest: UploadFileRequest): Future<UploadFileResponse> {
        return AsyncResult(uploadFile(uploadFileRequest))
    }

    override fun getFile(path: String): InputStream {
        log.info("Reading file from AmazonS3 $path")
        val s3Object = s3Client.getObject(GetObjectRequest(defaultBucketName, path))
        return s3Object.objectContent
    }

    override fun deleteFile(path: String): Boolean {
        log.info("Deleting file from path $path")
        return try {
            val request = DeleteObjectRequest(defaultBucketName, path)
            s3Client.deleteObject(request)
            true
        } catch (ase: AmazonServiceException) {
            showAmazonServiceExceptionUploadFileLogs(ase)
            false
        } catch (ace: AmazonClientException) {
            showAmazonClientExceptionUploadFileLogs(ace)
            false
        } catch (e: Exception) {
            log.error(e.message, e)
            false
        }
    }

    /* Private methods */

    private fun showAmazonServiceExceptionUploadFileLogs(ase: AmazonServiceException) {
        log.error("Caught an AmazonServiceException, which means your request made it " +
                "to Amazon S3, but was rejected with an error response for some reason.")
        log.error("Error Message:    ${ase.message}")
        log.error("HTTP Status Code: ${ase.statusCode}")
        log.error("AWS Error Code:   ${ase.errorCode}")
        log.error("Error Type:       ${ase.errorType}")
        log.error("Request ID:       ${ase.requestId}")
    }

    private fun showAmazonClientExceptionUploadFileLogs(ace: AmazonClientException) {
        log.error("Caught an AmazonClientException, which means the client encountered " +
                "an internal error while trying to communicate with S3, such as not being able to access the network.")
        log.error("Error Message: ${ace.message}")
    }

    private fun InputStream.clone(): InputStream? {
        var result: InputStream? = null
        try {
            this.mark(0)
            val outputStream = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var readLength = this.read(buffer)
            while ((readLength) != -1) {
                outputStream.write(buffer, 0, readLength)
                readLength = this.read(buffer)
            }
            this.reset()
            outputStream.flush()
            result = ByteArrayInputStream(outputStream.toByteArray())
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return result
    }
}