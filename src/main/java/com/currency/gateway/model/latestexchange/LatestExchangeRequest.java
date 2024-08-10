package com.currency.gateway.model.latestexchange;

import com.currency.gateway.model.ExchangeApiRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class LatestExchangeRequest extends ExchangeApiRequest {

    public LatestExchangeRequest(String requestId, String currency, String client, long timestamp, String service) {
        super(requestId, timestamp, client, currency, service);
    }
}
