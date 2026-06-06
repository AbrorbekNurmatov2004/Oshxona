package oshxona.oshxona.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import oshxona.oshxona.model.base.IdEntity;

@Getter
@Setter
@Entity
@Table(name = "file_entity")
public class FileEntity extends IdEntity {

    private String originalName;

    private String generatedName;

    private Long size;

    private String contentType;

    private String path;

    @Column(name = "telegram_file_id")
    private String telegramFileId;
}
