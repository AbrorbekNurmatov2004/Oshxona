package oshxona.oshxona.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import oshxona.oshxona.criteria.BaseCriteria;
import oshxona.oshxona.criteria.DataList;
import oshxona.oshxona.dto.permission.PermissionCreateDto;
import oshxona.oshxona.dto.permission.PermissionDto;
import oshxona.oshxona.dto.permission.PermissionUpdateDto;
import oshxona.oshxona.exception.BadRequestException;
import oshxona.oshxona.mapper.PermissionMapper;
import oshxona.oshxona.model.Permission;
import oshxona.oshxona.repository.PermissionRepository;
import oshxona.oshxona.repository.RoleRepository;
import oshxona.oshxona.service.base.AbstractService;
import oshxona.oshxona.service.base.CRUDService;
import oshxona.oshxona.utils.ErrorConstants;
import oshxona.oshxona.validator.PermissionValidator;
import java.util.List;

@Service
public class PermissionService extends AbstractService<PermissionRepository, PermissionMapper, PermissionValidator>
        implements CRUDService<PermissionDto, PermissionUpdateDto, PermissionCreateDto, BaseCriteria, String> {

    private final RoleRepository roleRepository;

    public PermissionService(PermissionRepository repository, PermissionMapper mapper, PermissionValidator validator, RoleRepository roleRepository) {
        super(repository, mapper, validator);
        this.roleRepository = roleRepository;
    }

    @Override
    public DataList<List<PermissionDto>> getAll(BaseCriteria criteria) {
        Pageable pageable = PageRequest.of(criteria.getPage(), criteria.getSize());
        Page<Permission> permissions = repository.findAllByCriteria(criteria.getSearch(), pageable);
        List<PermissionDto> dto = mapper.toDto(permissions.getContent());
        return DataList.<List<PermissionDto>>builder()
                .data(dto)
                .totalPages(permissions.getTotalPages())
                .allElements(permissions.getTotalElements())
                .build();
    }

    @Override
    public PermissionDto get(String id) {
        Permission permission = validator.existsAndGet(id);
        return mapper.toDto(permission);
    }

    @Override
    public PermissionDto create(PermissionCreateDto dto) {
        Permission permission = mapper.fromDto(dto);
        Permission saved = repository.save(permission);
        return mapper.toDto(saved);
    }

    @Override
    public PermissionDto update(String id, PermissionUpdateDto dto) {
        Permission permission = validator.existsAndGet(id);
        mapper.fromDto(dto, permission);
        return mapper.toDto(repository.save(permission));
    }

    @Override
    public void delete(String id) {
        Permission permission = validator.existsAndGet(id);
        if (roleRepository.existsByPermissionsContains(permission)) {
            throw new BadRequestException(ErrorConstants.CANT_DELETE_PERMISSION);
        }
        repository.delete(permission);
    }
}
