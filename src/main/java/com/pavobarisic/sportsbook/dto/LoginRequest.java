package com.pavobarisic.sportsbook.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @Email(message = "Email nije ispravan!")
    @NotBlank(message = "Email ne smije biti prazan!")
    private String email;

    @NotBlank(message = "Lozinka ne smije biti prazna!")
    private String lozinka;
}