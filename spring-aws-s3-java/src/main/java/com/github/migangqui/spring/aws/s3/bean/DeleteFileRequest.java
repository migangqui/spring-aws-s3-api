package com.github.migangqui.spring.aws.s3.bean;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeleteFileRequest {
    private String path;
    private String bucketName;
}
