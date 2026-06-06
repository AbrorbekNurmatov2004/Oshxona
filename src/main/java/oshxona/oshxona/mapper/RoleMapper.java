package oshxona.oshxona.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import oshxona.oshxona.dto.permission.PermissionDto;
import oshxona.oshxona.dto.role.RoleCreateDto;
import oshxona.oshxona.dto.role.RoleDto;
import oshxona.oshxona.dto.role.RoleUpdateDto;
import oshxona.oshxona.exception.ResourceNotFoundException;
import oshxona.oshxona.model.Permission;
import oshxona.oshxona.model.Role;
import oshxona.oshxona.repository.PermissionRepository;
import oshxona.oshxona.repository.RoleRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RoleMapper {

    private final PermissionMapper permissionMapper;
    private final PermissionRepository permissionRepository;

    public List<RoleDto> toDto(List<Role> roles) {
        if (roles == null) {
            return Collections.emptyList();
        }
        return roles.stream().map(this::toDto).collect(Collectors.toList());
    }

    public RoleDto toDto(Role role) {
        List<PermissionDto> permissions = permissionMapper.toDto(role.getPermissions());
        return RoleDto.builder().
                id(role.getId()).
                name(role.getName()).
                code(role.getCode()).
                permissions(permissions).
                build();
    }

    public Role fromDto(RoleCreateDto dto) {
        List<Permission> permissions = permissionRepository.findAllByIdIn(dto.getPermissionIds());
        Role role = new Role();
        role.setName(dto.getName());
        role.setCode(dto.getCode());
        role.setPermissions(permissions);
        return role;
    }

    public void fromDto(Role role, RoleUpdateDto dto) {
        List<Permission> permissions = permissionRepository.findAllByIdIn(dto.getPermissionIds());
        role.setName(dto.getName());
        role.setCode(dto.getCode());
        role.setPermissions(permissions);
    }
}
