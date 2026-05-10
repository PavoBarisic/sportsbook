package com.pavobarisic.sportsbook.service;

import com.pavobarisic.sportsbook.exception.ResourceNotFoundException;
import com.pavobarisic.sportsbook.model.Korisnik;
import com.pavobarisic.sportsbook.repository.KorisnikRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KorisnikService {

    private final KorisnikRepository korisnikRepository;

    public List<Korisnik> dajSve() {
        return korisnikRepository.findAll();
    }

    public Korisnik dajByEmail(String email) {
        return korisnikRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Korisnik s emailom " + email + " ne postoji!"));
    }
}