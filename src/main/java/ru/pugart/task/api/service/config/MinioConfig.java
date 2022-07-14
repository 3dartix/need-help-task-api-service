package ru.pugart.task.api.service.config;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.pugart.task.api.service.api.StorageApi;
import ru.pugart.task.api.service.api.minio.MinioService;
import ru.pugart.task.api.service.api.minio.MinioStore;


@ConfigurationProperties(prefix = "file.storage")
@Configuration
@Data
public class MinioConfig {
    private String endpoint;
    private Integer port;
    private String accessKey;
    private String secretKey;
    private Boolean secure;
    private String bucketName;
    private Long fileSize;

    @Bean
    public StorageApi storeApi(){
        MinioClient minioClient =
                MinioClient.builder()
                        .credentials(accessKey, secretKey)
                        .endpoint(endpoint, port, secure)
                        .build();

        MinioService minioService = new MinioService(minioClient, fileSize, "application/octet-stream", bucketName);
        return new MinioStore(minioService);
    }
}
