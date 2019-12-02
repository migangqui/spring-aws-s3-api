package com.github.migangqui.spring.aws.s3.property;

import org.springframework.core.env.Environment;

public class AmazonS3Properties {
	
	private static final String BUCKET_NAME = "amazon.s3.bucket.name";
	private static final String S3_ACCESS_KEY = "amazon.s3.accessKey";
	private static final String S3_SECRET_KEY = "amazon.s3.secretKey";
	private static final String REGION = "amazon.region";

	private static final String LOCALSTACK_ENABLED = "localstack.enabled";
	private static final String LOCALSTACK_ENDPOINT = "localstack.endpoint";
	private static final String LOCALSTACK_REGION = "localstack.region";

	private Environment env;

	public AmazonS3Properties(Environment env) {
		this.env = env;
	}

	public String getBucketName() {
		return env.getProperty(BUCKET_NAME);
	}
	public String getS3AccessKey() {
		return env.getProperty(S3_ACCESS_KEY);
	}
	public String getS3SecretKey() {
		return env.getProperty(S3_SECRET_KEY);
	}
	public String getRegion() {
		return env.getProperty(REGION);
	}
	public Boolean isLocalstackEnabled() {
		return env.getProperty(LOCALSTACK_ENABLED) != null ? env.getProperty(LOCALSTACK_ENABLED, Boolean.class) : Boolean.FALSE;
	}
	public String getLocalstackEndpoint() {
		return env.getProperty(LOCALSTACK_ENDPOINT);
	}
	public String getLocalstackRegion() {
		return env.getProperty(LOCALSTACK_REGION);
	}

}
