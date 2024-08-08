package com.currency.gateway.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Data;

@Data
public class LatestExchangeXmlRequest {

    @JacksonXmlProperty(isAttribute = true)
    private String consumer;

    @JacksonXmlProperty(localName = "currency")
    private String currency;
}
