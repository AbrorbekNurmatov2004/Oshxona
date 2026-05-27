package oshxona.oshxona.mapper;

import org.springframework.stereotype.Component;
import oshxona.oshxona.dto.permission.PermissionCreateDto;
import oshxona.oshxona.dto.permission.PermissionDto;
import oshxona.oshxona.dto.permission.PermissionUpdateDto;
import oshxona.oshxona.model.Permission;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PermissionMapper {

    public List<PermissionDto> toDto(List<Permission> permissions) {
        if (permissions == null) {
            return Collections.emptyList();
        }
        return permissions.stream().map(this::toDto).collect(Collectors.toList());
    }

    public PermissionDto toDto(Permission permission) {
        if (permission == null) return null;
        return PermissionDto.builder()
                .id(permission.getId())
                .name(permission.getName())
                .code(permission.getCode())
                .build();
    }

    public Permission fromDto(PermissionCreateDto dto) {
        Permission permission = new Permission();
        permission.setName(dto.getName());
        permission.setCode(dto.getCode());
        return permission;
    }

    public void fromDto(PermissionUpdateDto dto, Permission permission) {
        permission.setName(dto.getName());
        permission.setCode(dto.getCode());
    }
}
