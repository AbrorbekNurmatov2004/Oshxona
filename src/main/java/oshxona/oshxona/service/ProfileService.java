package oshxona.oshxona.service;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import oshxona.oshxona.config.CustomUserDetails;
import oshxona.oshxona.config.YmlData;
import oshxona.oshxona.dto.user.ChangePasswordDto;
import oshxona.oshxona.mapper.FileMapper;
import oshxona.oshxona.model.FileEntity;
import oshxona.oshxona.model.User;
import oshxona.oshxona.repository.FileRepository;
import oshxona.oshxona.repository.UserRepository;
import oshxona.oshxona.service.base.AbstractService;
import oshxona.oshxona.utils.SecurityUtils;
import oshxona.oshxona.validator.FileValidator;
import oshxona.oshxona.validator.UserValidator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

@Service
public class ProfileService extends AbstractService<FileRepository, FileMapper, FileValidator> {

    private final YmlData ymlData;
    private final UserValidator userValidator;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public ProfileService(FileRepository repository, FileMapper mapper, FileValidator validator, YmlData ymlData, UserValidator userValidator, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        super(repository, mapper, validator);
        this.ymlData = ymlData;
        this.userValidator = userValidator;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public void upload(MultipartFile file) {
        validator.validate(file);
        FileEntity savedFile = saveFileDb(file);
        CustomUserDetails currentUser = SecurityUtils.sessionUser();
        User user = userValidator.existAndGet(currentUser.getId());
        user.setProfileImage("/profile/download/" + savedFile.getId());
        userRepository.save(user);
    }

    public FileEntity saveFileDb(MultipartFile file) {
        Map<String, String> upload = uploadDB(file, ymlData.getFilePath());
        String filePath = upload.get("path");
        String generatedName = upload.get("generatedName");
        FileEntity entity = mapper.fromDto(file);
        entity.setPath(filePath);
        entity.setGeneratedName(generatedName);
        return repository.save(entity);
    }

    public ResponseEntity<FileSystemResource> download(String id) {
        FileEntity entity = validator.existAndGet(id);
        FileSystemResource resource = new FileSystemResource(entity.getPath());
        String originalName = resource.getFilename();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; attachment; filename=\"" + extractOriginalFileName(originalName) + "\"")
                .contentType(MediaType.parseMediaType(entity.getContentType()))
                .body(resource);
    }

    private Map<String, String> uploadDB(MultipartFile file, String filePath) {
        String generatedName = UUID.randomUUID().toString().replace("-", "") + "_" + file.getOriginalFilename();
        Path path = Path.of(filePath, generatedName);
        try {
            Path parentDir = path.getParent();
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
            }
            return Map.of("path", path.toString(), "generatedName", generatedName);
        } catch (IOException e) {
            throw new RuntimeException("Failed while uploading file", e);
        }
    }

    public void changePassword(ChangePasswordDto dto) {
        CustomUserDetails currentUser = SecurityUtils.sessionUser();
        userValidator.validateChangePassword(dto, currentUser);
        User user = userValidator.existAndGet(currentUser.getId());
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }

    private String extractOriginalFileName(String originalName) {
        return originalName.substring(originalName.indexOf("_") + 1);
    }
}
