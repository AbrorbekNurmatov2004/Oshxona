package oshxona.oshxona.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import oshxona.oshxona.dto.user.UserCreateDto;
import oshxona.oshxona.dto.user.UserDto;
import oshxona.oshxona.dto.user.UserUpdateDto;
import oshxona.oshxona.model.Role;
import oshxona.oshxona.model.User;
import oshxona.oshxona.validator.RoleValidator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserMapperTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleValidator roleValidator;

    @InjectMocks
    private UserMapper mapper;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("uuid-123");
        user.setFullName("Ali");
        user.setPhone("+998902223344");
        user.setSuperAdmin(false);
    }

    @Test
    void testFromDto_shouldReturnUser_whenCreateDtoIsValid() {
        UserCreateDto createDto = new UserCreateDto();
        createDto.setFullName("Ali");
        createDto.setPhone("+998901112233");
        createDto.setPassword("password");
        createDto.setRoleId("123");

        Role adminRole = new Role();
        adminRole.setId("123");
        adminRole.setName("ADMIN");

        when(passwordEncoder.encode("password")).thenReturn("encode-password");
        when(roleValidator.existsAndGet("123")).thenReturn(adminRole);

        User result = mapper.fromDto(createDto);

        assertNotNull(result);
        assertEquals("Ali", result.getFullName());
        assertEquals("+998901112233", result.getPhone());
        assertEquals("encode-password", result.getPassword());
        assertFalse(result.getSuperAdmin());
        assertNotNull(result.getRole());
        assertEquals("123", result.getRole().getId());
        assertEquals("ADMIN", result.getRole().getName());
    }

    @Test
    void testFromDto_shouldReturnUser_whenUpdateDtoIsValid() {
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setFullName("Vali");
        userUpdateDto.setPhone("+998901112233");
        userUpdateDto.setRoleId("321");

        Role managerRole = new Role();
        managerRole.setId("321");
        managerRole.setName("MANAGER");

        when(roleValidator.existsAndGet("321")).thenReturn(managerRole);

        mapper.fromDto(userUpdateDto, user);

        assertEquals("Vali", user.getFullName());
        assertEquals("+998901112233", user.getPhone());
        assertFalse(user.getSuperAdmin());
        assertNotNull(user.getRole());
        assertEquals("321", user.getRole().getId());
        assertEquals("MANAGER", user.getRole().getName());
    }

    @Test
    void fromDto_shouldSetRoleToNull_whenRoleIdIsNullAndUserIsNotSuperAdmin() {
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setFullName("Vali");
        userUpdateDto.setPhone("+998901112233");
        userUpdateDto.setRoleId(null);

        Role oldRole = new Role();
        oldRole.setId("123");
        user.setRole(oldRole);

        mapper.fromDto(userUpdateDto, user);

        assertNull(user.getRole());
    }

    @Test
    void toDto_shouldReturnUserDto_WhenUserIsValid() {
        Role userRole = new Role();
        userRole.setId("123");
        userRole.setName("USER");

        User user = new User();
        user.setId("uuid-123");
        user.setFullName("Ali");
        user.setPhone("+998901112233");
        user.setSuperAdmin(false);
        user.setRole(userRole);
        user.setProfileImage("image");

        UserDto resultDto = mapper.toDto(user);

        assertNotNull(resultDto);
        assertEquals("uuid-123", resultDto.getId());
        assertEquals("Ali", resultDto.getFullName());
        assertEquals("+998901112233", resultDto.getPhone());
        assertFalse(resultDto.getSuperAdmin());
        assertEquals("image", resultDto.getProfileImageUrl());

        assertNotNull(resultDto.getRole());
        assertEquals("123", resultDto.getRole().getId());
        assertEquals("USER", resultDto.getRole().getName());
    }
}
