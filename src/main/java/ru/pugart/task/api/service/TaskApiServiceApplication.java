package ru.pugart.task.api.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactivefeign.spring.config.EnableReactiveFeignClients;

@SpringBootApplication
@EnableReactiveFeignClients
public class TaskApiServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskApiServiceApplication.class, args);
    }

}
