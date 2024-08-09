package com.currency.gateway.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Data;

@Data
public class Command {

    @JacksonXmlProperty(isAttribute = true)
    private String id;

    @JacksonXmlProperty(localName = "get")
    private LatestExchangeXmlRequest latestExchangeXmlRequest;

    @JacksonXmlProperty(localName = "history")
    private HistoricalExchangeXmlRequest historicalExchangeXmlRequest;
}
