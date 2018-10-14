package com.migangqui.spring.aws.s3.service;

import com.migangqui.spring.aws.s3.bean.UploadFileResult;

import java.io.InputStream;

public interface AmazonS3Service {
	
	UploadFileResult uploadFile(InputStream stream, String folder, String name, String contentType);

	UploadFileResult uploadFile(byte[] bytes, String folder, String name, String contentType);

	UploadFileResult uploadFileAsync(InputStream stream, String folder, String name, String contentType);

	UploadFileResult uploadFileAsync(byte[] bytes, String folder, String name, String contentType);
	
	InputStream getFile(String path);
	
	boolean deleteFile(String path);

}
