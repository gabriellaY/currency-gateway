package com.currency.gateway.model;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents the result returned from fixer get latest rates request.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
public class CurrenciesResponse {

    @JsonProperty(value = "success")
    boolean success;

    @JsonProperty(value = "symbols")
    HashMap<String, String> symbols;
}
