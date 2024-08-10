package com.currency.gateway.model;

import com.currency.gateway.model.historicalexchange.HistoricalExchangeXmlRequest;
import com.currency.gateway.model.latestexchange.LatestExchangeXmlRequest;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Data;

@Data
public class Command {

    @JacksonXmlProperty(isAttribute = true)
    private String id;

    @JacksonXmlProperty(isAttribute = true)
    private String service;

    @JacksonXmlProperty(localName = "get")
    private LatestExchangeXmlRequest latestExchangeXmlRequest;

    @JacksonXmlProperty(localName = "history")
    private HistoricalExchangeXmlRequest historicalExchangeXmlRequest;
}
