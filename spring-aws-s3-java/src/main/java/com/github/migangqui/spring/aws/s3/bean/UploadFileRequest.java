package com.github.migangqui.spring.aws.s3.bean;

import lombok.Getter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Getter
public class UploadFileRequest {
    private InputStream stream;
    private String folder;
    private String name;
    private String contentType;
    private String bucketName;

    public UploadFileRequest(InputStream stream, String folder, String name, String contentType) {
        this.stream = stream;
        this.folder = folder;
        this.name = name;
        this.contentType = contentType;
    }

    public UploadFileRequest(InputStream stream, String folder, String name, String contentType, String bucketName) {
        this.stream = stream;
        this.folder = folder;
        this.name = name;
        this.contentType = contentType;
        this.bucketName = bucketName;
    }

    public UploadFileRequest(byte[] bytes, String folder, String name, String contentType) {
        this.stream = new ByteArrayInputStream(bytes);
        this.folder = folder;
        this.name = name;
        this.contentType = contentType;
    }

    public UploadFileRequest(byte[] bytes, String folder, String name, String contentType, String bucketName) {
        this.stream = new ByteArrayInputStream(bytes);
        this.folder = folder;
        this.name = name;
        this.contentType = contentType;
        this.bucketName = bucketName;
    }
}
