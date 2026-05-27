package oshxona.oshxona.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import oshxona.oshxona.model.FileEntity;

public interface FileRepository extends JpaRepository<FileEntity,String> {
}
