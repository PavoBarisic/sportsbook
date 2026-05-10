package com.pavobarisic.sportsbook.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "stavka_tiketa")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StavkaTiketa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "stavke"})
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tiket_id", nullable = false)
    private Tiket tiket;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "stavke"})
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dogadaj_id", nullable = false)
    private Dogadaj dogadaj;

    @Column(nullable = false)
    private BigDecimal odabranaKvota;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipOklade tip;
}