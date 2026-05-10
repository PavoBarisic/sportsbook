package com.pavobarisic.sportsbook.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class TiketRequest {

    @NotNull(message = "Ulog ne smije biti prazan!")
    @Positive(message = "Ulog mora biti pozitivan broj!")
    private BigDecimal ulog;

    @NotEmpty(message = "Tiket mora imati barem jednu stavku!")
    private List<StavkaTiketaRequest> stavke;
}