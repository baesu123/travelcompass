package com.example.travelcompass.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClimateInfo {

    private String region;
    private double averageTemperature;
    private double averagePrecipitation;
    private String recommendedClothing;
    private String travelTip;

}
