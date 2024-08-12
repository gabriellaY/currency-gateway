package com.currency.gateway.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LatestExchangeDto implements Serializable {

    private long id;
    private long timestamp;
    private String currency;
    private double rate;
}
