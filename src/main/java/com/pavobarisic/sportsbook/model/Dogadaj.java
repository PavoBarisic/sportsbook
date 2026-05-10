package com.pavobarisic.sportsbook.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "dogadaj")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Dogadaj {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Naziv ne smije biti prazan!")
    @Column(nullable = false)
    private String naziv;

    @NotNull(message = "Sport je obavezan!")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Sport sport;

    @NotNull(message = "Datum je obavezan!")
    @Column(nullable = false)
    private LocalDateTime datum;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private DogadajStatus status = DogadajStatus.UPCOMING;

    @Column
    private String rezultat;

    @NotNull(message = "Kvota domaćin je obavezna!")
    @Column(nullable = false)
    private BigDecimal kvotaDomacin;

    @NotNull(message = "Kvota neriješeno je obavezna!")
    @Column(nullable = false)
    private BigDecimal kvotaNerijeseno;

    @NotNull(message = "Kvota gost je obavezna!")
    @Column(nullable = false)
    private BigDecimal kvotaGost;

    @Enumerated(EnumType.STRING)
    @Column
    private TipOklade pobjednickiTip;

    @JsonIgnore
    @OneToMany(mappedBy = "dogadaj")
    private List<StavkaTiketa> stavke;
}