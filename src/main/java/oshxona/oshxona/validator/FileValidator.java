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
}
