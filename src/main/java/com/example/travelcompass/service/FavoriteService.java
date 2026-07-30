package com.example.travelcompass.service;

import com.example.travelcompass.dto.request.FavoriteCreateRequest;
import com.example.travelcompass.dto.response.FavoriteResponse;

import java.util.List;

/**
 * 회원의 "즐겨찾기 국가" 기능을 정의하는 서비스 인터페이스.
 * 구현체(FavoriteServiceImpl)와 분리해두면, 컨트롤러는 이 인터페이스만 바라보고
 * 실제 구현(Mapper 호출 방식 등)이 바뀌어도 영향을 받지 않는다. (스프링의 다형성 활용)
 */
public interface FavoriteService {

    /**
     * 특정 회원이 즐겨찾기한 국가 목록을 조회한다.
     * @param memberId 조회할 회원의 ID
     * @return 즐겨찾기한 국가 정보 목록(FavoriteResponse) - 비어있으면 빈 리스트
     */
    List<FavoriteResponse> getFavorites(Long memberId);

    /**
     * 회원의 즐겨찾기 목록에 국가를 추가한다.
     * @param memberId 즐겨찾기를 추가할 회원의 ID
     * @param request 추가할 국가 정보(국가 코드 등)가 담긴 요청 DTO
     */
    void addFavorite(Long memberId, FavoriteCreateRequest request);

    /**
     * 회원의 즐겨찾기 목록에서 특정 항목을 삭제한다.
     * @param memberId 삭제를 요청한 회원의 ID(본인 소유 항목인지 검증용)
     * @param favoriteId 삭제할 즐겨찾기 항목의 ID
     */
    void removeFavorite(Long memberId, Long favoriteId);

}
