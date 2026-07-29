package com.example.travelcompass.service;

import com.example.travelcompass.common.exception.BusinessException;
import com.example.travelcompass.common.exception.ErrorCode;
import com.example.travelcompass.dto.request.FavoriteCreateRequest;
import com.example.travelcompass.dto.response.FavoriteResponse;
import com.example.travelcompass.entity.FavoriteCountry;
import com.example.travelcompass.mapper.FavoriteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteMapper favoriteMapper;

    @Override
    public List<FavoriteResponse> getFavorites(Long memberId) {
        return favoriteMapper.findAllByMemberId(memberId).stream()
                .map(favorite -> FavoriteResponse.builder()
                        .id(favorite.getId())
                        .countryCode(favorite.getCountryCode())
                        .createdAt(favorite.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addFavorite(Long memberId, FavoriteCreateRequest request) {
        String countryCode = request.getCountryCode().toUpperCase();

        if (favoriteMapper.countByMemberIdAndCountryCode(memberId, countryCode) > 0) {
            throw new BusinessException(ErrorCode.FAVORITE_ALREADY_EXISTS);
        }

        FavoriteCountry favoriteCountry = FavoriteCountry.builder()
                .memberId(memberId)
                .countryCode(countryCode)
                .build();

        favoriteMapper.insert(favoriteCountry);
    }

    @Override
    @Transactional
    public void removeFavorite(Long memberId, Long favoriteId) {
        int deleted = favoriteMapper.deleteByIdAndMemberId(favoriteId, memberId);
        if (deleted == 0) {
            throw new BusinessException(ErrorCode.FAVORITE_NOT_FOUND);
        }
    }

}
