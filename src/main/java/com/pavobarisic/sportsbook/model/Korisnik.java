package com.pavobarisic.sportsbook.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "korisnik")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Korisnik {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Ime ne smije biti prazno!")
    @Column(nullable = false)
    private String ime;

    @NotBlank(message = "Prezime ne smije biti prazno!")
    @Column(nullable = false)
    private String prezime;

    @Email(message = "Email nije ispravan!")
    @NotBlank(message = "Email ne smije biti prazan!")
    @Column(nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @NotBlank(message = "Lozinka ne smije biti prazna!")
    @Column(nullable = false)
    private String lozinka;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Rola rola = Rola.USER;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal stanje = new BigDecimal("1000.00");

    @Column(nullable = false)
    private LocalDateTime datumRegistracije;

    @JsonIgnore
    @OneToMany(mappedBy = "korisnik")
    private List<Tiket> tiketi;
}