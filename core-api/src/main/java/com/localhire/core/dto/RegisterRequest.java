package com.localhire.core.dto;

import com.localhire.core.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    String email,

    @NotBlank(message = "Password is required")
    String password,

    @NotBlank(message = "Full name is required")
    String fullName,

    String phone,

    @NotNull(message = "Role is required")
    UserRole role
) {}