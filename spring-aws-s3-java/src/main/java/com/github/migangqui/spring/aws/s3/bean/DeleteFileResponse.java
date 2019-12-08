package com.github.migangqui.spring.aws.s3.bean;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;

@Getter
@Setter
@Builder
public class DeleteFileResponse {
	private boolean result;
	private int status;
	private String cause;
	private Exception exception;
}
