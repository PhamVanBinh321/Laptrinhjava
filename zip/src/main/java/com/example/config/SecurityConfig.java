package com.example.config;

import com.example.service.AppUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final AppUserDetailsService uds;

    @Bean
    BCryptPasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    DaoAuthenticationProvider daoAuthenticationProvider() {
        var p = new DaoAuthenticationProvider();
        p.setUserDetailsService(uds);
        p.setPasswordEncoder(passwordEncoder());
        return p;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF bật mặc định
            .authorizeHttpRequests(reg -> reg
                .requestMatchers("/", "/home", "/login", "/register",
                                 "/css/**", "/js/**", "/images/**", "/assets/**").permitAll()
                .requestMatchers("/booking", "/booking/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(f -> f
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("username")   // cho phép nhập username hoặc email
                .passwordParameter("password")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(l -> l
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .clearAuthentication(true)
            );

        http.authenticationProvider(daoAuthenticationProvider());
        return http.build();
    }
}
