package oshxona.oshxona.mapper;

import org.springframework.stereotype.Component;
import oshxona.oshxona.dto.food.FoodCreateDto;
import oshxona.oshxona.dto.food.FoodDto;
import oshxona.oshxona.dto.food.FoodUpdateDto;
import oshxona.oshxona.model.Food;
import java.util.List;

@Component
public class FoodMapper {

    public List<FoodDto> toDto(List<Food> foods) {
        return foods.stream().map(this::toDto).toList();
    }

    public FoodDto toDto(Food food) {
        return FoodDto.builder().
                id(food.getId()).
                name(food.getName()).
                price(food.getPrice()).
                code(food.getCode()).
                description(food.getDescription()).
                image(food.getImage()).
                active(food.isActive()).build();
    }

    public Food fromDto(FoodCreateDto dto) {
        Food food = new Food();
        food.setName(dto.getName());
        food.setPrice(dto.getPrice());
        food.setDescription(dto.getDescription());
        return food;
    }

    public void fromDto(FoodUpdateDto dto, Food food) {
       food.setName(dto.getName());
       food.setPrice(dto.getPrice());
       food.setDescription(dto.getDescription());
    }
}
