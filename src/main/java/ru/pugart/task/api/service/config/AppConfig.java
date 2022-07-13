package ru.pugart.task.api.service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import ru.pugart.task.api.service.dto.UserDto;

@Configuration
@ConfigurationProperties(prefix = "app")
@Data
public class AppConfig {

    private UserGroups userGroups;
    private UserDto credentials;

    @Data
    public static class UserGroups {
        private String defaultGroup;
        private String adminGroup;
    }
}
