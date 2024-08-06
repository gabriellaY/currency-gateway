package com.currency.gateway.model;

import java.io.Serializable;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents the result returned from fixer get currencies request.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
public class CurrenciesResponse implements Serializable {

    @JsonProperty(value = "success")
    boolean success;

    @JsonProperty(value = "symbols")
    HashMap<String, String> symbols;
}
