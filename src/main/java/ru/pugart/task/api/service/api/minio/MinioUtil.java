package ru.pugart.task.api.service.api.minio;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.internal.http2.ErrorCode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class MinioUtil {

    private final MinioClient minioClient;
    private final Long fileSize;

    public MinioUtil(MinioClient minioClient, Long fileSize) {
        this.minioClient = minioClient;
        this.fileSize = fileSize;
    }

    @SneakyThrows
    public boolean bucketExists(String bucketName) {
        boolean result = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        log.info(result ? bucketName + " exists" : bucketName + " does not exist");
        return result;
    }

    @SneakyThrows
    public boolean makeBucket(String bucketName) {
        if (!bucketExists(bucketName)) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(bucketName)
                            .build());

            return true;
        }
        return false;
    }

    @SneakyThrows
    public void putObject(String bucketName, InputStream content, String filename, String fileType) {
        minioClient.putObject(
                PutObjectArgs.builder().bucket(bucketName).object(filename).stream(
                        content, -1, fileSize)
                        .contentType(fileType)
                        .build());
    }

    public boolean removeObject(String bucketName, String objectName) {
        if (bucketExists(bucketName)) {
            try {
                minioClient.removeObject(
                        RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());

                return !objectExits(bucketName, objectName);
            } catch (Exception ex) {
                log.error(ex.getLocalizedMessage(), ex);
                return false;
            }
        }
        return false;
    }

    private boolean objectExits(String bucketName, String objectName){
        StatObjectResponse statObject = statObject(bucketName, objectName);
        return statObject != null && statObject.size() > 0;
    }

    @SneakyThrows
    public InputStream getObject(String bucketName, String objectName) {
        if (bucketExists(bucketName)) {

            if (objectExits(bucketName, objectName)) {
                return minioClient.getObject(
                                GetObjectArgs.builder()
                                        .bucket(bucketName)
                                        .object(objectName)
                                        .build());
            }
        }
        return null;
    }

    public StatObjectResponse statObject(String bucketName, String objectName) {
        if (bucketExists(bucketName)) {
            try {
                return minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
            } catch (Exception ex) {
                log.warn(ex.getLocalizedMessage());
            }
        }
        return null;
    }

    @SneakyThrows
    public boolean removeObject(String bucketName, List<String> objectNames) {
        boolean result = true;

        if (bucketExists(bucketName)) {
            List<DeleteObject> objects = objectNames.stream()
                    .map(DeleteObject::new)
                    .collect(Collectors.toList());

            Iterable<Result<DeleteError>> results =
                    minioClient.removeObjects(
                            RemoveObjectsArgs.builder().bucket(bucketName).objects(objects).build());

            for (Result<DeleteError> r : results) {
                try {
                    DeleteError error = r.get();
                    log.info(String.format("Error in deleting object %s; Error message: %s", error.objectName(), error));
                } catch (Exception ex) {
                    log.error(ex.getLocalizedMessage(), ex);
                }
                result = false;
            }
        }

        return result;
    }
}
