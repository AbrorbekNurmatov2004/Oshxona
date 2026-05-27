package oshxona.oshxona.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import oshxona.oshxona.criteria.BaseCriteria;
import oshxona.oshxona.criteria.DataList;
import oshxona.oshxona.dto.role.RoleCreateDto;
import oshxona.oshxona.dto.role.RoleDto;
import oshxona.oshxona.dto.role.RoleUpdateDto;
import oshxona.oshxona.exception.BadRequestException;
import oshxona.oshxona.mapper.RoleMapper;
import oshxona.oshxona.model.Permission;
import oshxona.oshxona.model.Role;
import oshxona.oshxona.repository.RoleRepository;
import oshxona.oshxona.service.base.AbstractService;
import oshxona.oshxona.service.base.CRUDService;
import oshxona.oshxona.utils.ErrorConstants;
import oshxona.oshxona.validator.RoleValidator;

import java.util.List;

@Service
public class RoleService extends AbstractService<RoleRepository, RoleMapper, RoleValidator>
        implements CRUDService<RoleDto, RoleUpdateDto, RoleCreateDto, BaseCriteria, String> {

    public RoleService(RoleRepository repository, RoleMapper mapper, RoleValidator validator) {
        super(repository, mapper, validator);
    }

    public DataList<List<RoleDto>> getAll(BaseCriteria criteria) {
        Pageable pageable = PageRequest.of(criteria.getPage(), criteria.getSize());
        Page<Role> roles = repository.findAllRoles(pageable, criteria.getSearch());
        List<RoleDto> dto = mapper.toDto(roles.getContent());
        return DataList.<List<RoleDto>>builder()
                .data(dto)
                .totalPages(roles.getTotalPages())
                .allElements(roles.getTotalElements())
                .build();
    }

    @Override
    public RoleDto get(String id) {
        Role role = validator.existsAndGet(id);
        return mapper.toDto(role);
    }

    @Override
    public RoleDto create(RoleCreateDto dto) {
        validator.existByCode(dto.getCode());
        Role role = mapper.fromDto(dto);
        return mapper.toDto(repository.save(role));
    }

    @Override
    public RoleDto update(String id, RoleUpdateDto dto) {
        Role role = validator.existsAndGet(id);
        mapper.fromDto(role, dto);
        return mapper.toDto(repository.save(role));
    }

    @Override
    public void delete(String id) {
        try {
            repository.deleteById(id);
        } catch (Exception e) {
            throw new BadRequestException(ErrorConstants.CANT_DELETE_ROLE);
        }
    }

    public RoleUpdateDto getForUpdate(String id) {
        Role role = validator.existsAndGet(id);
        RoleUpdateDto dto = new RoleUpdateDto();
        dto.setName(role.getName());
        dto.setCode(role.getCode());
        List<String> currentPermissionIds = role.getPermissions().stream()
                .map(Permission::getId).toList();
        dto.setPermissionIds(currentPermissionIds);
        return dto;
    }
}
