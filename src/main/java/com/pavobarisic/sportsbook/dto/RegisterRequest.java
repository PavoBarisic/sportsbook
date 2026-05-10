package com.pavobarisic.sportsbook.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "Ime ne smije biti prazno!")
    private String ime;

    @NotBlank(message = "Prezime ne smije biti prazno!")
    private String prezime;

    @Email(message = "Email nije ispravan!")
    @NotBlank(message = "Email ne smije biti prazan!")
    private String email;

    @NotBlank(message = "Lozinka ne smije biti prazna!")
    private String lozinka;
}