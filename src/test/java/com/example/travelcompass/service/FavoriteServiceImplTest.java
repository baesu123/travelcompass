package com.example.travelcompass.service;

import com.example.travelcompass.client.RestCountriesClient;
import com.example.travelcompass.common.exception.BusinessException;
import com.example.travelcompass.common.exception.ErrorCode;
import com.example.travelcompass.dto.request.FavoriteCreateRequest;
import com.example.travelcompass.dto.response.FavoriteResponse;
import com.example.travelcompass.entity.FavoriteCountry;
import com.example.travelcompass.mapper.FavoriteMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FavoriteServiceImplTest {

    private final FavoriteMapper favoriteMapper = mock(FavoriteMapper.class);
    private final RestCountriesClient restCountriesClient = mock(RestCountriesClient.class);
    private final FavoriteServiceImpl favoriteService = new FavoriteServiceImpl(favoriteMapper, restCountriesClient);

    @Test
    void 회원의_즐겨찾기_목록을_조회한다() {
        FavoriteCountry favorite = FavoriteCountry.builder()
                .id(1L).memberId(10L).countryCode("KR").createdAt(LocalDateTime.now())
                .build();
        when(favoriteMapper.findAllByMemberId(10L)).thenReturn(List.of(favorite));

        List<FavoriteResponse> responses = favoriteService.getFavorites(10L);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getCountryCode()).isEqualTo("KR");
    }

    @Test
    void 즐겨찾기를_추가하면_국가코드가_대문자로_저장된다() {
        when(favoriteMapper.countByMemberIdAndCountryCode(10L, "JP")).thenReturn(0);

        FavoriteCreateRequest request = new FavoriteCreateRequest();
        request.setCountryCode("jp");
        favoriteService.addFavorite(10L, request);

        verify(favoriteMapper, times(1)).insert(any(FavoriteCountry.class));
    }

    @Test
    void 이미_즐겨찾기한_국가는_중복_추가할_수_없다() {
        when(favoriteMapper.countByMemberIdAndCountryCode(10L, "KR")).thenReturn(1);

        FavoriteCreateRequest request = new FavoriteCreateRequest();
        request.setCountryCode("KR");

        assertThatThrownBy(() -> favoriteService.addFavorite(10L, request))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.FAVORITE_ALREADY_EXISTS);
    }

    @Test
    void 본인_소유가_아니거나_존재하지_않는_즐겨찾기_삭제는_예외가_발생한다() {
        when(favoriteMapper.deleteByIdAndMemberId(99L, 10L)).thenReturn(0);

        assertThatThrownBy(() -> favoriteService.removeFavorite(10L, 99L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.FAVORITE_NOT_FOUND);
    }

    @Test
    void 본인_즐겨찾기는_정상_삭제된다() {
        when(favoriteMapper.deleteByIdAndMemberId(1L, 10L)).thenReturn(1);

        favoriteService.removeFavorite(10L, 1L);

        verify(favoriteMapper, times(1)).deleteByIdAndMemberId(1L, 10L);
    }

}
