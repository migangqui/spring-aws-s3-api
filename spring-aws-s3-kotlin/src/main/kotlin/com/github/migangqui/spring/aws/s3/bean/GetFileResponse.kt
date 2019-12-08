package com.github.migangqui.spring.aws.s3.bean

import java.io.InputStream
import java.lang.Exception

class GetFileResponse(val content: InputStream? = null, val status: Int, cause: String? = null, val exception: Exception? = null)