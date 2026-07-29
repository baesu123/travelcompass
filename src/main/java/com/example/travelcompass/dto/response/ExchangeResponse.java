package com.example.travelcompass.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ExchangeResponse {

    private String fromCurrency;
    private String toCurrency;
    private double amount;
    private double convertedAmount;
    private double rate;
    private String date;

}
