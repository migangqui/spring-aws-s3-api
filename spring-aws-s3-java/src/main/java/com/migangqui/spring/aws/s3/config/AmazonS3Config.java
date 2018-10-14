package com.migangqui.spring.aws.s3.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import static com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.migangqui.spring.aws.s3.property.AmazonS3Properties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AmazonS3Config {

    @Autowired
    private AmazonS3Properties properties;

    @Bean
    public AmazonS3 amazonS3Client() {
        AmazonS3 client;

        if (properties.isLocalstackEnabled()) {
            log.info("Registering AmazonS3Client (with Localstack)");
            client = AmazonS3ClientBuilder.standard()
                    .withEndpointConfiguration(new EndpointConfiguration(properties.getLocalstackEndpoint(), properties.getLocalstackRegion()))
                    .withPathStyleAccessEnabled(true)
                    .build();
        } else {
            log.info("Registering AmazonS3Client");
            client = AmazonS3ClientBuilder.standard()
                    .withCredentials(awsS3CredentialsProvider())
                    .withRegion(properties.getRegion())
                    .build();
        }
        return client;
    }

    private AWSCredentialsProvider awsS3CredentialsProvider() {
        return new AWSCredentialsProvider() {

            public AWSCredentials getCredentials() {
                return new BasicAWSCredentials(
                        properties.getS3AccessKey(), properties.getS3SecretKey());
            }

            public void refresh() {
                // Not implemented cause not necesary
            }
        };
    }

}
