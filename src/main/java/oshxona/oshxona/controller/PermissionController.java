package oshxona.oshxona.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import oshxona.oshxona.criteria.BaseCriteria;
import oshxona.oshxona.criteria.DataList;
import oshxona.oshxona.dto.permission.PermissionCreateDto;
import oshxona.oshxona.dto.permission.PermissionDto;
import oshxona.oshxona.dto.permission.PermissionUpdateDto;
import oshxona.oshxona.service.PermissionService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/permissions")
public class PermissionController {

    private final PermissionService service;

    @PreAuthorize("@securityUtils.superAdmin() or hasAuthority('permission:read')")
    @GetMapping
    public String getAll(@RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "10") int size,
                         @RequestParam(defaultValue = "", required = false) String search, Model model) {
        BaseCriteria criteria = BaseCriteria.builder().search(search).page(page).size(size).build();
        DataList<List<PermissionDto>> permissionList = service.getAll(criteria);

        model.addAttribute("permissions", permissionList.getData());
        model.addAttribute("totalPages", permissionList.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("search", search);
        return "permission-list";
    }

    @PreAuthorize("@securityUtils.superAdmin() or hasAuthority('permission:create')")
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("dto", new PermissionCreateDto());
        return "permission-create";
    }

    @PreAuthorize("@securityUtils.superAdmin() or hasAuthority('permission:create')")
    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("dto") PermissionCreateDto dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "permission-create";
        service.create(dto);
        return "redirect:/permissions";
    }

    @PreAuthorize("@securityUtils.superAdmin() or hasAuthority('permission:update')")
    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable String id, Model model) {
        PermissionDto dto = service.get(id);
        model.addAttribute("permissionDto", dto);
        return "permission-update";
    }

    @PreAuthorize("@securityUtils.superAdmin() or hasAuthority('permission:update')")
    @PostMapping("/update/{id}")
    public String update(@PathVariable String id,
                         @Valid @ModelAttribute("permissionDto") PermissionUpdateDto dto,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "permission-update";
        }
        service.update(id, dto);
        return "redirect:/permissions";
    }

    @PreAuthorize("@securityUtils.superAdmin() or hasAuthority('permission:delete')")
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable String id, RedirectAttributes redirectAttributes) {
        service.delete(id);
        redirectAttributes.addFlashAttribute("success", "Huquq muvaffaqiyatli o'chirildi");
        return "redirect:/permissions";
    }
}