package oshxona.oshxona.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import oshxona.oshxona.criteria.BaseCriteria;
import oshxona.oshxona.criteria.DataList;
import oshxona.oshxona.dto.food.FoodCreateDto;
import oshxona.oshxona.dto.food.FoodDto;
import oshxona.oshxona.dto.food.FoodUpdateDto;
import oshxona.oshxona.service.FoodService;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/foods")
public class FoodController {

    private final FoodService service;

    @PreAuthorize(value = "@securityUtils.superAdmin() or hasAuthority('food:read')")
    @GetMapping
    public String getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "", required = false) String search,
            Model model
    ) {
        BaseCriteria criteria = BaseCriteria.builder()
                .search(search)
                .page(page)
                .size(size)
                .build();
        DataList<List<FoodDto>> foodList = service.getAll(criteria);

        model.addAttribute("foods", foodList.getData());
        model.addAttribute("totalPages", foodList.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("search", search);
        return "food-list";
    }

    @PreAuthorize(value = "@securityUtils.superAdmin() or hasAuthority('food:create')")
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("dto", new FoodCreateDto());
        return "food-create";
    }

    @PreAuthorize(value = "@securityUtils.superAdmin() or hasAuthority('food:create')")
    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("dto") FoodCreateDto dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "food-create";
        }
        service.create(dto);
        return "redirect:/foods";
    }

    @PreAuthorize(value = "@securityUtils.superAdmin() or hasAuthority('food:update')")
    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable String id, Model model) {
        FoodDto dto = service.get(id);
        model.addAttribute("dto", dto);
        model.addAttribute("foodId", id);
        return "food-update";
    }

    @PreAuthorize(value = "@securityUtils.superAdmin() or hasAuthority('food:update')")
    @PostMapping("/update/{id}")
    public String update(@PathVariable String id, @Valid @ModelAttribute("dto") FoodUpdateDto dto,
            BindingResult bindingResult, Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("foodId", id);
            return "food-update";
        }
        service.update(id, dto);
        return "redirect:/foods";
    }

    @PreAuthorize(value = "@securityUtils.superAdmin() or hasAuthority('food:delete')")
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable String id) {
        service.delete(id);
        return "redirect:/foods";
    }

    @GetMapping("/active/{id}")
    public String active(@PathVariable String id) {
        service.active(id);
        return "redirect:/foods";
    }
}