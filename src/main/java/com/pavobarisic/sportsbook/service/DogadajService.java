package com.pavobarisic.sportsbook.service;

import com.pavobarisic.sportsbook.exception.ResourceNotFoundException;
import com.pavobarisic.sportsbook.model.Dogadaj;
import com.pavobarisic.sportsbook.model.DogadajStatus;
import com.pavobarisic.sportsbook.model.Sport;
import com.pavobarisic.sportsbook.repository.DogadajRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DogadajService {

    private final DogadajRepository dogadajRepository;

    public List<Dogadaj> dajSve() {
        return dogadajRepository.findAll();
    }

    public List<Dogadaj> dajByStatus(DogadajStatus status) {
        return dogadajRepository.findByStatus(status);
    }

    public List<Dogadaj> dajBySport(Sport sport) {
        return dogadajRepository.findBySport(sport);
    }

    public List<Dogadaj> dajByStatusAndSport(DogadajStatus status, Sport sport) {
        return dogadajRepository.findByStatusAndSport(status, sport);
    }

    public Dogadaj dajById(Long id) {
        return dogadajRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Dogadaj s id-em " + id + " ne postoji!"));
    }

    public Dogadaj spremi(Dogadaj dogadaj) {
        return dogadajRepository.save(dogadaj);
    }

    public Dogadaj azuriraj(Long id, Dogadaj noviPodaci) {
        Dogadaj postojeci = dajById(id);
        postojeci.setNaziv(noviPodaci.getNaziv());
        postojeci.setSport(noviPodaci.getSport());
        postojeci.setDatum(noviPodaci.getDatum());
        postojeci.setStatus(noviPodaci.getStatus());
        postojeci.setKvotaDomacin(noviPodaci.getKvotaDomacin());
        postojeci.setKvotaNerijeseno(noviPodaci.getKvotaNerijeseno());
        postojeci.setKvotaGost(noviPodaci.getKvotaGost());
        return dogadajRepository.save(postojeci);
    }

    public void obrisi(Long id) {
        dajById(id);
        dogadajRepository.deleteById(id);
    }
}