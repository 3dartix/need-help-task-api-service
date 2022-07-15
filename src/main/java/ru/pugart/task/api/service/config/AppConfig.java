package ru.pugart.task.api.service.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import ru.pugart.task.api.service.dto.UserDto;

import javax.annotation.PostConstruct;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "app")
@Data
@Slf4j
public class AppConfig {

    private UserGroups userGroups;
    private UserDto credentials;
    private List<String> allowedFileExtension;

    @Data
    public static class UserGroups {
        private String defaultGroup;
        private String adminGroup;
    }

    @PostConstruct
    private void init(){
        log.info("app configuration: {}", this);
    }
}
