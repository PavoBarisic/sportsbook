package com.pavobarisic.sportsbook.controller;

import com.pavobarisic.sportsbook.model.Korisnik;
import com.pavobarisic.sportsbook.service.KorisnikService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/korisnici")
@RequiredArgsConstructor
public class KorisnikController {

    private final KorisnikService korisnikService;

    @GetMapping("/me")
    public ResponseEntity<Korisnik> dajMojProfil() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(korisnikService.dajByEmail(email));
    }

    @GetMapping
    public ResponseEntity<List<Korisnik>> dajSve() {
        return ResponseEntity.ok(korisnikService.dajSve());
    }
}