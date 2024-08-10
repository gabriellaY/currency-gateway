package com.currency.gateway.model.historicalexchange;

import com.currency.gateway.model.ExchangeApiRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class HistoricalExchangeRequest extends ExchangeApiRequest {

    @JsonProperty(value = "period")
    private int period;

    public HistoricalExchangeRequest(String requestId, String currency, String client, long timestamp, int period,
                                     String service) {
        super(requestId, timestamp, client, currency, service);
        this.period = period;
    }
}
