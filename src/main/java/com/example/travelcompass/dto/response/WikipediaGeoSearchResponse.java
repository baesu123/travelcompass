package com.example.travelcompass.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WikipediaGeoSearchResponse {

    private Query query;

    @Getter
    @Setter
    public static class Query {
        private List<GeoSearchItem> geosearch;
    }

    @Getter
    @Setter
    public static class GeoSearchItem {
        private long pageid;
        private String title;
        private double lat;
        private double lon;
        private double dist;
    }

}
