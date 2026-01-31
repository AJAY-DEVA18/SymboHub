package com.symbohub.symbohub_backend.config;



import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private JwtProperties jwt;
    private FileProperties file;
    private AdminProperties admin;

    @Data
    public static class JwtProperties {
        private String secret;
        private Long expiration;
    }

    @Data
    public static class FileProperties {
        private String uploadDir;
        private String allowedExtensions;
    }

    @Data
    public static class AdminProperties {
        private String username;
        private String password;
        private String email;
    }
}
