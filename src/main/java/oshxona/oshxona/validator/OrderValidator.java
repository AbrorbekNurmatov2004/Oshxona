package oshxona.oshxona.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import oshxona.oshxona.exception.ResourceNotFoundException;
import oshxona.oshxona.model.Order;
import oshxona.oshxona.repository.OrderRepository;
import oshxona.oshxona.utils.ErrorConstants;

@Component
@RequiredArgsConstructor
public class OrderValidator {

    private final OrderRepository repository;

    public Order existAndGet(String id) {
        return repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(ErrorConstants.s_NOT_FOUND.formatted("order"))
        );
    }
}
