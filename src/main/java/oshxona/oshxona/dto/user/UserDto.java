package oshxona.oshxona.dto.user;

import lombok.*;
import oshxona.oshxona.dto.IdNameDTO;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private String id;
    private String fullName;
    private String phone;
    private IdNameDTO role;
    private Boolean superAdmin;
    private String profileImageUrl;
}