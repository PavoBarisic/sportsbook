package com.pavobarisic.sportsbook.controller;

import com.pavobarisic.sportsbook.dto.TiketRequest;
import com.pavobarisic.sportsbook.model.Tiket;
import com.pavobarisic.sportsbook.service.TiketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tiketi")
@RequiredArgsConstructor
public class TiketController {

    private final TiketService tiketService;

    @GetMapping
    public ResponseEntity<List<Tiket>> dajMojeTikete() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(tiketService.dajMojeTikete(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tiket> dajById(@PathVariable Long id) {
        return ResponseEntity.ok(tiketService.dajById(id));
    }

    @GetMapping("/sve")
    public ResponseEntity<List<Tiket>> dajSveTikete() {
        return ResponseEntity.ok(tiketService.dajSveTikete());
    }

    @PostMapping
    public ResponseEntity<Tiket> postaviTiket(@Valid @RequestBody TiketRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tiketService.postaviTiket(request, email));
    }
}