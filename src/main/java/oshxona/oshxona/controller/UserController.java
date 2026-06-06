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
import oshxona.oshxona.dto.user.UserCreateDto;
import oshxona.oshxona.dto.user.UserDto;
import oshxona.oshxona.dto.user.UserUpdateDto;
import oshxona.oshxona.repository.RoleRepository;
import oshxona.oshxona.service.UserService;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService service;
    private final RoleRepository roleRepository;

    @PreAuthorize(value = "@securityUtils.superAdmin() or hasAuthority('user:read')")
    @GetMapping
    public String getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "", required = false) String search,
            Model model
    ) {
        BaseCriteria criteria = BaseCriteria.builder()
                .search(search)
                .page(page)
                .size(size)
                .build();
        DataList<List<UserDto>> usersList = service.getAll(criteria);

        model.addAttribute("users", usersList.getData());
        model.addAttribute("totalPages", usersList.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("search", search);
        return "user-list";
    }

    @PreAuthorize(value = "@securityUtils.superAdmin() or hasAuthority('user:create')")
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("userCreateDto", new UserCreateDto());
        model.addAttribute("roles", roleRepository.findAll());
        return "user-create";
    }

    @PreAuthorize(value = "@securityUtils.superAdmin() or hasAuthority('user:create')")
    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("userCreateDto") UserCreateDto dto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", roleRepository.findAll());
            return "user-create";
        }
        service.create(dto);
        return "redirect:/users";
    }

    @PreAuthorize(value = "@securityUtils.superAdmin() or hasAuthority('user:update')")
    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable String id, Model model) {
        UserDto userDto = service.get(id);
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setFullName(userDto.getFullName());
        updateDto.setPhone(userDto.getPhone());
        if (userDto.getRole() != null) {
            updateDto.setRoleId(userDto.getRole().getId());
        }
        model.addAttribute("dto", updateDto);
        model.addAttribute("userId", id);
        model.addAttribute("roles", roleRepository.findAll());
        return "user-update";
    }

    @PreAuthorize(value = "@securityUtils.superAdmin() or hasAuthority('user:update')")
    @PostMapping("/update/{id}")
    public String update(@PathVariable String id, @Valid @ModelAttribute("dto") UserUpdateDto dto,
                         BindingResult bindingResult, Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("userId", id);
            model.addAttribute("roles", roleRepository.findAll());
            return "user-update";
        }
        service.update(id, dto);
        return "redirect:/users";
    }

    @PreAuthorize(value = "@securityUtils.superAdmin() or hasAuthority('user:delete')")
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable String id) {
        service.delete(id);
        return "redirect:/users";
    }
}