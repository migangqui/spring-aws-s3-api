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
import com.github.migangqui.spring.aws.s3.bean.UploadFileRequest;
import com.github.migangqui.spring.aws.s3.bean.UploadFileResponse;
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
    public UploadFileResponse uploadFile(UploadFileRequest uploadFileRequest) {
        UploadFileResponse result;

        try {
            String bucketName = Optional.ofNullable(
                    Optional.ofNullable(uploadFileRequest.getBucketName()).orElse(defaultBucketName))
                    .orElseThrow(() -> new NoBucketException("Bucket name not indicated"));

            InputStream streamToUpload = clone(uploadFileRequest.getStream());

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(IOUtils.toByteArray(uploadFileRequest.getStream()).length);

            if (!StringUtils.isEmpty(uploadFileRequest.getContentType())) {
                metadata.setContentType(uploadFileRequest.getContentType());
                metadata.setCacheControl("s-maxage");
            }

            String path = uploadFileRequest.getFolder().concat("/").concat(uploadFileRequest.getName());

            PutObjectRequest request = new PutObjectRequest(bucketName, path, streamToUpload, metadata)
                    .withCannedAcl(uploadFileRequest.getAccessControl());

            log.debug("Uploading file to {}", path);

            amazonS3Client.putObject(request);

            result = UploadFileResponse.builder().fileName(uploadFileRequest.getName()).status(HttpStatus.SC_OK).build();
        } catch (AmazonServiceException ase) {
            showAmazonServiceExceptionUploadFileLogs(ase);
            result = UploadFileResponse.builder().fileName(uploadFileRequest.getName()).status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .cause(ase.getErrorMessage()).exception(ase).build();
        } catch (AmazonClientException ace) {
            showAmazonClientExceptionUploadFileLogs(ace);
            result = UploadFileResponse.builder().fileName(uploadFileRequest.getName()).status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .cause(ace.getMessage()).exception(ace).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result = UploadFileResponse.builder().fileName(uploadFileRequest.getName()).status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .cause(e.getMessage()).exception(e).build();
        }
        return result;
    }

    @Async
    @Override
    public Future<UploadFileResponse> uploadFileAsync(UploadFileRequest uploadFileRequest) {
        return new AsyncResult<>(uploadFile(uploadFileRequest));
    }

    @Override
    public InputStream getFile(String path) {
        log.info("Reading file from AmazonS3 {}", path);
        S3Object s3Object = amazonS3Client.getObject(new GetObjectRequest(defaultBucketName, path));
        return s3Object.getObjectContent();
    }

    @Override
    public boolean deleteFile(String path) {
        log.info("Deleting file from path {}", path);
        boolean result = false;
        try {
            DeleteObjectRequest request = new DeleteObjectRequest(defaultBucketName, path);
            amazonS3Client.deleteObject(request);
            result = true;
        } catch (AmazonServiceException ase) {
            showAmazonServiceExceptionUploadFileLogs(ase);
        } catch (AmazonClientException ace) {
            showAmazonClientExceptionUploadFileLogs(ace);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
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
}



