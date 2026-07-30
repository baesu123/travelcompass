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

/**
 * 로그인한 회원의 즐겨찾기(관심 여행지) 목록을 조회/추가/삭제하는 REST API 컨트롤러.
 * 모든 메서드가 세션 기반 인증 정보(@AuthenticationPrincipal)를 통해 현재 로그인한 회원을 식별한다.
 */
// @RestController + @RequestMapping("/api/favorites") : "/api/favorites"로 시작하는 요청을 처리하는 JSON API 컨트롤러.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorites")
public class FavoriteController {

    // 즐겨찾기 등록/조회/삭제 로직을 담당하는 서비스 계층.
    private final FavoriteService favoriteService;

    /**
     * 현재 로그인한 회원의 즐겨찾기 목록을 조회한다.
     *
     * @param memberDetails 스프링 시큐리티 세션에 저장된 로그인 회원 정보(인증 주체)
     * @return 즐겨찾기 목록을 담은 공통 응답 객체(ApiResponse)
     */
    @GetMapping
    public ApiResponse<List<FavoriteResponse>> getFavorites(@AuthenticationPrincipal MemberDetails memberDetails) {
        // @AuthenticationPrincipal : 스프링 시큐리티가 현재 인증된(로그인된) 사용자 정보를 자동으로 주입해준다.
        // 즉, 별도로 세션에서 값을 꺼내는 코드를 작성하지 않아도 로그인한 회원의 정보(MemberDetails)를 바로 사용할 수 있다.
        return ApiResponse.success(favoriteService.getFavorites(memberDetails.getMemberId()));
    }

    /**
     * 현재 로그인한 회원의 즐겨찾기를 새로 추가한다.
     *
     * @param memberDetails 로그인한 회원 정보 (즐겨찾기 소유자를 식별하기 위함)
     * @param request       추가할 즐겨찾기 정보(여행지 등)를 담은 요청 DTO
     * @return 별도 반환 데이터 없이 성공 여부만 담은 공통 응답 객체(ApiResponse&lt;Void&gt;)
     */
    @PostMapping
    public ApiResponse<Void> addFavorite(@AuthenticationPrincipal MemberDetails memberDetails,
                                          @Valid @RequestBody FavoriteCreateRequest request) {
        // 회원 ID와 요청 데이터를 서비스에 전달하여 실제 저장(INSERT) 처리를 위임한다.
        favoriteService.addFavorite(memberDetails.getMemberId(), request);
        // 반환할 데이터가 없는 성공 응답이므로 data 자리에 null을 넣어준다.
        return ApiResponse.success(null);
    }

    /**
     * 현재 로그인한 회원의 즐겨찾기를 삭제한다.
     *
     * @param memberDetails 로그인한 회원 정보 (본인 소유의 즐겨찾기인지 검증하기 위함)
     * @param favoriteId    삭제할 즐겨찾기의 식별자(PK)
     * @return 별도 반환 데이터 없이 성공 여부만 담은 공통 응답 객체(ApiResponse&lt;Void&gt;)
     */
    @DeleteMapping("/{favoriteId}")
    public ApiResponse<Void> removeFavorite(@AuthenticationPrincipal MemberDetails memberDetails,
                                             @PathVariable Long favoriteId) {
        // 서비스 계층에서 favoriteId가 실제로 해당 회원 소유인지 확인 후 삭제하도록 위임한다(권한 검증 포함 가능).
        favoriteService.removeFavorite(memberDetails.getMemberId(), favoriteId);
        return ApiResponse.success(null);
    }

}
