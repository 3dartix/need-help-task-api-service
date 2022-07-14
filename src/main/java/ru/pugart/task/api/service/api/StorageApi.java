package ru.pugart.task.api.service.api;

import java.util.List;

public interface StorageApi {
    String upload(byte[] content, String filename);
    byte[] download(String path);
    boolean delete(String path);
    boolean delete(List<String> paths);
    String getNameStorage();
}
