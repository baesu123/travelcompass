package com.example.travelcompass.controller;

import com.example.travelcompass.client.RestCountriesClient;
import com.example.travelcompass.common.response.ApiResponse;
import com.example.travelcompass.config.MemberDetails;
import com.example.travelcompass.dto.response.CountryDetailResponse;
import com.example.travelcompass.dto.response.CountrySearchResultResponse;
import com.example.travelcompass.service.CountryFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 국가 검색 및 국가 상세 정보(환율, 시차, 기후, 즐겨찾기 여부 등을 종합)를 제공하는 REST API 컨트롤러.
 * 로그인 여부와 관계없이 접근 가능하며, 로그인한 경우에는 개인화된 정보(즐겨찾기 여부 등)를 함께 내려준다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/countries")
public class CountryController {

    // 여러 서비스(환율, 시차, 기후, 즐겨찾기 등)를 조합해 국가 상세 정보를 만들어주는 파사드(Facade) 서비스.
    // 파사드 패턴 : 여러 하위 시스템의 복잡한 호출을 하나의 단순한 인터페이스로 감싸서 제공하는 방식.
    private final CountryFacadeService countryFacadeService;
    // 정적 countries.json 기반으로 국가 정보를 검색/제공하는 클라이언트(원래는 RestCountries 외부 API였으나
    // 키 인증이 필요해져 정적 데이터로 대체되었다).
    private final RestCountriesClient restCountriesClient;

    /**
     * 키워드(국가명, 한글/영문 등)로 국가를 검색한다. 로그인이 필요 없는 공개 API이다.
     *
     * @param keyword 검색할 국가명 키워드 (한국어 국가명도 지원)
     * @return 검색된 국가 목록을 담은 공통 응답 객체(ApiResponse)
     */
    @GetMapping("/search")
    public ApiResponse<List<CountrySearchResultResponse>> searchCountries(@RequestParam String keyword) {
        return ApiResponse.success(restCountriesClient.searchByKeyword(keyword));
    }

    /**
     * 국가 코드로 국가 상세 정보(환율/시차/기후/즐겨찾기 여부 등)를 조회한다.
     * 비로그인 사용자도 조회할 수 있지만, 로그인한 사용자에게는 즐겨찾기 여부 등 개인화된 데이터가 추가로 포함된다.
     *
     * @param memberDetails 로그인한 회원 정보. 비로그인 상태면 null일 수 있다.
     * @param countryCode   조회할 국가 코드
     * @return 국가 상세 정보를 담은 공통 응답 객체(ApiResponse)
     */
    @GetMapping("/{countryCode}")
    public ApiResponse<CountryDetailResponse> getCountryDetail(@AuthenticationPrincipal MemberDetails memberDetails,
                                                                 @PathVariable String countryCode) {
        // @AuthenticationPrincipal은 비로그인 상태(익명 사용자)일 경우 null이 주입될 수 있으므로
        // 반드시 null 체크 후 사용해야 한다 (로그인 필수 API와 달리 이 API는 비회원도 접근 가능하기 때문).
        Long memberId = memberDetails != null ? memberDetails.getMemberId() : null;
        String nickname = memberDetails != null ? memberDetails.getNickname() : null;
        // 파사드 서비스가 내부적으로 환율/시차/기후/즐겨찾기 서비스를 조합하여 하나의 응답으로 만들어준다.
        return ApiResponse.success(countryFacadeService.getCountryDetail(countryCode, memberId, nickname));
    }

}
