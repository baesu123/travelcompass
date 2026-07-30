package com.example.travelcompass.controller;

import com.example.travelcompass.config.MemberDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * REST API가 아닌, 화면(Thymeleaf 템플릿)을 그대로 보여주는 페이지 라우팅 전용 컨트롤러.
 * 실제 데이터 조회는 각 화면의 자바스크립트가 REST API(다른 @RestController들)를 호출해서 채우고,
 * 이 컨트롤러는 로그인한 사용자의 닉네임 등 화면 렌더링에 필요한 최소한의 공통 정보만 Model에 담아 넘긴다.
 */
@Controller
@RequiredArgsConstructor
public class PageController {

    /**
     * 메인 홈 화면(지도 기반 화면)을 보여준다.
     *
     * @param memberDetails 로그인한 회원 정보 (비로그인 시 null)
     * @param model         화면에 전달할 데이터를 담는 객체
     * @return 뷰 이름 "index" (templates/index.html)
     */
    @GetMapping("/")
    public String index(@AuthenticationPrincipal MemberDetails memberDetails, Model model) {
        // 로그인 여부와 상관없이 접근 가능한 페이지이므로, nicknameOf에서 null 체크를 해준다.
        model.addAttribute("nickname", nicknameOf(memberDetails));
        return "index";
    }

    /**
     * 예전에 별도 지도 페이지("/map")로 있던 기능이 홈 화면에 통합되면서,
     * 하위 호환을 위해 "/map" 요청이 오면 홈("/")으로 리다이렉트만 시켜준다.
     *
     * @return "/"로 리다이렉트하는 지시자 문자열
     */
    @GetMapping("/map")
    public String map() {
        return "redirect:/";
    }

    /**
     * 특정 국가의 상세 정보 화면을 보여준다. 실제 상세 데이터는 화면의 JS가 CountryController API로 조회한다.
     *
     * @param memberDetails 로그인한 회원 정보 (비로그인 시 null)
     * @param countryCode   조회할 국가 코드 (URL 경로에서 추출)
     * @param model         화면에 전달할 데이터를 담는 객체
     * @return 뷰 이름 "country/detail"
     */
    @GetMapping("/country/{countryCode}")
    public String countryDetail(@AuthenticationPrincipal MemberDetails memberDetails,
                                 @PathVariable String countryCode,
                                 Model model) {
        model.addAttribute("nickname", nicknameOf(memberDetails));
        // 국가 코드는 대소문자 표기가 섞여 들어올 수 있어, 대문자로 통일해서 화면/후속 API 호출에 일관되게 사용한다.
        model.addAttribute("countryCode", countryCode.toUpperCase());
        return "country/detail";
    }

    /**
     * 즐겨찾기 목록 화면을 보여준다.
     *
     * @param memberDetails 로그인한 회원 정보
     * @param model         화면에 전달할 데이터를 담는 객체
     * @return 뷰 이름 "favorite/list"
     */
    @GetMapping("/favorites")
    public String favorites(@AuthenticationPrincipal MemberDetails memberDetails, Model model) {
        model.addAttribute("nickname", nicknameOf(memberDetails));
        return "favorite/list";
    }

    /**
     * 여행 준비물 체크리스트 화면을 보여준다.
     *
     * @param memberDetails 로그인한 회원 정보
     * @param model         화면에 전달할 데이터를 담는 객체
     * @return 뷰 이름 "checklist/list"
     */
    @GetMapping("/checklist")
    public String checklist(@AuthenticationPrincipal MemberDetails memberDetails, Model model) {
        model.addAttribute("nickname", nicknameOf(memberDetails));
        return "checklist/list";
    }

    /**
     * 리뷰 목록 화면을 보여준다. 특정 국가의 리뷰만 필터링해서 볼 수도 있다.
     *
     * @param memberDetails 로그인한 회원 정보
     * @param countryCode   필터링할 국가 코드 (선택값, 없으면 전체 리뷰)
     * @param model         화면에 전달할 데이터를 담는 객체
     * @return 뷰 이름 "review/list"
     */
    @GetMapping("/reviews")
    public String reviews(@AuthenticationPrincipal MemberDetails memberDetails,
                           @RequestParam(required = false) String countryCode,
                           Model model) {
        // required = false : 쿼리 파라미터가 없어도 400 에러 없이 null로 받을 수 있도록 허용한다.
        model.addAttribute("nickname", nicknameOf(memberDetails));
        model.addAttribute("countryCode", countryCode);
        return "review/list";
    }

    /**
     * 여행 예산 계산기 화면을 보여준다.
     *
     * @param memberDetails 로그인한 회원 정보
     * @param model         화면에 전달할 데이터를 담는 객체
     * @return 뷰 이름 "budget/calculator"
     */
    @GetMapping("/budget")
    public String budget(@AuthenticationPrincipal MemberDetails memberDetails, Model model) {
        model.addAttribute("nickname", nicknameOf(memberDetails));
        return "budget/calculator";
    }

    /**
     * 리뷰 상세(댓글 포함) 화면을 보여준다.
     *
     * @param memberDetails 로그인한 회원 정보
     * @param reviewId      조회할 리뷰의 식별자(PK)
     * @param model         화면에 전달할 데이터를 담는 객체
     * @return 뷰 이름 "review/detail"
     */
    @GetMapping("/reviews/{reviewId}")
    public String reviewDetail(@AuthenticationPrincipal MemberDetails memberDetails,
                                @PathVariable Long reviewId,
                                Model model) {
        model.addAttribute("nickname", nicknameOf(memberDetails));
        model.addAttribute("reviewId", reviewId);
        return "review/detail";
    }

    /**
     * 로그인 회원 정보에서 닉네임만 안전하게 꺼내는 헬퍼 메서드.
     * 비로그인 상태(memberDetails가 null)에서도 NullPointerException 없이 null을 반환하도록 방어 처리한다.
     *
     * @param memberDetails 로그인한 회원 정보 (비로그인 시 null)
     * @return 로그인한 경우 닉네임, 비로그인인 경우 null
     */
    private String nicknameOf(MemberDetails memberDetails) {
        return memberDetails != null ? memberDetails.getNickname() : null;
    }

}
