package com.pavobarisic.sportsbook.service;

import com.pavobarisic.sportsbook.dto.StavkaTiketaRequest;
import com.pavobarisic.sportsbook.dto.TiketRequest;
import com.pavobarisic.sportsbook.exception.NedovoljnoSredstavaException;
import com.pavobarisic.sportsbook.exception.ResourceNotFoundException;
import com.pavobarisic.sportsbook.model.*;
import com.pavobarisic.sportsbook.repository.DogadajRepository;
import com.pavobarisic.sportsbook.repository.KorisnikRepository;
import com.pavobarisic.sportsbook.repository.TiketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TiketServiceTest {

    @Mock
    private TiketRepository tiketRepository;

    @Mock
    private KorisnikRepository korisnikRepository;

    @Mock
    private DogadajRepository dogadajRepository;

    @InjectMocks
    private TiketService tiketService;

    private Korisnik kreirajKorisnika(BigDecimal stanje) {
        return Korisnik.builder()
                .id(1L)
                .ime("Pavo")
                .prezime("Barisic")
                .email("pavo@test.com")
                .lozinka("hashirana_lozinka")
                .rola(Rola.USER)
                .stanje(stanje)
                .datumRegistracije(LocalDateTime.now())
                .build();
    }

    private Dogadaj kreirajDogadaj(Long id) {
        return Dogadaj.builder()
                .id(id)
                .naziv("Barcelona vs Real Madrid")
                .sport(Sport.FOOTBALL)
                .datum(LocalDateTime.now().plusDays(1))
                .status(DogadajStatus.UPCOMING)
                .kvotaDomacin(new BigDecimal("1.85"))
                .kvotaNerijeseno(new BigDecimal("3.20"))
                .kvotaGost(new BigDecimal("2.10"))
                .build();
    }

    private TiketRequest kreirajTiketRequest(BigDecimal ulog, Long dogadajId, TipOklade tip) {
        StavkaTiketaRequest stavka = new StavkaTiketaRequest();
        stavka.setDogadajId(dogadajId);
        stavka.setTip(tip);

        TiketRequest request = new TiketRequest();
        request.setUlog(ulog);
        request.setStavke(List.of(stavka));
        return request;
    }

    @Test
    void postaviTiket_korisnikNePostoji_baciException() {
        TiketRequest request = kreirajTiketRequest(
                new BigDecimal("10.00"), 1L, TipOklade.DOMACIN);

        when(korisnikRepository.findByEmail("pavo@test.com"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> tiketService.postaviTiket(request, "pavo@test.com"));
    }

    @Test
    void postaviTiket_nedovoljnoSredstava_baciException() {
        TiketRequest request = kreirajTiketRequest(
                new BigDecimal("500.00"), 1L, TipOklade.DOMACIN);
        Korisnik korisnik = kreirajKorisnika(new BigDecimal("100.00"));

        when(korisnikRepository.findByEmail("pavo@test.com"))
                .thenReturn(Optional.of(korisnik));

        assertThrows(NedovoljnoSredstavaException.class,
                () -> tiketService.postaviTiket(request, "pavo@test.com"));
    }

    @Test
    void postaviTiket_dogadajNijeUpcoming_baciException() {
        TiketRequest request = kreirajTiketRequest(
                new BigDecimal("10.00"), 1L, TipOklade.DOMACIN);
        Korisnik korisnik = kreirajKorisnika(new BigDecimal("1000.00"));
        Dogadaj dogadaj = kreirajDogadaj(1L);
        dogadaj.setStatus(DogadajStatus.FINISHED);

        when(korisnikRepository.findByEmail("pavo@test.com"))
                .thenReturn(Optional.of(korisnik));
        when(dogadajRepository.findById(1L))
                .thenReturn(Optional.of(dogadaj));

        assertThrows(IllegalArgumentException.class,
                () -> tiketService.postaviTiket(request, "pavo@test.com"));
    }

    @Test
    void postaviTiket_sveIspravno_smanjiStanjeIzracunajDobit() {
        TiketRequest request = kreirajTiketRequest(
                new BigDecimal("10.00"), 1L, TipOklade.DOMACIN);
        Korisnik korisnik = kreirajKorisnika(new BigDecimal("1000.00"));
        Dogadaj dogadaj = kreirajDogadaj(1L);

        Tiket ocekivaniTiket = Tiket.builder()
                .id(1L)
                .korisnik(korisnik)
                .ukupnaUloga(new BigDecimal("10.00"))
                .potencijalnaDobitak(new BigDecimal("18.50"))
                .status(TiketStatus.AKTIVAN)
                .datumPostavljanja(LocalDateTime.now())
                .build();

        when(korisnikRepository.findByEmail("pavo@test.com"))
                .thenReturn(Optional.of(korisnik));
        when(dogadajRepository.findById(1L))
                .thenReturn(Optional.of(dogadaj));
        when(korisnikRepository.save(any(Korisnik.class)))
                .thenReturn(korisnik);
        when(tiketRepository.save(any(Tiket.class)))
                .thenReturn(ocekivaniTiket);

        Tiket rezultat = tiketService.postaviTiket(request, "pavo@test.com");

        assertEquals(TiketStatus.AKTIVAN, rezultat.getStatus());
        assertEquals(new BigDecimal("990.00"), korisnik.getStanje());
    }

    @Test
    void dajById_tiketPostoji_vratiTiket() {
        Tiket tiket = Tiket.builder()
                .id(1L)
                .ukupnaUloga(new BigDecimal("10.00"))
                .status(TiketStatus.AKTIVAN)
                .build();

        when(tiketRepository.findById(1L)).thenReturn(Optional.of(tiket));

        Tiket rezultat = tiketService.dajById(1L);

        assertEquals(1L, rezultat.getId());
        assertEquals(TiketStatus.AKTIVAN, rezultat.getStatus());
    }

    @Test
    void dajById_tiketNePostoji_baciException() {
        when(tiketRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> tiketService.dajById(99L));
    }
}