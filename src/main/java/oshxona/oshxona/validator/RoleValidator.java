package oshxona.oshxona.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import oshxona.oshxona.exception.AlreadyExistsException;
import oshxona.oshxona.exception.ResourceNotFoundException;
import oshxona.oshxona.model.Role;
import oshxona.oshxona.repository.RoleRepository;
import oshxona.oshxona.utils.ErrorConstants;

@Component
@RequiredArgsConstructor
public class RoleValidator {

    private final RoleRepository repository;

    public Role existsAndGet(String id) {
        return repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(ErrorConstants.s_NOT_FOUND.formatted("role"))
        );
    }

    public void existByCode(String code) {
        if (repository.existsByCode(code)) {
            throw new AlreadyExistsException(ErrorConstants.ALREADY_EXISTS);
        }
    }
}
