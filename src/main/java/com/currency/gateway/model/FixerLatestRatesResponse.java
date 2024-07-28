package com.currency.gateway.model;

import java.sql.Date;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Represents the result returned from fixer get latest rates request.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FixerLatestRatesResponse {

    @JsonProperty(value = "success")
    boolean success;

    @JsonProperty(value = "timestamp")
    long timestamp;

    @JsonProperty(value = "base")
    String base;

    @JsonProperty(value = "date")
    Date date;

    @JsonProperty(value = "rates")
    HashMap<String, Double> rates;
}
