package com.github.migangqui.spring.aws.s3.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.github.migangqui.spring.aws.s3.property.AmazonS3Properties
import com.github.migangqui.spring.aws.s3.service.AmazonS3Service
import com.github.migangqui.spring.aws.s3.service.AmazonS3ServiceImpl
import mu.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

@Configuration
class AmazonS3Config() {

    private val log = KotlinLogging.logger {}

    @Bean
    fun amazonS3Properties(env: Environment): AmazonS3Properties {
        return AmazonS3Properties(env)
    }

    @Bean
    fun amazonS3Client(amazonS3Properties: AmazonS3Properties): AmazonS3 {
        return if (amazonS3Properties.isLocalstackEnabled) {
            log.info("Registering AmazonS3Client (with Localstack)")
            AmazonS3ClientBuilder.standard()
                    .withEndpointConfiguration(EndpointConfiguration(amazonS3Properties.localstackEndpoint, amazonS3Properties.localstackRegion))
                    .withPathStyleAccessEnabled(true)
                    .build()
        } else {
            log.info("Registering AmazonS3Client")
            AmazonS3ClientBuilder.standard()
                    .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials(amazonS3Properties.s3AccessKey, amazonS3Properties.s3SecretKey)))
                    .withRegion(amazonS3Properties.region)
                    .build()
        }
    }

    @Bean
    fun amazonS3Service(amazonS3Client: AmazonS3): AmazonS3Service {
        return AmazonS3ServiceImpl(amazonS3Client)
    }
}