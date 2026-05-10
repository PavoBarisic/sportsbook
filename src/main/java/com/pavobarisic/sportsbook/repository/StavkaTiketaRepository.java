package com.pavobarisic.sportsbook.repository;

import com.pavobarisic.sportsbook.model.StavkaTiketa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StavkaTiketaRepository extends JpaRepository<StavkaTiketa, Long> {
}