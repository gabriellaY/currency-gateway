package com.currency.gateway.model.historicalexchange;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "response")
@AllArgsConstructor
public class HistoricalExchangeXmlResponse {

    HistoricalExchangeResponse history;
}
