package com.github.migangqui.spring.aws.s3.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.github.migangqui.spring.aws.s3.bean.*;
import com.github.migangqui.spring.aws.s3.exception.NoBucketException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Optional;
import java.util.concurrent.Future;

@Slf4j
public class AmazonS3ServiceImpl implements AmazonS3Service {

    @Value("${amazon.s3.bucket.name}")
    private String defaultBucketName;

    private AmazonS3 amazonS3Client;

    public AmazonS3ServiceImpl(AmazonS3 s3Client) {
        this.amazonS3Client = s3Client;
    }

    @Override
    public UploadFileResponse uploadFile(UploadFileRequest request) {
        UploadFileResponse result;

        try {
            InputStream streamToUpload = clone(request.getStream());

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(IOUtils.toByteArray(request.getStream()).length);

            if (!StringUtils.isEmpty(request.getContentType())) {
                metadata.setContentType(request.getContentType());
                metadata.setCacheControl("s-maxage");
            }

            String path = request.getFolder().concat("/").concat(request.getName());

            PutObjectRequest putObjectRequest = new PutObjectRequest(getBucketName(request.getBucketName()), path, streamToUpload, metadata)
                    .withCannedAcl(request.getAccessControl());

            log.debug("Uploading file to {}", path);

            amazonS3Client.putObject(putObjectRequest);

            result = UploadFileResponse.builder().fileName(request.getName()).status(HttpStatus.SC_OK).build();
        } catch (AmazonServiceException ase) {
            showAmazonServiceExceptionUploadFileLogs(ase);
            result = UploadFileResponse.builder().fileName(request.getName()).status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .cause(ase.getErrorMessage()).exception(ase).build();
        } catch (AmazonClientException ace) {
            showAmazonClientExceptionUploadFileLogs(ace);
            result = UploadFileResponse.builder().fileName(request.getName()).status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .cause(ace.getMessage()).exception(ace).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result = UploadFileResponse.builder().fileName(request.getName()).status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .cause(e.getMessage()).exception(e).build();
        }
        return result;
    }

    @Async
    @Override
    public Future<UploadFileResponse> uploadFileAsync(UploadFileRequest request) {
        return new AsyncResult<>(uploadFile(request));
    }

    @Override
    public GetFileResponse getFile(GetFileRequest request) {
        log.info("Reading file from AmazonS3 {}", request.getPath());
        GetFileResponse result;
        try {
            S3Object s3Object = amazonS3Client.getObject(new GetObjectRequest(getBucketName(request.getBucketName()), request.getPath()));
            result = GetFileResponse.builder().content(s3Object.getObjectContent()).status(HttpStatus.SC_OK).build();
        } catch (NoBucketException e) {
            log.error(e.getMessage(), e);
            result = GetFileResponse.builder().cause(e.getMessage()).exception(e).status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        }
        return result;
    }

    @Override
    public DeleteFileResponse deleteFile(DeleteFileRequest request) {
        log.info("Deleting file from path {}", request.getPath());
        DeleteFileResponse result;
        try {
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(getBucketName(request.getBucketName()), request.getPath());
            amazonS3Client.deleteObject(deleteObjectRequest);
            result = DeleteFileResponse.builder().result(true).status(HttpStatus.SC_OK).build();
        } catch (AmazonServiceException ase) {
            showAmazonServiceExceptionUploadFileLogs(ase);
            result = DeleteFileResponse.builder().cause(ase.getMessage()).exception(ase).status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        } catch (AmazonClientException ace) {
            showAmazonClientExceptionUploadFileLogs(ace);
            result = DeleteFileResponse.builder().cause(ace.getMessage()).exception(ace).status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result = DeleteFileResponse.builder().cause(e.getMessage()).exception(e).status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        }
        return result;
    }

    /* Private methods */

    private void showAmazonServiceExceptionUploadFileLogs(AmazonServiceException ase) {
        log.error("Caught an AmazonServiceException, which means your request made it "
                + "to Amazon S3, but was rejected with an error response for some reason.");
        log.error("Error Message:    {}", ase.getMessage());
        log.error("HTTP Status Code: {}", ase.getStatusCode());
        log.error("AWS Error Code:   {}", ase.getErrorCode());
        log.error("Error Type:       {}", ase.getErrorType());
        log.error("Request ID:       {}", ase.getRequestId());
    }

    private void showAmazonClientExceptionUploadFileLogs(AmazonClientException ace) {
        log.error("Caught an AmazonClientException, which means the client encountered "
                + "an internal error while trying to communicate with S3, such as not being able to access the network.");
        log.error("Error Message: " + ace.getMessage());
    }

    private InputStream clone(final InputStream inputStream) {
        InputStream result = null;
        try {
            inputStream.mark(0);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int readLength = 0;
            while ((readLength = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, readLength);
            }
            inputStream.reset();
            outputStream.flush();
            result = new ByteArrayInputStream(outputStream.toByteArray());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    private String getBucketName(String bucketName) throws NoBucketException {
        return Optional.ofNullable(
                Optional.ofNullable(bucketName).orElse(defaultBucketName))
                .orElseThrow(() -> new NoBucketException("Bucket name not indicated"));
    }
}



