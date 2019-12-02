package com.github.migangqui.spring.aws.s3.bean

import java.io.ByteArrayInputStream
import java.io.InputStream

class UploadFileRequest(var stream: InputStream,
                        var folder: String,
                        var name: String,
                        var contentType: String,
                        var bucketName: String? = null) {


    constructor(bytes: ByteArray?, folder: String, name: String, contentType: String) : this(ByteArrayInputStream(bytes), folder, name, contentType)

    constructor(bytes: ByteArray?, folder: String, name: String, contentType: String, bucketName: String?) : this(ByteArrayInputStream(bytes), folder, name, contentType, bucketName)
}