package com.pavobarisic.sportsbook.service;

import com.pavobarisic.sportsbook.dto.AuthResponse;
import com.pavobarisic.sportsbook.dto.LoginRequest;
import com.pavobarisic.sportsbook.dto.RegisterRequest;
import com.pavobarisic.sportsbook.model.Korisnik;
import com.pavobarisic.sportsbook.model.Rola;
import com.pavobarisic.sportsbook.repository.KorisnikRepository;
import com.pavobarisic.sportsbook.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private KorisnikRepository korisnikRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private Korisnik kreirajKorisnika() {
        return Korisnik.builder()
                .id(1L)
                .ime("Pavo")
                .prezime("Barisic")
                .email("pavo@test.com")
                .lozinka("hashirana_lozinka")
                .rola(Rola.USER)
                .stanje(new BigDecimal("1000.00"))
                .datumRegistracije(LocalDateTime.now())
                .build();
    }

    @Test
    void register_emailZauzet_baciException() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("pavo@test.com");

        when(korisnikRepository.existsByEmail("pavo@test.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> authService.register(request));
    }

    @Test
    void register_noviEmail_spremiIVratiToken() {
        RegisterRequest request = new RegisterRequest();
        request.setIme("Pavo");
        request.setPrezime("Barisic");
        request.setEmail("pavo@test.com");
        request.setLozinka("lozinka123");

        when(korisnikRepository.existsByEmail("pavo@test.com")).thenReturn(false);
        when(passwordEncoder.encode("lozinka123")).thenReturn("hashirana_lozinka");
        when(korisnikRepository.save(any(Korisnik.class))).thenReturn(kreirajKorisnika());
        when(jwtService.generirajToken(anyString(), anyString())).thenReturn("jwt_token");

        AuthResponse rezultat = authService.register(request);

        assertEquals("jwt_token", rezultat.getToken());
        assertEquals("pavo@test.com", rezultat.getEmail());
        assertEquals("USER", rezultat.getRola());
    }

    @Test
    void login_korisnikNePostoji_baciException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("nepostoji@test.com");
        request.setLozinka("lozinka123");

        when(korisnikRepository.findByEmail("nepostoji@test.com"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> authService.login(request));
    }

    @Test
    void login_pogresnaLozinka_baciException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("pavo@test.com");
        request.setLozinka("pogresna_lozinka");

        when(korisnikRepository.findByEmail("pavo@test.com"))
                .thenReturn(Optional.of(kreirajKorisnika()));
        when(passwordEncoder.matches("pogresna_lozinka", "hashirana_lozinka"))
                .thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> authService.login(request));
    }

    @Test
    void login_ispravniPodaci_vratiToken() {
        LoginRequest request = new LoginRequest();
        request.setEmail("pavo@test.com");
        request.setLozinka("lozinka123");

        when(korisnikRepository.findByEmail("pavo@test.com"))
                .thenReturn(Optional.of(kreirajKorisnika()));
        when(passwordEncoder.matches("lozinka123", "hashirana_lozinka"))
                .thenReturn(true);
        when(jwtService.generirajToken(anyString(), anyString()))
                .thenReturn("jwt_token");

        AuthResponse rezultat = authService.login(request);

        assertEquals("jwt_token", rezultat.getToken());
        assertEquals("pavo@test.com", rezultat.getEmail());
    }
}