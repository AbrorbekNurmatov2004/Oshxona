package oshxona.oshxona.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import oshxona.oshxona.exception.ResourceNotFoundException;
import oshxona.oshxona.model.Food;
import oshxona.oshxona.repository.FoodRepository;
import oshxona.oshxona.utils.ErrorConstants;

@Component
@RequiredArgsConstructor
public class FoodValidator {

    private final FoodRepository repository;

    public Food existAndGet(String id) {
        return repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(ErrorConstants.s_NOT_FOUND.formatted("food"))
        );
    }

    public Food getFoodCode(String foodCode) {
        return repository.findByCode(foodCode).orElseThrow(
                () -> new ResourceNotFoundException(ErrorConstants.s_NOT_FOUND.formatted("food code"))
        );
    }
}
