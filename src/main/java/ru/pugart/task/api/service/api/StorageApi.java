package ru.pugart.task.api.service.api;

import java.io.InputStream;
import java.util.List;

public interface StorageApi {
    String upload(InputStream content, String filename);
    InputStream download(String path);
    boolean delete(String path);
    boolean delete(List<String> paths);
    String getNameStorage();
}
