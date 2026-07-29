package com.example.travelcompass.service;

import com.example.travelcompass.dto.request.FavoriteCreateRequest;
import com.example.travelcompass.dto.response.FavoriteResponse;

import java.util.List;

public interface FavoriteService {

    List<FavoriteResponse> getFavorites(Long memberId);

    void addFavorite(Long memberId, FavoriteCreateRequest request);

    void removeFavorite(Long memberId, Long favoriteId);

}
