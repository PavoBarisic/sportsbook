package com.pavobarisic.sportsbook.service;

import com.pavobarisic.sportsbook.dto.AuthResponse;
import com.pavobarisic.sportsbook.dto.LoginRequest;
import com.pavobarisic.sportsbook.dto.RegisterRequest;
import com.pavobarisic.sportsbook.model.Korisnik;
import com.pavobarisic.sportsbook.repository.KorisnikRepository;
import com.pavobarisic.sportsbook.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KorisnikRepository korisnikRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse register(RegisterRequest request) {
        if (korisnikRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email je već zauzet!");
        }

        Korisnik korisnik = Korisnik.builder()
                .ime(request.getIme())
                .prezime(request.getPrezime())
                .email(request.getEmail())
                .lozinka(passwordEncoder.encode(request.getLozinka()))
                .stanje(new BigDecimal("1000.00"))
                .datumRegistracije(LocalDateTime.now())
                .build();

        korisnikRepository.save(korisnik);

        String token = jwtService.generirajToken(korisnik.getEmail(), korisnik.getRola().name());

        return new AuthResponse(token, korisnik.getEmail(), korisnik.getRola().name());
    }

    public AuthResponse login(LoginRequest request) {
        Korisnik korisnik = korisnikRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Korisnik s emailom " + request.getEmail() + " ne postoji!"));

        if (!passwordEncoder.matches(request.getLozinka(), korisnik.getLozinka())) {
            throw new IllegalArgumentException("Pogrešna lozinka!");
        }

        String token = jwtService.generirajToken(korisnik.getEmail(), korisnik.getRola().name());

        return new AuthResponse(token, korisnik.getEmail(), korisnik.getRola().name());
    }
}