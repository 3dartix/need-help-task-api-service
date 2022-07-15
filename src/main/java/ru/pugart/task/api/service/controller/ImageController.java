package ru.pugart.task.api.service.controller;

import feign.form.FormData;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @GetMapping(value = "download/{image}")
    public ResponseEntity<Mono<InputStreamResource>> download(@PathVariable String image){
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + image.split("-")[1])
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE).body(imageService.download(image));
    }
}
