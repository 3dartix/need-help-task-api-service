package ru.pugart.task.api.service.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.pugart.task.api.service.repository.entity.Task;
import ru.pugart.task.api.service.service.ImageService;

@RestController
@RequestMapping("/image")
@AllArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping(value = "upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Task> store(@RequestParam String author, @RequestParam String taskId, @RequestPart("file") Flux<FilePart> files){
        return imageService.store(author, taskId, files);
    }

    @PostMapping(value = "remove")
    public Mono<Task> remove(@RequestParam String author, @RequestParam String taskId, @RequestBody Flux<String> files){
        return imageService.remove(author, taskId, files);
    }
}
