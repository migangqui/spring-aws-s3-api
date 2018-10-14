package com.migangqui.spring.aws.s3.config

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.migangqui.spring.aws.s3.property.AmazonS3Properties
import mu.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AmazonS3Config(private val properties: AmazonS3Properties) {

    private val log = KotlinLogging.logger {}

    @Bean
    fun amazonS3Client(): AmazonS3 {
        return if (properties.isLocalstackEnabled) {
            log.info("Registering AmazonS3Client (with Localstack)")
            AmazonS3ClientBuilder.standard()
                    .withEndpointConfiguration(EndpointConfiguration(properties.localstackEndpoint, properties.localstackRegion))
                    .withPathStyleAccessEnabled(true)
                    .build()
        } else {
            log.info("Registering AmazonS3Client")
            AmazonS3ClientBuilder.standard()
                    .withCredentials(awsS3CredentialsProvider())
                    .withRegion(properties.region)
                    .build()
        }
    }

    private fun awsS3CredentialsProvider(): AWSCredentialsProvider {
        return object : AWSCredentialsProvider {
            override fun getCredentials(): AWSCredentials {
                return BasicAWSCredentials(
                        properties.s3AccessKey, properties.s3SecretKey)
            }

            override fun refresh() {
                // Not implemented cause not necesary
            }
        }
    }

}