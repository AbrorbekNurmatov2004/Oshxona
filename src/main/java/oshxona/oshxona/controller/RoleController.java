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
import oshxona.oshxona.dto.role.RoleCreateDto;
import oshxona.oshxona.dto.role.RoleDto;
import oshxona.oshxona.dto.role.RoleUpdateDto;
import oshxona.oshxona.repository.PermissionRepository;
import oshxona.oshxona.service.RoleService;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/roles")
public class RoleController {

    private final RoleService service;
    private final PermissionRepository permissionRepository;

    @PreAuthorize(value = "@securityUtils.superAdmin() or hasAuthority('role:read')")
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
        DataList<List<RoleDto>> roleList = service.getAll(criteria);

        model.addAttribute("roles", roleList.getData());
        model.addAttribute("totalPages", roleList.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("search", search);
        return "role-list";
    }

    @PreAuthorize(value = "@securityUtils.superAdmin() or hasAuthority('role:create')")
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("dto", new RoleCreateDto());
        model.addAttribute("allPermissions", permissionRepository.findAll());
        return "role-create";
    }

    @PreAuthorize(value = "@securityUtils.superAdmin() or hasAuthority('role:create')")
    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("dto") RoleCreateDto dto, BindingResult bindingResult,Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("allPermissions", permissionRepository.findAll());
            return "role-create";
        }
        service.create(dto);
        return "redirect:/roles";
    }

    @PreAuthorize(value = "@securityUtils.superAdmin() or hasAuthority('role:update')")
    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable String id, Model model) {
        RoleUpdateDto dto = service.getForUpdate(id);
        model.addAttribute("dto", dto);
        model.addAttribute("roleId", id);
        model.addAttribute("allPermissions", permissionRepository.findAll());
        return "role-update";
    }

    @PreAuthorize(value = "@securityUtils.superAdmin() or hasAuthority('role:update')")
    @PostMapping("/update/{id}")
    public String update(@PathVariable String id, @Valid @ModelAttribute("dto") RoleUpdateDto dto,
                         BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("roleId", id);
            model.addAttribute("allPermissions", permissionRepository.findAll());
            return "role-update";
        }
        service.update(id, dto);
        return "redirect:/roles";
    }

    @PreAuthorize(value = "@securityUtils.superAdmin() or hasAuthority('role:delete')")
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable String id) {
        service.delete(id);
        return "redirect:/roles";
    }
}