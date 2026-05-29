package oshxona.oshxona;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;
import oshxona.oshxona.model.User;
import oshxona.oshxona.repository.UserRepository;

@SpringBootApplication
@EnableScheduling
public class OshxonaApplication {

    public static void main(String[] args) {
        SpringApplication.run(OshxonaApplication.class, args);
    }

    @Bean
    public CommandLineRunner init(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
                User authUser = new User();
                authUser.setSuperAdmin(true);
                authUser.setFullName("Super Admin");
                authUser.setPhone("1");
                authUser.setPassword(passwordEncoder.encode("1"));
                userRepository.save(authUser);
        };
    }
}