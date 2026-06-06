package oshxona.oshxona.mapper;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import oshxona.oshxona.dto.IdNameDTO;
import oshxona.oshxona.dto.user.UserCreateDto;
import oshxona.oshxona.dto.user.UserDto;
import oshxona.oshxona.dto.user.UserUpdateDto;
import oshxona.oshxona.model.Role;
import oshxona.oshxona.model.User;
import oshxona.oshxona.validator.RoleValidator;

import java.util.List;

@Component
public class UserMapper {

    private final PasswordEncoder passwordEncoder;
    private final RoleValidator roleValidator;

    public UserMapper(PasswordEncoder passwordEncoder, RoleValidator roleValidator) {
        this.passwordEncoder = passwordEncoder;
        this.roleValidator = roleValidator;
    }

    public List<UserDto> toDto(List<User> users) {
        return users.stream().map(this::toDto).toList();
    }

    public UserDto toDto(User user) {
        if (user == null) return null;
        return UserDto.builder().
                id(user.getId()).
                fullName(user.getFullName()).
                phone(user.getPhone()).
                superAdmin(user.getSuperAdmin()).
                role(toIdNameDto(user.getRole())).
                profileImageUrl(user.getProfileImage() != null ? user.getProfileImage() : null).
                build();
    }

    private IdNameDTO toIdNameDto(Role role) {
        if (role == null) return null;
        return IdNameDTO.builder().
                id(role.getId()).
                name(role.getName()).
                build();
    }

    public User fromDto(UserCreateDto dto) {
        if (dto == null) return null;
        User user = new User();
        user.setFullName(dto.getFullName());
        user.setPhone(dto.getPhone());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setSuperAdmin(false);
        if (dto.getRoleId() != null) {
            user.setRole(roleValidator.existsAndGet(dto.getRoleId()));
        }
        return user;
    }

    public void fromDto(UserUpdateDto dto, User user) {
        if (dto == null || user == null) return;
        user.setFullName(dto.getFullName());
        user.setPhone(dto.getPhone());
        if (dto.getRoleId() != null) {
            user.setRole(roleValidator.existsAndGet(dto.getRoleId()));
        } else {
            if (!Boolean.TRUE.equals(user.getSuperAdmin())) {
                user.setRole(null);
            }
        }
    }
}
