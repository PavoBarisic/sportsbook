package com.pavobarisic.sportsbook.service;

import com.pavobarisic.sportsbook.exception.ResourceNotFoundException;
import com.pavobarisic.sportsbook.model.Korisnik;
import com.pavobarisic.sportsbook.repository.KorisnikRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KorisnikServiceTest {

    @Mock
    private KorisnikRepository korisnikRepository;

    @InjectMocks
    private KorisnikService korisnikService;

    @Test
    void dajSve_vratiListuKorisnika() {
        Korisnik k1 = new Korisnik();
        k1.setEmail("ana@test.com");
        Korisnik k2 = new Korisnik();
        k2.setEmail("ivan@test.com");

        when(korisnikRepository.findAll()).thenReturn(List.of(k1, k2));

        List<Korisnik> rezultat = korisnikService.dajSve();

        assertEquals(2, rezultat.size());
        assertEquals("ana@test.com", rezultat.get(0).getEmail());
    }

    @Test
    void dajByEmail_korisnikPostoji_vratiKorisnika() {
        Korisnik korisnik = new Korisnik();
        korisnik.setEmail("ana@test.com");
        korisnik.setIme("Ana");

        when(korisnikRepository.findByEmail("ana@test.com"))
                .thenReturn(Optional.of(korisnik));

        Korisnik rezultat = korisnikService.dajByEmail("ana@test.com");

        assertEquals("ana@test.com", rezultat.getEmail());
        assertEquals("Ana", rezultat.getIme());
    }

    @Test
    void dajByEmail_korisnikNePostoji_baciException() {
        when(korisnikRepository.findByEmail("nepostoji@test.com"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> korisnikService.dajByEmail("nepostoji@test.com"));
    }
}