package oshxona.oshxona;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;
import oshxona.oshxona.model.User;
import oshxona.oshxona.repository.UserRepository;

@Slf4j
@SpringBootApplication
@EnableScheduling
public class OshxonaApplication {

    public static void main(String[] args) {
        SpringApplication.run(OshxonaApplication.class, args);
    }

    @Bean
    public CommandLineRunner init(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String adminPhone = "+998901112233";
            if (!userRepository.existsByPhone(adminPhone)) {
                User admin = new User();
                admin.setPhone(adminPhone);
                admin.setFullName("Super Admin");
                admin.setPassword(passwordEncoder.encode("admin"));
                userRepository.save(admin);
            } else {
               log.info("--> Admin allaqachon bazada bor <--");
            }
        };
    }
}