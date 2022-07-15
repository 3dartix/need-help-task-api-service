package ru.pugart.task.api.service.api.minio;

import lombok.extern.slf4j.Slf4j;
import ru.pugart.task.api.service.api.StorageApi;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
public class MinioStore implements StorageApi {

    private final static String NAME_STORAGE = "minio";

    private final MinioApi minioApi;

    public MinioStore(MinioApi minioApi) {
        this.minioApi = minioApi;
    }

    @Override
    public String upload(InputStream content, String filename) {
        return minioApi.putObject(content, filename);
    }

    @Override
    public InputStream download(String path) {
        try {
            return minioApi.downloadObject(null, path);
//            return Files.readAllBytes(Paths.get(path));
        } catch (Exception ex) {
            log.error(ex.getLocalizedMessage(), ex);
            return null;
        }
    }

    @Override
    public boolean delete(String path) {
        return minioApi.removeObject(null, path);
    }

    @Override
    public boolean delete(List<String> paths) {
        return minioApi.removeListObject(null, paths);
    }

    @Override
    public String getNameStorage() {
        return NAME_STORAGE;
    }
}
