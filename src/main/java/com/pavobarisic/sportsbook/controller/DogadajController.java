package com.pavobarisic.sportsbook.controller;

import com.pavobarisic.sportsbook.model.Dogadaj;
import com.pavobarisic.sportsbook.model.DogadajStatus;
import com.pavobarisic.sportsbook.model.Sport;
import com.pavobarisic.sportsbook.model.TipOklade;
import com.pavobarisic.sportsbook.service.DogadajService;
import com.pavobarisic.sportsbook.service.TiketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dogadaji")
@RequiredArgsConstructor
public class DogadajController {

    private final DogadajService dogadajService;
    private final TiketService tiketService;

    @GetMapping
    public ResponseEntity<List<Dogadaj>> dajSve(
            @RequestParam(required = false) DogadajStatus status,
            @RequestParam(required = false) Sport sport) {

        if (status != null && sport != null) {
            return ResponseEntity.ok(dogadajService.dajByStatusAndSport(status, sport));
        } else if (status != null) {
            return ResponseEntity.ok(dogadajService.dajByStatus(status));
        } else if (sport != null) {
            return ResponseEntity.ok(dogadajService.dajBySport(sport));
        }
        return ResponseEntity.ok(dogadajService.dajSve());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dogadaj> dajById(@PathVariable Long id) {
        return ResponseEntity.ok(dogadajService.dajById(id));
    }

    @PostMapping
    public ResponseEntity<Dogadaj> dodaj(@Valid @RequestBody Dogadaj dogadaj) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dogadajService.spremi(dogadaj));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Dogadaj> azuriraj(
            @PathVariable Long id,
            @Valid @RequestBody Dogadaj dogadaj) {
        return ResponseEntity.ok(dogadajService.azuriraj(id, dogadaj));
    }

    @PutMapping("/{id}/rezultat")
    public ResponseEntity<Dogadaj> postaviRezultat(
            @PathVariable Long id,
            @RequestParam String rezultat,
            @RequestParam TipOklade pobjednickiTip) {
        return ResponseEntity.ok(tiketService.postaviRezultat(id, rezultat, pobjednickiTip));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> obrisi(@PathVariable Long id) {
        dogadajService.obrisi(id);
        return ResponseEntity.noContent().build();
    }
}