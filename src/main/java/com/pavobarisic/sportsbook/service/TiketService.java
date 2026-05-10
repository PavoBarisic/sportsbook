package com.pavobarisic.sportsbook.service;

import com.pavobarisic.sportsbook.dto.StavkaTiketaRequest;
import com.pavobarisic.sportsbook.dto.TiketRequest;
import com.pavobarisic.sportsbook.exception.NedovoljnoSredstavaException;
import com.pavobarisic.sportsbook.exception.ResourceNotFoundException;
import com.pavobarisic.sportsbook.model.*;
import com.pavobarisic.sportsbook.repository.DogadajRepository;
import com.pavobarisic.sportsbook.repository.KorisnikRepository;
import com.pavobarisic.sportsbook.repository.TiketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TiketService {

    private final TiketRepository tiketRepository;
    private final KorisnikRepository korisnikRepository;
    private final DogadajRepository dogadajRepository;

    public List<Tiket> dajMojeTikete(String email) {
        Korisnik korisnik = korisnikRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Korisnik s emailom " + email + " ne postoji!"));
        return tiketRepository.findByKorisnikId(korisnik.getId());
    }

    public List<Tiket> dajSveTikete() {
        return tiketRepository.findAll();
    }

    public Tiket dajById(Long id) {
        return tiketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tiket s id-em " + id + " ne postoji!"));
    }

    @Transactional
    public Tiket postaviTiket(TiketRequest request, String email) {
        // 1. Dohvati korisnika
        Korisnik korisnik = korisnikRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Korisnik s emailom " + email + " ne postoji!"));

        // 2. Provjeri sredstva
        if (korisnik.getStanje().compareTo(request.getUlog()) < 0) {
            throw new NedovoljnoSredstavaException(
                    "Nemate dovoljno sredstava! Stanje: " + korisnik.getStanje() + " KM");
        }

        // 3. Kreiraj tiket
        Tiket tiket = Tiket.builder()
                .korisnik(korisnik)
                .ukupnaUloga(request.getUlog())
                .datumPostavljanja(LocalDateTime.now())
                .build();

        // 4. Obradi stavke i izračunaj potencijalnu dobit
        List<StavkaTiketa> stavke = new ArrayList<>();
        BigDecimal ukupnaKvota = BigDecimal.ONE;

        for (StavkaTiketaRequest stavkaRequest : request.getStavke()) {
            Dogadaj dogadaj = dogadajRepository.findById(stavkaRequest.getDogadajId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Dogadaj s id-em " + stavkaRequest.getDogadajId() + " ne postoji!"));

            if (dogadaj.getStatus() != DogadajStatus.UPCOMING) {
                throw new IllegalArgumentException(
                        "Dogadaj '" + dogadaj.getNaziv() + "' nije dostupan za klađenje!");
            }

            // Dohvati ispravnu kvotu ovisno o tipu oklade
            BigDecimal kvota = switch (stavkaRequest.getTip()) {
                case DOMACIN -> dogadaj.getKvotaDomacin();
                case NERIJESENO -> dogadaj.getKvotaNerijeseno();
                case GOST -> dogadaj.getKvotaGost();
            };

            ukupnaKvota = ukupnaKvota.multiply(kvota);

            StavkaTiketa stavka = StavkaTiketa.builder()
                    .tiket(tiket)
                    .dogadaj(dogadaj)
                    .odabranaKvota(kvota)
                    .tip(stavkaRequest.getTip())
                    .build();

            stavke.add(stavka);
        }

        // 5. Postavi potencijalnu dobit i stavke
        tiket.setPotencijalnaDobitak(request.getUlog().multiply(ukupnaKvota));
        tiket.setStavke(stavke);

        // 6. Smanji stanje računa
        korisnik.setStanje(korisnik.getStanje().subtract(request.getUlog()));
        korisnikRepository.save(korisnik);

        return tiketRepository.save(tiket);
    }

    @Transactional
    public Dogadaj postaviRezultat(Long dogadajId, String rezultat, TipOklade pobjednickiTip) {
        // 1. Dohvati i ažuriraj događaj
        Dogadaj dogadaj = dogadajRepository.findById(dogadajId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Dogadaj s id-em " + dogadajId + " ne postoji!"));

        dogadaj.setRezultat(rezultat);
        dogadaj.setStatus(DogadajStatus.FINISHED);
        dogadaj.setPobjednickiTip(pobjednickiTip);
        dogadajRepository.save(dogadaj);

        // 2. Dohvati sve aktivne tikete
        List<Tiket> aktivniTiketi = tiketRepository.findByStatus(TiketStatus.AKTIVAN);

        // 3. Za svaki aktivni tiket koji sadrži ovaj događaj
        for (Tiket tiket : aktivniTiketi) {
            boolean imaStavkuZaDogadaj = tiket.getStavke().stream()
                    .anyMatch(s -> s.getDogadaj().getId().equals(dogadajId));

            if (!imaStavkuZaDogadaj) continue;

            // Provjeri je li stavka za ovaj događaj pogođena
            boolean stavkaPogodena = tiket.getStavke().stream()
                    .filter(s -> s.getDogadaj().getId().equals(dogadajId))
                    .allMatch(s -> s.getTip() == pobjednickiTip);

            if (!stavkaPogodena) {
                tiket.setStatus(TiketStatus.GUBITAN);
                tiketRepository.save(tiket);
                continue;
            }

            // Stavka pogođena - provjeri jesu li SVE stavke na tiketu završene i pogođene
            boolean sveZavrseneIPogodene = tiket.getStavke().stream()
                    .allMatch(s -> {
                        Dogadaj d = s.getDogadaj();
                        if (d.getStatus() != DogadajStatus.FINISHED) return false;
                        return s.getTip() == d.getPobjednickiTip();
                    });

            if (sveZavrseneIPogodene) {
                tiket.setStatus(TiketStatus.DOBITAN);
                Korisnik korisnik = tiket.getKorisnik();
                korisnik.setStanje(korisnik.getStanje().add(tiket.getPotencijalnaDobitak()));
                korisnikRepository.save(korisnik);
                tiketRepository.save(tiket);
            }
        }

        return dogadaj;
    }
}