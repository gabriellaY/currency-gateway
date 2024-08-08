package com.currency.gateway.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LatestExchangeRequest extends ExchangeApiRequest {

}
