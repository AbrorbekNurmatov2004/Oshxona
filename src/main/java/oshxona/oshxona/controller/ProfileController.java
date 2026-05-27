package oshxona.oshxona.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import oshxona.oshxona.config.CustomUserDetails;
import oshxona.oshxona.dto.user.ChangePasswordDto;
import oshxona.oshxona.exception.BadRequestException;
import oshxona.oshxona.model.User;
import oshxona.oshxona.service.ProfileService;
import oshxona.oshxona.utils.SecurityUtils;
import oshxona.oshxona.validator.UserValidator;

@Controller
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService service;
    private final UserValidator validator;

    @GetMapping
    public String profilePage(Model model) {
        CustomUserDetails currentUser = SecurityUtils.sessionUser();
        User user = validator.existAndGet(currentUser.getId());
        model.addAttribute("user", user);
        return "profile";
    }

    @GetMapping("/upload")
    public String uploadForm() {
        return "upload-file";
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file,Model model) {
        try {
            service.upload(file);
            return "redirect:/profile";
        } catch (BadRequestException ex) {
            CustomUserDetails currentUser = SecurityUtils.sessionUser();
            User user = validator.existAndGet(currentUser.getId());
            model.addAttribute("user", user);
            model.addAttribute("globalError", ex.getMessage());
            return "profile";
        }
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<FileSystemResource> download(@PathVariable String id) {
        return service.download(id);
    }

    @GetMapping("/password")
    public String changePasswordForm(Model model) {
        model.addAttribute("changePasswordDto", new ChangePasswordDto());
        return "change-password";
    }

    @PostMapping("/password")
    public String changePassword(@Valid @ModelAttribute("changePasswordDto") ChangePasswordDto dto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("changePasswordDto", dto);
            return "change-password";
        }
        service.changePassword(dto);
        return "redirect:/profile?success=true";
    }
}