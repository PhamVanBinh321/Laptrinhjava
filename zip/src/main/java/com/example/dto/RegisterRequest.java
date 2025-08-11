package com.example.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank @Size(min = 4, max = 100)
    private String fullName;

    @NotBlank @Email
    private String email;

    @NotBlank @Size(min = 4, max = 32)
    private String username;

    @NotBlank @Size(min = 6, max = 100)
    private String password;

    @NotBlank @Size(min = 6, max = 100)
    private String rePassword;
}
