package oshxona.oshxona.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import oshxona.oshxona.criteria.BaseCriteria;
import oshxona.oshxona.criteria.DataList;
import oshxona.oshxona.dto.user.UserCreateDto;
import oshxona.oshxona.dto.user.UserDto;
import oshxona.oshxona.dto.user.UserUpdateDto;
import oshxona.oshxona.mapper.UserMapper;
import oshxona.oshxona.model.User;
import oshxona.oshxona.repository.UserRepository;
import oshxona.oshxona.service.base.AbstractService;
import oshxona.oshxona.service.base.CRUDService;
import oshxona.oshxona.validator.UserValidator;

import java.util.List;

@Service
public class UserService extends AbstractService<UserRepository, UserMapper, UserValidator>
        implements CRUDService<UserDto, UserUpdateDto, UserCreateDto, BaseCriteria, String> {

    public UserService(UserRepository repository, UserMapper mapper, UserValidator validator) {
        super(repository, mapper, validator);
    }

    @Override
    public DataList<List<UserDto>> getAll(BaseCriteria criteria) {
        Pageable pageable = PageRequest.of(criteria.getPage(), criteria.getSize());
        Page<User> doctors = repository.findAllUsers(pageable, criteria.getSearch());
        List<UserDto> dto = mapper.toDto(doctors.getContent());
        return DataList.<List<UserDto>>builder()
                .data(dto)
                .totalPages(doctors.getTotalPages())
                .allElements(doctors.getTotalElements())
                .build();
    }

    @Override
    public UserDto get(String id) {
        User user = validator.existAndGet(id);
        return mapper.toDto(user);
    }

    @Override
    public UserDto create(UserCreateDto dto) {
        User user = mapper.fromDto(dto);
        validator.validateUniquePhone(dto.getPhone());
        return mapper.toDto(repository.save(user));
    }

    @Override
    public UserDto update(String id, UserUpdateDto dto) {
        User user = validator.existAndGet(id);
        validator.validateUniquePhoneForUpdate(dto.getPhone(), id);
        mapper.fromDto(dto, user);
        return mapper.toDto(repository.save(user));
    }

    @Override
    public void delete(String id) {
        User user = validator.existAndGet(id);
        user.setDeleted(true);
        repository.save(user);
    }
}





