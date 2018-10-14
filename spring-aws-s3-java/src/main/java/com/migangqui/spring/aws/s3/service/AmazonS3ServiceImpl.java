package com.migangqui.spring.aws.s3.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.migangqui.spring.aws.s3.bean.UploadFileResult;
import com.migangqui.spring.aws.s3.property.AmazonS3Properties;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Slf4j
@Service
public class AmazonS3ServiceImpl implements AmazonS3Service {

	private AmazonS3 amazonS3Client;

	private AmazonS3Properties properties;

	@Autowired
	public AmazonS3ServiceImpl(AmazonS3 s3Client, AmazonS3Properties properties) {
		this.amazonS3Client = s3Client;
		this.properties = properties;
	}

	@Override
	public UploadFileResult uploadFile(InputStream stream, String folder, String name, String contentType) {
		UploadFileResult result;
		try {
			InputStream streamToUpload = clone(stream);

			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(IOUtils.toByteArray(stream).length);

			if (!StringUtils.isEmpty(contentType)) {
				metadata.setContentType(contentType);
				metadata.setCacheControl("s-maxage");
			}

			String path = folder.concat("/").concat(name);

			uploadFileToS3(path, streamToUpload, metadata);

			result = UploadFileResult.builder().fileName(name).status(HttpStatus.SC_OK).build();
		} catch (AmazonServiceException ase) {
			showAmazonServiceExceptionUploadFileLogs(ase);
			result = UploadFileResult.builder().fileName(name).status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
					.cause(ase.getErrorMessage()).exception(ase).build();
		} catch (AmazonClientException ace) {
			showAmazonClientExceptionUploadFileLogs(ace);
			result = UploadFileResult.builder().fileName(name).status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
					.cause(ace.getMessage()).exception(ace).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result = UploadFileResult.builder().fileName(name).status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
					.cause(e.getMessage()).exception(e).build();
		}
		return result;
	}

	@Override
	public UploadFileResult uploadFile(byte[] bytes, String folder, String name, String contentType) {
		return uploadFile(new ByteArrayInputStream(bytes), folder, name, contentType);
	}
	
	@Async
	@Override
	public UploadFileResult uploadFileAsync(InputStream stream, String folder, String name, String contentType) {
		return uploadFile(stream, folder, name, contentType);
	}

	@Async
	@Override
	public UploadFileResult uploadFileAsync(byte[] bytes, String folder, String name, String contentType) {
		return uploadFileAsync(new ByteArrayInputStream(bytes), folder, name, contentType);
	}

	@Override
	public InputStream getFile(String path) {
		log.info("Reading file from AmazonS3 {}", path);
		S3Object s3Object = amazonS3Client.getObject(new GetObjectRequest(properties.getBucketName(), path));
		return s3Object.getObjectContent();
	}

	@Override
	public boolean deleteFile(String path) {
		log.info("Deleting file from path {}", path);
		boolean result = false;
		try {
			DeleteObjectRequest request = new DeleteObjectRequest(properties.getBucketName(), path);
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

	private void uploadFileToS3(String path, InputStream stream, ObjectMetadata metadata) {
		PutObjectRequest request = new PutObjectRequest(properties.getBucketName(), path, stream, metadata)
				.withCannedAcl(CannedAccessControlList.PublicRead);
		log.debug("Uploading file to {}", path);
		amazonS3Client.putObject(request);
	}

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
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}
}



