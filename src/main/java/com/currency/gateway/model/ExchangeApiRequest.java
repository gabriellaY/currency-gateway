package com.currency.gateway.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public abstract class ExchangeApiRequest implements Serializable {

    @JsonProperty(value = "requestId")
    private String requestId;

    @JsonProperty(value = "timestamp")
    private long timestamp;

    @JsonProperty(value = "client")
    private String client;

    @JsonProperty(value = "currency")
    private String currency;
}
