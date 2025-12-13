package com.wic.edu.kg.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

/**
 * Cloudflare R2 存储配置
 */
@Configuration
public class R2StorageConfig {
    
    @Value("${r2.account-id}")
    private String accountId;
    
    @Value("${r2.access-key-id}")
    private String accessKeyId;
    
    @Value("${r2.secret-access-key}")
    private String secretAccessKey;
    
    @Bean
    public S3Client s3Client() {
        String endpoint = String.format("https://%s.r2.cloudflarestorage.com", accountId);
        
        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKeyId, secretAccessKey)
                ))
                .region(Region.of("auto"))
                .forcePathStyle(true)
                .build();
    }
    
    @Bean
    public S3Presigner s3Presigner() {
        String endpoint = String.format("https://%s.r2.cloudflarestorage.com", accountId);
        
        return S3Presigner.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKeyId, secretAccessKey)
                ))
                .region(Region.of("auto"))
                .build();
    }
}
