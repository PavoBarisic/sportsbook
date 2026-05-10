package com.pavobarisic.sportsbook.dto;

import com.pavobarisic.sportsbook.model.TipOklade;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StavkaTiketaRequest {

    @NotNull(message = "ID događaja je obavezan!")
    private Long dogadajId;

    @NotNull(message = "Tip oklade je obavezan!")
    private TipOklade tip;
}