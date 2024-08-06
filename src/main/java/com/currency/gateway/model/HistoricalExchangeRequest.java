package com.currency.gateway.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HistoricalExchangeRequest extends ExchangeApiRequest {

    @JsonProperty(value = "period")
    private int period;
}
