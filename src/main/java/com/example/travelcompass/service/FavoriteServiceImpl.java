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

/**
 * FavoriteService의 구현체. MyBatis Mapper(FavoriteMapper)로 DB를 다루고,
 * 필요 시 RestCountriesClient(WebClient 기반 외부 API 클라이언트)를 호출해
 * 국가의 한글 이름을 채워 넣는다.
 */
@Service
@RequiredArgsConstructor // final 필드들(favoriteMapper, restCountriesClient)을 생성자 주입받는다.
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteMapper favoriteMapper;
    private final RestCountriesClient restCountriesClient; // 국가 코드로 한글 국가명을 조회하는 외부 API 클라이언트

    /**
     * 회원의 즐겨찾기 국가 목록을 조회하고, Entity를 응답 DTO로 변환해서 반환한다.
     * @param memberId 조회할 회원의 ID
     * @return 즐겨찾기 국가 목록
     */
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

    /**
     * 즐겨찾기에 국가를 새로 추가한다. 이미 즐겨찾기된 국가라면 중복 추가를 막는다.
     * @param memberId 즐겨찾기를 추가할 회원의 ID
     * @param request 추가할 국가 코드가 담긴 요청 DTO
     */
    @Override
    @Transactional // 중복 검사(SELECT)와 저장(INSERT)을 하나의 트랜잭션으로 묶어 일관성을 보장
    public void addFavorite(Long memberId, FavoriteCreateRequest request) {
        // 국가 코드는 대소문자 표기가 섞여 들어올 수 있으므로 대문자로 통일해서 저장/비교한다.
        String countryCode = request.getCountryCode().toUpperCase();

        if (favoriteMapper.countByMemberIdAndCountryCode(memberId, countryCode) > 0) {
            // 이미 즐겨찾기에 있는 국가면 중복 저장을 막고 비즈니스 예외를 던진다.
            throw new BusinessException(ErrorCode.FAVORITE_ALREADY_EXISTS);
        }

        FavoriteCountry favoriteCountry = FavoriteCountry.builder()
                .memberId(memberId)
                .countryCode(countryCode)
                // 외부 API(RestCountriesClient)를 호출해 국가 코드에 해당하는 한글 이름을 가져와 저장해둔다.
                // 이렇게 저장해두면 목록 조회 시마다 외부 API를 다시 호출하지 않아도 된다.
                .countryName(restCountriesClient.getKoreanName(countryCode))
                .build();

        favoriteMapper.insert(favoriteCountry);
    }

    /**
     * 즐겨찾기 항목을 삭제한다. id와 memberId를 함께 조건으로 걸어 본인 소유 항목만 삭제되도록 보호한다.
     * @param memberId 삭제를 요청한 회원의 ID
     * @param favoriteId 삭제할 즐겨찾기 항목의 ID
     */
    @Override
    @Transactional
    public void removeFavorite(Long memberId, Long favoriteId) {
        // DELETE 쿼리의 영향받은 행 수가 0이면 존재하지 않거나 남의 소유이므로 NOT_FOUND 처리한다.
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
