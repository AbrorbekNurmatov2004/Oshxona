package oshxona.oshxona.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import oshxona.oshxona.dto.user.UserCreateDto;
import oshxona.oshxona.dto.user.UserUpdateDto;
import oshxona.oshxona.utils.ErrorConstants;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ResourceNotFoundException.class, PhoneNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundExceptions(RuntimeException e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());
        model.addAttribute("status", HttpStatus.NOT_FOUND.value());
        return "error/error-page";
    }

    @ExceptionHandler(AlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleAlreadyExistsException(AlreadyExistsException e, Model model, HttpServletRequest request) {
        model.addAttribute("errorMessage", e.getMessage());
        model.addAttribute("globalError", e.getMessage());
        model.addAttribute("status", HttpStatus.CONFLICT.value());
        String requestURI = request.getRequestURI();
        if (requestURI.contains("/update")) {
            String id = requestURI.substring(requestURI.lastIndexOf("/") + 1);
            model.addAttribute("userId", id);
            if (!model.containsAttribute("dto")) {
                model.addAttribute("dto", new UserUpdateDto());
            }
            return "user-update";
        }
        if (!model.containsAttribute("userCreateDto")) {
            model.addAttribute("userCreateDto", new UserCreateDto());
        }
        return "user-create";
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequestException(BadRequestException e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());
        model.addAttribute("status", HttpStatus.BAD_REQUEST.value());
        return "error/error-page";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGlobalException(Exception e, Model model) {
        model.addAttribute("errorMessage", ErrorConstants.SERVER_ERROR + ": " + e.getMessage());
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return "error/error-page";
    }

}
