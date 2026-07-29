package com.example.travelcompass.controller;

import com.example.travelcompass.common.response.ApiResponse;
import com.example.travelcompass.config.MemberDetails;
import com.example.travelcompass.dto.request.FavoriteCreateRequest;
import com.example.travelcompass.dto.response.FavoriteResponse;
import com.example.travelcompass.service.FavoriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping
    public ApiResponse<List<FavoriteResponse>> getFavorites(@AuthenticationPrincipal MemberDetails memberDetails) {
        return ApiResponse.success(favoriteService.getFavorites(memberDetails.getMemberId()));
    }

    @PostMapping
    public ApiResponse<Void> addFavorite(@AuthenticationPrincipal MemberDetails memberDetails,
                                          @Valid @RequestBody FavoriteCreateRequest request) {
        favoriteService.addFavorite(memberDetails.getMemberId(), request);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{favoriteId}")
    public ApiResponse<Void> removeFavorite(@AuthenticationPrincipal MemberDetails memberDetails,
                                             @PathVariable Long favoriteId) {
        favoriteService.removeFavorite(memberDetails.getMemberId(), favoriteId);
        return ApiResponse.success(null);
    }

}
