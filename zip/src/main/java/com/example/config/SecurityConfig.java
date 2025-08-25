// src/main/java/com/example/config/SecurityConfig.java
package com.example.config;

import com.example.service.AppUserDetailsService;
import com.example.config.RoleBasedAuthSuccessHandler;
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
    private final RoleBasedAuthSuccessHandler successHandler;

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
            .authorizeHttpRequests(reg -> reg
                .requestMatchers("/", "/home", "/login", "/register",
                                 "/css/**", "/js/**", "/images/**", "/assets/**", "/uploads/**").permitAll()
                .requestMatchers("/booking", "/booking/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(f -> f
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler(successHandler)      // 🔴 dùng success handler theo role
                // .defaultSuccessUrl("/", false)    // (tuỳ chọn) fallback nếu không dùng successHandler
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
