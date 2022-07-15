package ru.pugart.task.api.service.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.pugart.task.api.service.api.StorageApi;
import ru.pugart.task.api.service.config.AppConfig;
import ru.pugart.task.api.service.repository.entity.Task;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
@Slf4j
public class ImageService implements ImageApi {

    private final TaskService taskService;
    private final StorageApi storageApi;
    private final AppConfig appConfig;

    private final static String FILE_NAME_FORMAT = "%s-%s"; // uuid-filename

    private Mono<Task> basicCheck(String author, String taskId){
        return taskService.getTaskById(Mono.just(taskId))
                .log()
                .filter(Objects::nonNull)
                .switchIfEmpty(Mono.error(new RuntimeException("error: task with id: {} not found")))
                .filter(task -> task.getAuthor() != null && task.getAuthor().equals(author))
                .switchIfEmpty(Mono.error(new RuntimeException("forbidden: not enough rights")));
    }

//    @Override
//    public Mono<Task> store(String author, String taskId, Flux<FilePart> files) {
//        long startMillis = System.currentTimeMillis();
//        return files
//                .subscribeOn(Schedulers.boundedElastic())
//                .log()
//                .filter(dataBuffer -> {
//                    String fileExtension = FilenameUtils.getExtension(dataBuffer.filename());
//                    log.info("fileExtension: {}", fileExtension);
//                    return ALLOWED_EXTENSION.stream()
//                            .anyMatch(extension -> extension.equalsIgnoreCase(fileExtension));
//                })
//                .flatMap(multipartFile -> {
//                    return multipartFile.content().reduce(InputStream.nullInputStream(),
//                            (inputStream, dataBuffer) -> new SequenceInputStream(inputStream, dataBuffer.asInputStream()));
//                })
//                .flatMap(inputStream -> Flux.just(storageApi.upload(inputStream, String.format(FILE_NAME_FORMAT, generateUUID(), "test.jpg"))))
//                .collectList()
//                .flatMap(images -> {
//                    log.info("upload file execution time {} ms", System.currentTimeMillis() - startMillis);
//                    return taskService.addImages(Mono.just(taskId), images);
//                })
//                .switchIfEmpty(Mono.empty());
//    }

    @Override
    public Mono<Task> remove(String author, String taskId, Flux<String> images) {
        return basicCheck(author, taskId)
                .log()
                .flatMap(task ->
                        images
                                .log()
                                .flatMap(image -> {
                                    if(storageApi.delete(image)){
                                        log.info("####: {}", image);
                                        return Flux.just(image);
                                    } else {
                                        log.warn("error: file {} not removed", image);
                                        return Flux.empty();
                                    }
                                })
                                .collectList()
                        .switchIfEmpty(Mono.empty())
                )
                .flatMap(deletedImages -> taskService.deletedImages(Mono.just(taskId), deletedImages))
                .switchIfEmpty(Mono.empty());
    }

    @Override
    public Mono<InputStreamResource> download(String image) {
        return Mono.fromCallable(() -> {
            InputStream imageStream = storageApi.download(image);
            return new InputStreamResource(imageStream);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Task> store(String author, String taskId, Flux<FilePart> files) {
        long startMillis = System.currentTimeMillis();
        return basicCheck(author, taskId)
                .flatMap(task -> {
                        return files.ofType(FilePart.class)
                                .flatMap(this::sendFileToStorage)
                                .collectList();
                })
                .flatMap(images -> {
                    log.info("upload file execution time {} ms", System.currentTimeMillis() - startMillis);
                    return taskService.addImages(Mono.just(taskId), images);
                });
    }

    private Flux<String> sendFileToStorage(FilePart file) {
        return Mono.just(file)
                .filter(dataBuffer -> {
                    String fileExtension = FilenameUtils.getExtension(file.filename());
                    log.info("fileExtension: {}", fileExtension);
                    return appConfig.getAllowedFileExtension().stream()
                            .anyMatch(extension -> extension.equalsIgnoreCase(fileExtension));
                })
                .switchIfEmpty(Mono.error(new RuntimeException("error: extension not allowed")))
                .flatMapMany(f -> {
                    return file.content().reduce(InputStream.nullInputStream(),
                            (inputStream, dataBuffer) -> new SequenceInputStream(inputStream, dataBuffer.asInputStream()));
                })
                .flatMap(inputStream -> Flux.just(storageApi.upload(inputStream, String.format(FILE_NAME_FORMAT, generateUUID(), file.filename()))))
                .switchIfEmpty(Flux.empty());
    }

    private String generateUUID(){
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }
}
