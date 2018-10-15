package com.migangqui.spring.aws.s3.bean

data class UploadFileResult(private val fileName: String? = null,
                            private val status: Int = 0,
                            private val cause: String? = null,
                            private val exception: Exception? = null) {
    constructor(fileName: String?, status: Int) : this(fileName, status, null, null)
}