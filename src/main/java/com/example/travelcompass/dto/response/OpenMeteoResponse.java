package com.example.travelcompass.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OpenMeteoResponse {

    private double latitude;
    private double longitude;
    private String timezone;
    private Daily daily;

    @Getter
    @Setter
    public static class Daily {

        private List<String> time;

        @JsonProperty("temperature_2m_max")
        private List<Double> temperature2mMax;

        @JsonProperty("temperature_2m_min")
        private List<Double> temperature2mMin;

        @JsonProperty("precipitation_probability_max")
        private List<Integer> precipitationProbabilityMax;

    }

}
