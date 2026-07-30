package com.example.travelcompass.service;

import com.example.travelcompass.client.RestCountriesClient;
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
    private final RestCountriesClient restCountriesClient;

    @Override
    public List<FavoriteResponse> getFavorites(Long memberId) {
        return favoriteMapper.findAllByMemberId(memberId).stream()
                .map(favorite -> FavoriteResponse.builder()
                        .id(favorite.getId())
                        .countryCode(favorite.getCountryCode())
                        .countryName(favorite.getCountryName())
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
                .countryName(restCountriesClient.getKoreanName(countryCode))
                .build();

        favoriteMapper.insert(favoriteCountry);
    }

    @Override
    @Transactional
    public void removeFavorite(Long memberId, Long favoriteId) {
        requireAffected(favoriteMapper.deleteByIdAndMemberId(favoriteId, memberId), ErrorCode.FAVORITE_NOT_FOUND);
    }

    /**
     * 소유권 검증(id + memberId)이 걸린 update/delete 쿼리는 영향받은 행이 0건이면
     * "존재하지 않음"과 "내 소유가 아님"을 구분하지 않고 동일하게 NOT_FOUND로 응답한다.
     * (다른 회원의 데이터 존재 여부를 노출하지 않기 위한 의도적인 설계)
     */
    private void requireAffected(int affectedRows, ErrorCode errorCode) {
        if (affectedRows == 0) {
            throw new BusinessException(errorCode);
        }
    }

}
