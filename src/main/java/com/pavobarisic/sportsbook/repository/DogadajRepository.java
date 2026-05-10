package com.pavobarisic.sportsbook.repository;

import com.pavobarisic.sportsbook.model.Dogadaj;
import com.pavobarisic.sportsbook.model.DogadajStatus;
import com.pavobarisic.sportsbook.model.Sport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DogadajRepository extends JpaRepository<Dogadaj, Long> {

    List<Dogadaj> findByStatus(DogadajStatus status);
    List<Dogadaj> findBySport(Sport sport);
    List<Dogadaj> findByStatusAndSport(DogadajStatus status, Sport sport);
}