package com.currency.gateway.model.latestexchange;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "response")
@AllArgsConstructor
public class LatestExchangeXmlResponse {

    LatestExchangeResponse latestExchangeData;
}
