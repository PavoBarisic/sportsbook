package com.pavobarisic.sportsbook.service;

import com.pavobarisic.sportsbook.exception.ResourceNotFoundException;
import com.pavobarisic.sportsbook.model.Dogadaj;
import com.pavobarisic.sportsbook.model.DogadajStatus;
import com.pavobarisic.sportsbook.model.Sport;
import com.pavobarisic.sportsbook.repository.DogadajRepository;
import com.pavobarisic.sportsbook.repository.StavkaTiketaRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DogadajServiceTest {

    @Mock
    private DogadajRepository dogadajRepository;

    @Mock
    private StavkaTiketaRepository stavkaTiketaRepository;

    @InjectMocks
    private DogadajService dogadajService;

    private Dogadaj kreirajDogadaj(Long id, String naziv) {
        return Dogadaj.builder()
                .id(id)
                .naziv(naziv)
                .sport(Sport.FOOTBALL)
                .datum(LocalDateTime.now().plusDays(1))
                .status(DogadajStatus.UPCOMING)
                .kvotaDomacin(new BigDecimal("1.85"))
                .kvotaNerijeseno(new BigDecimal("3.20"))
                .kvotaGost(new BigDecimal("2.10"))
                .build();
    }

    @Test
    void dajSve_vratiListuDogadaja() {
        Dogadaj d1 = kreirajDogadaj(1L, "Barcelona vs Real Madrid");
        Dogadaj d2 = kreirajDogadaj(2L, "Lakers vs Bulls");

        when(dogadajRepository.findAll()).thenReturn(List.of(d1, d2));

        List<Dogadaj> rezultat = dogadajService.dajSve();

        assertEquals(2, rezultat.size());
        assertEquals("Barcelona vs Real Madrid", rezultat.get(0).getNaziv());
    }

    @Test
    void dajById_dogadajPostoji_vratiDogadaj() {
        Dogadaj dogadaj = kreirajDogadaj(1L, "Barcelona vs Real Madrid");

        when(dogadajRepository.findById(1L)).thenReturn(Optional.of(dogadaj));

        Dogadaj rezultat = dogadajService.dajById(1L);

        assertEquals(1L, rezultat.getId());
        assertEquals("Barcelona vs Real Madrid", rezultat.getNaziv());
    }

    @Test
    void dajById_dogadajNePostoji_baciException() {
        when(dogadajRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> dogadajService.dajById(99L));
    }

    @Test
    void azuriraj_dogadajPostoji_azurirajPolja() {
        Dogadaj postojeci = kreirajDogadaj(1L, "Barcelona vs Real Madrid");
        Dogadaj noviPodaci = kreirajDogadaj(1L, "Bayern vs Dortmund");
        noviPodaci.setSport(Sport.BASKETBALL);
        noviPodaci.setStatus(DogadajStatus.LIVE);

        when(dogadajRepository.findById(1L)).thenReturn(Optional.of(postojeci));
        when(dogadajRepository.save(any(Dogadaj.class))).thenReturn(postojeci);

        Dogadaj rezultat = dogadajService.azuriraj(1L, noviPodaci);

        assertEquals("Bayern vs Dortmund", rezultat.getNaziv());
        assertEquals(Sport.BASKETBALL, rezultat.getSport());
        assertEquals(DogadajStatus.LIVE, rezultat.getStatus());
    }

    @Test
    void obrisi_dogadajPostoji_obrisiGa() {
        Dogadaj dogadaj = kreirajDogadaj(1L, "Barcelona vs Real Madrid");

        when(dogadajRepository.findById(1L)).thenReturn(Optional.of(dogadaj));
        doNothing().when(stavkaTiketaRepository).deleteByDogadajId(1L);
        doNothing().when(dogadajRepository).deleteById(1L);

        assertDoesNotThrow(() -> dogadajService.obrisi(1L));
        verify(stavkaTiketaRepository, times(1)).deleteByDogadajId(1L);
        verify(dogadajRepository, times(1)).deleteById(1L);
    }

    @Test
    void obrisi_dogadajNePostoji_baciException() {
        when(dogadajRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> dogadajService.obrisi(99L));
        verify(dogadajRepository, never()).deleteById(any());
    }
}