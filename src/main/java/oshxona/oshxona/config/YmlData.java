package oshxona.oshxona.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import java.util.List;

@Getter
@Configuration
public class YmlData {

    @Value("${application.file-path}")
    private String filePath;

    @Value("${application.max-file-size}")
    private DataSize maxFileSize;

    @Value("${application.bot-username}")
    private String username;

    @Value("${application.bot-token:null}")
    private String token;

    @Value("${application.allowed-types}")
    private List<String> allowedTypes;

}
