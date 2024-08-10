package com.currency.gateway.model.historicalexchange;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoricalExchangeResponse implements Serializable {

    private long timestamp;
    private String currency;
    private int period;
    private List<HistoricalExchangeData> exchangeHistory;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HistoricalExchangeData {
        private long timestamp;
        private double rate;
    }
}
