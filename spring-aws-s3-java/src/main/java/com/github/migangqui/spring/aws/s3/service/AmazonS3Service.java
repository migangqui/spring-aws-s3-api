package com.github.migangqui.spring.aws.s3.service;

import com.github.migangqui.spring.aws.s3.bean.*;

import java.io.InputStream;
import java.util.concurrent.Future;

public interface AmazonS3Service {
	
	UploadFileResponse uploadFile(UploadFileRequest request);

	Future<UploadFileResponse> uploadFileAsync(UploadFileRequest request);

	GetFileResponse getFile(GetFileRequest request);
	
	DeleteFileResponse deleteFile(DeleteFileRequest request);

}
