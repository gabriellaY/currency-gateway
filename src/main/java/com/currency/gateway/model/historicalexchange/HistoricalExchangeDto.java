package com.currency.gateway.model.historicalexchange;

import java.io.Serializable;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoricalExchangeDto implements Serializable {
    @NotNull
    private long timestamp;

    @NotNull
    private double rate;

    @NotNull
    private String exchangeCurrency;
}
