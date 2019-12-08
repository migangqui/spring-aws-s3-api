package com.github.migangqui.spring.aws.s3.bean;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Getter
@Builder
public class UploadFileRequest {
    @NonNull
    private InputStream stream;
    @NonNull
    private String folder;
    @NonNull
    private String name;
    @NonNull
    private String contentType;
    private String bucketName;
    @Builder.Default
    private CannedAccessControlList accessControl = CannedAccessControlList.Private;
}
