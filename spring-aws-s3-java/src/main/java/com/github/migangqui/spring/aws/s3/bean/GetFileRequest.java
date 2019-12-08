package com.github.migangqui.spring.aws.s3.bean;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetFileRequest {
    private String path;
    private String bucketName;
}
