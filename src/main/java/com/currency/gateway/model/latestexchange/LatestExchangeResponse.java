package com.currency.gateway.model.latestexchange;

import java.io.Serializable;
import java.util.List;

import com.currency.gateway.dto.LatestExchangeDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LatestExchangeResponse implements Serializable {
    
    List<LatestExchangeDto> latestExchanges;
}
