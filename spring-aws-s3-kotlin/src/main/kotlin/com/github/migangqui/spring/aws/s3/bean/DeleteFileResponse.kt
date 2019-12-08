package com.github.migangqui.spring.aws.s3.bean

import java.lang.Exception

class DeleteFileResponse(val result: Boolean = false, val status: Int, cause: String? = null, val exception: Exception? = null)