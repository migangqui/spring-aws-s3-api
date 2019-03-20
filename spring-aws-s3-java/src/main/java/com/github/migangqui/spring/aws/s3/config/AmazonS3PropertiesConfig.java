package com.github.migangqui.spring.aws.s3.config;

import com.github.migangqui.spring.aws.s3.property.AmazonS3Properties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AmazonS3Properties.class)
public class AmazonS3PropertiesConfig {

}
