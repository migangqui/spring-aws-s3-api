package com.github.migangqui.spring.aws.s3.service;

import com.github.migangqui.spring.aws.s3.bean.UploadFileRequest;
import com.github.migangqui.spring.aws.s3.bean.UploadFileResponse;

import java.io.InputStream;
import java.util.concurrent.Future;

public interface AmazonS3Service {
	
	UploadFileResponse uploadFile(UploadFileRequest uploadFileRequest);

	Future<UploadFileResponse> uploadFileAsync(UploadFileRequest uploadFileRequest);

	InputStream getFile(String path);
	
	boolean deleteFile(String path);

}
