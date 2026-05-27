package oshxona.oshxona.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import oshxona.oshxona.utils.SecurityUtils;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomUserDetailsService userDetailsService) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers(SecurityUtils.WHITE_LIST).permitAll()
                        .anyRequest().authenticated()
        );

        http.formLogin(
                configurer -> {
                    configurer.loginPage("/login");
                    configurer.usernameParameter("phone");
                    configurer.passwordParameter("password");
                    configurer.defaultSuccessUrl("/foods",true);
                }
        );

        http.logout(logoutConfigurer -> {
                    logoutConfigurer.logoutUrl("/logout");
                    logoutConfigurer.logoutSuccessUrl("/login");
            }
        );

        http.exceptionHandling(exception -> exception
                .accessDeniedPage("/403")
        );

        http.userDetailsService(userDetailsService);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
