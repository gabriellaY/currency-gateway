package com.currency.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LatestExchangeDto {

    private long id;
    private long timestamp;
    private String currency;
    private double rate;
}
