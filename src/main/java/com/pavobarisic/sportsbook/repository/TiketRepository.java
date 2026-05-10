package com.pavobarisic.sportsbook.repository;

import com.pavobarisic.sportsbook.model.Tiket;
import com.pavobarisic.sportsbook.model.TiketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TiketRepository extends JpaRepository<Tiket, Long> {

    List<Tiket> findByKorisnikId(Long korisnikId);
    List<Tiket> findByStatus(TiketStatus status);
}