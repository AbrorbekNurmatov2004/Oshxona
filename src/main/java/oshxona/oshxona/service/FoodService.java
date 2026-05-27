package oshxona.oshxona.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import oshxona.oshxona.criteria.BaseCriteria;
import oshxona.oshxona.criteria.DataList;
import oshxona.oshxona.dto.food.FoodCreateDto;
import oshxona.oshxona.dto.food.FoodDto;
import oshxona.oshxona.dto.food.FoodUpdateDto;
import oshxona.oshxona.mapper.FoodMapper;
import oshxona.oshxona.model.FileEntity;
import oshxona.oshxona.model.Food;
import oshxona.oshxona.repository.FoodRepository;
import oshxona.oshxona.service.base.AbstractService;
import oshxona.oshxona.service.base.CRUDService;
import oshxona.oshxona.validator.FoodValidator;

import java.util.List;

@Service
public class FoodService extends AbstractService<FoodRepository, FoodMapper, FoodValidator>
        implements CRUDService<FoodDto, FoodUpdateDto, FoodCreateDto, BaseCriteria, String> {

    private final ProfileService profileService;

    public FoodService(FoodRepository repository, FoodMapper mapper, FoodValidator validator, ProfileService profileService) {
        super(repository, mapper, validator);
        this.profileService = profileService;
    }

    @Override
    public DataList<List<FoodDto>> getAll(BaseCriteria criteria) {
        Pageable pageable = PageRequest.of(criteria.getPage(), criteria.getSize());
        Page<Food> foods = repository.findAllFoods(pageable, criteria.getSearch());
        List<FoodDto> dto = mapper.toDto(foods.getContent());
        return DataList.<List<FoodDto>>builder()
                .data(dto)
                .totalPages(foods.getTotalPages())
                .allElements(foods.getTotalElements())
                .build();
    }

    @Override
    public FoodDto get(String id) {
        Food food = validator.existAndGet(id);
        return mapper.toDto(food);
    }

    @Override
    public FoodDto create(FoodCreateDto dto) {
        Food food = mapper.fromDto(dto);
        if (dto.getImage() != null && !dto.getImage().isEmpty()) {
            FileEntity image = profileService.saveFileDb(dto.getImage());
            food.setImage("/profile/download/" + image.getId());
        }
        long code = repository.countAllFoods() + 1;
        food.setCode("№" + code);
        return mapper.toDto(repository.save(food));
    }

    @Override
    public FoodDto update(String id, FoodUpdateDto dto) {
        Food food = validator.existAndGet(id);
        mapper.fromDto(dto, food);
        if (dto.getImage() != null && !dto.getImage().isEmpty()) {
            FileEntity image = profileService.saveFileDb(dto.getImage());
            food.setImage("/profile/download/" + image.getId());
        }
        return mapper.toDto(repository.save(food));
    }

    @Override
    public void delete(String id) {
        Food food = validator.existAndGet(id);
        food.setDeleted(true);
        repository.save(food);
    }

    public void active(String id) {
        Food food = validator.existAndGet(id);
        food.setActive(!food.isActive());
        repository.save(food);
    }
}
