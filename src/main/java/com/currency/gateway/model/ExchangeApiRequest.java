package com.currency.gateway.model;

import java.io.Serializable;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class ExchangeApiRequest implements Serializable {

    @JsonProperty(value = "requestId")
    private UUID requestId;

    @JsonProperty(value = "timestamp")
    private long timestamp;

    @JsonProperty(value = "client")
    private String client;

    @JsonProperty(value = "currency")
    private String currency;
}
