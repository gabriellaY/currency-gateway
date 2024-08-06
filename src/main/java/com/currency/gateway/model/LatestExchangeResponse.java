package com.currency.gateway.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LatestExchangeResponse implements Serializable {

    private long id;
    private long timestamp;
    private String currency;
    private double rate;
}
