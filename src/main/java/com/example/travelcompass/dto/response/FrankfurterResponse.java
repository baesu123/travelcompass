package com.example.travelcompass.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class FrankfurterResponse {

    private double amount;
    private String base;
    private String date;
    private Map<String, Double> rates;

}
