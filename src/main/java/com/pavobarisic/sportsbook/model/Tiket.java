package com.pavobarisic.sportsbook.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tiket")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tiket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "tiketi"})
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "korisnik_id", nullable = false)
    private Korisnik korisnik;

    @Column(nullable = false)
    private BigDecimal ukupnaUloga;

    @Column(nullable = false)
    private BigDecimal potencijalnaDobitak;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TiketStatus status = TiketStatus.AKTIVAN;

    @Column(nullable = false)
    private LocalDateTime datumPostavljanja;

    @OneToMany(mappedBy = "tiket", cascade = CascadeType.ALL)
    private List<StavkaTiketa> stavke;
}