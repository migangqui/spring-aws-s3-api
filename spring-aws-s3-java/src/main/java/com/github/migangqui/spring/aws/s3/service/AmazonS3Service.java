package com.github.migangqui.spring.aws.s3.service;

import com.github.migangqui.spring.aws.s3.bean.UploadFileResult;

import java.io.InputStream;
import java.util.concurrent.Future;

public interface AmazonS3Service {
	
	UploadFileResult uploadFile(InputStream stream, String folder, String name, String contentType);

	UploadFileResult uploadFile(byte[] bytes, String folder, String name, String contentType);

	Future<UploadFileResult> uploadFileAsync(InputStream stream, String folder, String name, String contentType);

	Future<UploadFileResult> uploadFileAsync(byte[] bytes, String folder, String name, String contentType);
	
	InputStream getFile(String path);
	
	boolean deleteFile(String path);

}
