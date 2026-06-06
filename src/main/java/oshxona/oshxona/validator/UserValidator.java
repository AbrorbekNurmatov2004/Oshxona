package oshxona.oshxona.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import oshxona.oshxona.config.CustomUserDetails;
import oshxona.oshxona.dto.user.ChangePasswordDto;
import oshxona.oshxona.exception.AlreadyExistsException;
import oshxona.oshxona.exception.ResourceNotFoundException;
import oshxona.oshxona.model.User;
import oshxona.oshxona.repository.UserRepository;
import oshxona.oshxona.utils.ErrorConstants;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public User existAndGet(String id) {
        return repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(ErrorConstants.s_NOT_FOUND.formatted("user"))
        );
    }

    public void validateUniquePhone(String phone) {
        if (repository.existsByPhoneAndDeletedFalse(phone)) {
            throw new AlreadyExistsException("Ushbu telefon raqami bilan foydalanuvchi allaqachon ro'yxatdan o'tgan!");
        }
    }

    public void validateUniquePhoneForUpdate(String phone, String id) {
        if (repository.existsByPhoneAndIdNotAndDeletedFalse(phone, id)) {
            throw new AlreadyExistsException("Ushbu telefon raqami boshqa foydalanuvchiga biriktirilgan!");
        }
    }

    public void validateChangePassword(ChangePasswordDto dto, CustomUserDetails currentUser) {
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new BadCredentialsException(ErrorConstants.PASSWORD_DO_NOT_MATCH);
        }
        if (!passwordEncoder.matches(dto.getOldPassword(), currentUser.getPassword())) {
            throw new BadCredentialsException(ErrorConstants.OLD_PASSWORD_DO_NOT_MATCH);
        }
        if (dto.getNewPassword().length() < 8) {
            throw new BadCredentialsException(ErrorConstants.PASSWORD_TOO_SHORT);
        }
    }
}
