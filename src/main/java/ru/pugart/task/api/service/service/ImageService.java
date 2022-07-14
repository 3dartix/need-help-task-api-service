package ru.pugart.task.api.service.service;

import com.google.common.primitives.Bytes;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.pugart.task.api.service.api.StorageApi;
import ru.pugart.task.api.service.repository.entity.Task;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
@Slf4j
public class ImageService implements ImageApi {

    private final TaskService taskService;
    private final StorageApi storageApi;

    private final static String FILE_NAME_FORMAT = "%s-%s"; // uuid-filename
    private final static List<String> ALLOWED_EXTENSION = Arrays.asList("jpg", "png");

    private Mono<Task> basicCheck(String author, String taskId){
        return taskService.getTaskById(Mono.just(taskId))
                .log()
                .filter(Objects::nonNull)
                .switchIfEmpty(Mono.error(new RuntimeException("error: task with id: {} not found")))
                .filter(task -> task.getAuthor() != null && task.getAuthor().equals(author))
                .switchIfEmpty(Mono.error(new RuntimeException("forbidden: not enough rights")));
    }

    @Override
    public Mono<Task> store(String author, String taskId, Flux<FilePart> files) {
        return basicCheck(author, taskId)
                .flatMap(task -> {
                        return files.ofType(FilePart.class)
                                .flatMap(this::save)
                                .collectList();
                })
                .flatMap(images -> taskService.addImages(Mono.just(taskId), images));
    }

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

    private Flux<String> save(FilePart file) {
        return Mono.just(file)
                .filter(dataBuffer -> {
                    String fileExtension = FilenameUtils.getExtension(file.filename());
                    log.info("fileExtension: {}", fileExtension);
                    return ALLOWED_EXTENSION.stream()
                            .anyMatch(extension -> extension.equalsIgnoreCase(fileExtension));
                })
                .switchIfEmpty(Mono.error(new RuntimeException("error: extension not allowed")))
                .flatMapMany(f -> {
                    return file.content()
                            .flatMap(dataBuffer -> {
                                byte[] bytes = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(bytes);
                                DataBufferUtils.release(dataBuffer);
                                return Flux.just(bytes);
                            })
                            .collectList()
                            .flatMapMany(byteList -> {
                                byte[] bytes = byteList.stream().collect(ByteArrayOutputStream::new, (b, e) -> b.write(e, 0, e.length), (a, b) -> {}).toByteArray();
                                String image = storageApi.upload(bytes, String.format(FILE_NAME_FORMAT, generateUUID(), file.filename()));
                                return Flux.just(image);
                            }).switchIfEmpty(Flux.empty());

                })
                .switchIfEmpty(Flux.empty());
    }

    private String generateUUID(){
        return java.util.UUID.randomUUID().toString();
    }
}
