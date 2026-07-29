package com.example.travelcompass.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class RestCountryResponse {

    private String cca2;
    private String cca3;
    private Name name;
    private List<String> capital;
    private String region;
    private String subregion;
    private Map<String, Currency> currencies;
    private Map<String, String> languages;
    private List<Double> latlng;
    private Flags flags;

    @Getter
    @Setter
    public static class Name {
        private String common;
        private String official;
    }

    @Getter
    @Setter
    public static class Currency {
        private String name;
        private String symbol;
    }

    @Getter
    @Setter
    public static class Flags {
        private String png;
        private String svg;
    }

}
