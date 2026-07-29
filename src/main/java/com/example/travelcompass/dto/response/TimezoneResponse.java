package com.example.travelcompass.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TimezoneResponse {

    private String countryCode;
    private String timezone;
    private String localTime;
    private String koreaTime;
    private double timeDifferenceHours;

}
