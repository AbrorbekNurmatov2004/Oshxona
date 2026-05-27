package oshxona.oshxona.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import oshxona.oshxona.exception.PhoneNotFoundException;
import oshxona.oshxona.model.User;
import oshxona.oshxona.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String phone) {
        User user = repository.findByPhoneAndDeletedFalse(phone).orElseThrow(() -> new PhoneNotFoundException(phone));
        return CustomUserDetails.builder()
                .id(user.getId())
                .phone(user.getPhone())
                .password(user.getPassword())
                .role(user.getRole())
                .superAdmin(user.getSuperAdmin())
                .build();
    }
}
