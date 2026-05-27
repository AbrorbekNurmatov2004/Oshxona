package oshxona.oshxona.validator;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import oshxona.oshxona.model.Permission;
import oshxona.oshxona.repository.PermissionRepository;
import oshxona.oshxona.utils.ErrorConstants;

@Component
@RequiredArgsConstructor
public class PermissionValidator {

    private final PermissionRepository permissionRepository;

    public Permission existsAndGet(String id) {
        return permissionRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(ErrorConstants.s_NOT_FOUND.formatted("permission"))
        );
    }
}
