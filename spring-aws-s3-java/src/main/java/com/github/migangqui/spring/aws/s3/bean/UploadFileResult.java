package com.github.migangqui.spring.aws.s3.bean;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UploadFileResult {
	
	private String fileName;
	private int status;
	private String cause;
	private Exception exception;

}
