package com.currency.gateway.dto;

import java.io.Serializable;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LatestExchangeDto implements Serializable {

    @NotNull
    private long id;

    @NotNull
    private long timestamp;
    
    @NotNull
    private String currency;
    
    @NotNull
    private double rate;
}
