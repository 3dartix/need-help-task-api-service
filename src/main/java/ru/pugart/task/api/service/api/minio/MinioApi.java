package ru.pugart.task.api.service.api.minio;

import java.io.InputStream;
import java.util.List;

public interface MinioApi {

    boolean bucketExists(String bucketName);

    void makeBucket(String bucketName);

    String putObject(InputStream content, String fileName);

    InputStream downloadObject(String bucketName, String objectName);

    boolean removeObject(String bucketName, String objectName);

    boolean removeListObject(String bucketName, List<String> objectNameList);
}
