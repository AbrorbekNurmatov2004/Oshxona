package oshxona.oshxona.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import oshxona.oshxona.criteria.BaseCriteria;
import oshxona.oshxona.criteria.DataList;
import oshxona.oshxona.dto.user.UserCreateDto;
import oshxona.oshxona.dto.user.UserDto;
import oshxona.oshxona.mapper.UserMapper;
import oshxona.oshxona.model.User;
import oshxona.oshxona.repository.UserRepository;
import oshxona.oshxona.validator.UserValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private UserMapper mapper;

    @Mock
    private UserValidator validator;

    @InjectMocks
    private UserService service;

    private UserCreateDto createDto;
    private UserDto expectedUserDto;
    private User user;
    private User savedUser;
    private BaseCriteria criteria;

    @BeforeEach
    void setUp() {
        createDto = new UserCreateDto();
        createDto.setPhone("+998901112233");

        user = new User();
        user.setPhone("+998901112233");

        savedUser = new User();
        savedUser.setId("uuid-123");
        savedUser.setPhone("+998901112233");

        expectedUserDto = new UserDto();
        expectedUserDto.setId("uuid-123");
        expectedUserDto.setPhone("+998901112233");

        criteria = new BaseCriteria();
        criteria.setPage(0);
        criteria.setSize(10);
        criteria.setSearch("Ali");
    }

    @Test
    void create_shouldReturnUserDto_whenDataIsTrue() {
        when(mapper.fromDto(createDto)).thenReturn(user);
        doNothing().when(validator).validateUniquePhone(createDto.getPhone());
        when(repository.save(user)).thenReturn(savedUser);
        when(mapper.toDto(savedUser)).thenReturn(expectedUserDto);

        UserDto userDto = service.create(createDto);

        assertNotNull(userDto);
        assertEquals(expectedUserDto.getId(), userDto.getId());
        assertEquals(expectedUserDto.getPhone(), userDto.getPhone());

        verify(repository, times(1)).save(user);
    }

    @Test
    void create_shouldThrowException_whenPhoneIsAlreadyExist() {
        when(mapper.fromDto(createDto)).thenReturn(user);
        doThrow(new IllegalArgumentException("Bu telefon raqami allaqachon mavjud"))
                .when(validator).validateUniquePhone(createDto.getPhone());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.create(createDto);
        });

        assertEquals("Bu telefon raqami allaqachon mavjud", exception.getMessage());
        verify(repository, never()).save(any(User.class));
    }

    @Test
    void create_shouldThrowException_whenPhoneIsNull() {
        createDto.setPhone(null);
        user.setPhone(null);

        when(mapper.fromDto(createDto)).thenReturn(user);
        doThrow(new RuntimeException("Telefon raqami bosh bolmasligi kerak"))
                .when(validator).validateUniquePhone(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.create(createDto);
        });

        assertEquals("Telefon raqami bosh bolmasligi kerak", exception.getMessage());
        verify(repository, never()).save(any(User.class));
    }

    @Test
    void getAll_shouldReturnDataList_WhenUsersExistInDb() {
        User user1 = new User();
        user1.setId("uuid-1234");
        User user2 = new User();
        user2.setId("uuid-1235");

        Pageable pageable = PageRequest.of(criteria.getPage(), criteria.getSize());
        Page<User> pageUser = new PageImpl<>(List.of(user1, user2), pageable, 2);

        UserDto userDto1 = new UserDto();
        userDto1.setId("uuid-1234");
        UserDto userDto2 = new UserDto();
        userDto2.setId("uuid-1235");
        List<UserDto> dtos = List.of(userDto1, userDto2);

        when(repository.findAllUsers(pageable, criteria.getSearch())).thenReturn(pageUser);
        when(mapper.toDto(pageUser.getContent())).thenReturn(dtos);

        DataList<List<UserDto>> res = service.getAll(criteria);

        assertNotNull(res);
        assertEquals(2, res.getData().size());
        assertEquals(1, res.getTotalPages());
        assertEquals(2, res.getAllElements());
    }

    @Test
    void getAll_shouldReturnDataList_WhenNoUsersFound() {
        Pageable pageable = PageRequest.of(criteria.getPage(), criteria.getSize());
        Page<User> empty = new PageImpl<>(List.of(), pageable, 0);

        when(repository.findAllUsers(pageable, criteria.getSearch())).thenReturn(empty);
        when(mapper.toDto(empty.getContent())).thenReturn(List.of());

        DataList<List<UserDto>> res = service.getAll(criteria);

        assertNotNull(res);
        assertTrue(res.getData().isEmpty());
        assertEquals(0, res.getTotalPages());
        assertEquals(0, res.getAllElements());
    }

    @Test
    void get_shouldReturnUserDto_WhenUserExist() {
        String userId = "uuid-123";
        when(validator.existAndGet(userId)).thenReturn(user);
        when(mapper.toDto(user)).thenReturn(expectedUserDto);

        UserDto userDto = service.get(userId);

        assertNotNull(userDto);
        assertEquals(expectedUserDto.getId(), userDto.getId());
    }

    @Test
    void get_shouldReturnUserDto_WhenUserNotFound() {
        when(validator.existAndGet(null)).thenThrow(new UsernameNotFoundException("User not found"));

        assertThrows(UsernameNotFoundException.class, () -> {
            service.get(null);
        });
    }
}