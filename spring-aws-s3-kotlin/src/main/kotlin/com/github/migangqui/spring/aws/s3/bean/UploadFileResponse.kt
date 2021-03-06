package com.github.migangqui.spring.aws.s3.bean

data class UploadFileResponse(var fileName: String? = null,
                            var status: Int = 0,
                            var cause: String? = null,
                            var exception: Exception? = null)