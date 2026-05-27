package oshxona.oshxona.mapper;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import oshxona.oshxona.model.FileEntity;

@Component
public class FileMapper {

    public FileEntity fromDto(MultipartFile file) {
        FileEntity entity = new FileEntity();
        entity.setSize(file.getSize());
        entity.setContentType(file.getContentType());
        entity.setOriginalName(file.getOriginalFilename());
        return entity;
    }
}
