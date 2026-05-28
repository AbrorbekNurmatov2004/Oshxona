package oshxona.oshxona.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import oshxona.oshxona.config.YmlData;
import oshxona.oshxona.exception.BadRequestException;
import oshxona.oshxona.exception.ResourceNotFoundException;
import oshxona.oshxona.model.FileEntity;
import oshxona.oshxona.repository.FileRepository;
import oshxona.oshxona.utils.ErrorConstants;

@Component
@RequiredArgsConstructor
public class FileValidator {

    private final FileRepository repository;
    private final YmlData ymlData;

    public FileEntity existAndGet(String id) {
        return repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(ErrorConstants.s_NOT_FOUND.formatted("file"))
        );
    }

    public void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException(ErrorConstants.FILE_NOT_EXISTS);
        }
        if (file.getSize() > ymlData.getMaxFileSize().toBytes()) {
            throw new BadRequestException(ErrorConstants.FILE_SIZE + ymlData.getMaxFileSize().toMegabytes() + " mb");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ymlData.getAllowedTypes().contains(contentType.toLowerCase())) {
            throw new BadRequestException(ErrorConstants.FILE_CONTENT_TYPE);
        }
    }

   /* spring:
    datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://localhost:5432/oshxona_db
    username: postgres
    password: 2004

    servlet:
    multipart:
    max-file-size: 10MB
    max-request-size: 10MB

    flyway:
    enabled: true
    baseline-on-migrate: true
    locations: classpath:db/migration

    jpa:
    hibernate:
    ddl-auto: update

    application:
    file-path: D:/G59_JAVA/Modul_9/Oshxona/src/main/resources/images/
    max-file-size: 10MB
    allowed-types: image/jpeg,image/jpg,image/png
    bot-token: 8899144345:AAFuXPGgC__1S7EgapYIFcWf9nm9PSQXWTc
    bot-username: OshpazUzBot*/
}
