package ru.pugart.task.api.service.api.minio;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.List;

@Slf4j
public class MinioService implements MinioApi {

    private final MinioUtil minioUtil;
    private String mediaType = "application/octet-stream";
    private final String bucketName;

    public MinioService(MinioClient minioClient, Long fileSize, String mediaType, String bucketName) {
        this.minioUtil = new MinioUtil(minioClient, fileSize);
        if(mediaType != null) {
            this.mediaType = mediaType;
        }
        this.bucketName = bucketName;
    }

    @PostConstruct
    private void init(){
        makeBucket(bucketName);
    }

    @Override
    public boolean bucketExists(String bucketName) {
        return minioUtil.bucketExists(bucketName);
    }


    @Override
    public void makeBucket(String bucketName) {
        minioUtil.makeBucket(bucketName);
    }

    @Override
    public String putObject(InputStream content, String fileName) {
        try {
            if (!this.bucketExists(bucketName)) {
                this.makeBucket(bucketName);
            }

            minioUtil.putObject(bucketName, content, fileName, mediaType);
            log.info(String.format("%s/%s", bucketName, fileName));
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public InputStream downloadObject(String bucketName, String objectName) {
        return minioUtil.getObject(bucketName == null ? this.bucketName : bucketName, objectName);
    }

    @Override
    public boolean removeObject(String bucketName, String objectName) {
        return minioUtil.removeObject(bucketName == null ? this.bucketName : bucketName, objectName);
    }

    @Override
    public boolean removeListObject(String bucketName, List<String> objectNameList) {
        return minioUtil.removeObject(bucketName,objectNameList);
    }
}
