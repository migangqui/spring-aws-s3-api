package com.github.migangqui.spring.aws.s3.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.github.migangqui.spring.aws.s3.property.AmazonS3Properties;
import com.github.migangqui.spring.aws.s3.service.AmazonS3Service;
import com.github.migangqui.spring.aws.s3.service.AmazonS3ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import static com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;

@Slf4j
@Configuration
public class AmazonS3Config {

    @Bean
    public AmazonS3Properties amazonS3Properties(Environment env) {
        return new AmazonS3Properties(env);
    }

    @Bean
    public AmazonS3 amazonS3Client(AmazonS3Properties amazonS3Properties) {
        AmazonS3 client;

        if (amazonS3Properties.isLocalstackEnabled()) {
            log.info("Registering AmazonS3Client (with Localstack)");
            client = AmazonS3ClientBuilder.standard()
                    .withEndpointConfiguration(new EndpointConfiguration(amazonS3Properties.getLocalstackEndpoint(), amazonS3Properties.getLocalstackRegion()))
                    .withPathStyleAccessEnabled(true)
                    .build();
        } else {
            log.info("Registering AmazonS3Client");
            client = AmazonS3ClientBuilder
                    .standard()
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(amazonS3Properties.getS3AccessKey(), amazonS3Properties.getS3SecretKey())))
                    .withRegion(amazonS3Properties.getRegion())
                    .build();
        }
        return client;
    }

    @Bean
    public AmazonS3Service amazonS3Service(AmazonS3 amazonS3Client) {
        return new AmazonS3ServiceImpl(amazonS3Client);
    }

}
