package com.currency.gateway.model;

import java.io.Serializable;
import java.sql.Date;
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
public class FixerLatestRatesResponse implements Serializable {

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
