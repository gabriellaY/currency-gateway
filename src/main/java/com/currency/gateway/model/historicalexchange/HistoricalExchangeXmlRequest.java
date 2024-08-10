package com.currency.gateway.model.historicalexchange;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Data;

@Data
public class HistoricalExchangeXmlRequest {
    @JacksonXmlProperty(isAttribute = true)
    private String consumer;

    @JacksonXmlProperty(localName = "currency", isAttribute = true)
    private String currency;

    @JacksonXmlProperty(localName = "period", isAttribute = true)
    private int period;
}
