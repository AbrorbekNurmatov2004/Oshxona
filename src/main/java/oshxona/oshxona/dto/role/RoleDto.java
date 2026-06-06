package oshxona.oshxona.dto.role;

import lombok.*;
import oshxona.oshxona.dto.permission.PermissionDto;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleDto {
    private String id;
    private String name;
    private String code;
    private List<PermissionDto> permissions;
}
