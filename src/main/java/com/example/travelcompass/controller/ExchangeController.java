package com.example.travelcompass.controller;

import com.example.travelcompass.common.response.ApiResponse;
import com.example.travelcompass.dto.request.BudgetCalculateRequest;
import com.example.travelcompass.dto.response.ExchangeResponse;
import com.example.travelcompass.service.ExchangeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 환율 조회 및 여행 예산 계산 기능을 제공하는 REST API 컨트롤러.
 * 화면(View)을 반환하지 않고 JSON 형태의 응답(ApiResponse)을 반환하는 API 전용 컨트롤러이다.
 */
// @RestController : @Controller + @ResponseBody가 합쳐진 어노테이션.
// 메서드의 반환값이 View 이름이 아니라 그대로 JSON(또는 문자열) 응답 바디로 직렬화되어 클라이언트에 전달된다.
// @RequiredArgsConstructor : Lombok이 final 필드를 매개변수로 하는 생성자를 만들어 의존성 주입(DI)을 처리한다.
// @RequestMapping("/api/exchange") : 이 컨트롤러의 모든 메서드는 "/api/exchange"로 시작하는 URL로 매핑된다(공통 prefix).
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exchange")
public class ExchangeController {

    // 환율 조회, 예산 계산 등 실제 계산/외부 API 연동 로직을 담당하는 서비스.
    private final ExchangeService exchangeService;

    /**
     * 두 통화 간 환율을 조회하고, 주어진 금액을 환산한 결과를 반환한다.
     *
     * @param from   변환 전 통화 코드 (예: "USD")
     * @param to     변환 후 통화 코드 (예: "KRW")
     * @param amount 변환할 금액
     * @return 환율 정보와 환산 결과를 담은 공통 응답 객체(ApiResponse)
     */
    @GetMapping("/rate")
    public ApiResponse<ExchangeResponse> getRate(@RequestParam String from,
                                                  @RequestParam String to,
                                                  @RequestParam double amount) {
        // @RequestParam : 쿼리스트링(?from=USD&to=KRW&amount=100)에서 값을 꺼내 각 파라미터에 바인딩한다.
        // 서비스 계층에서 실제 환율 조회 및 계산을 수행하고, 결과를 ApiResponse.success로 감싸서 반환한다.
        // ApiResponse는 프로젝트 공통 응답 포맷(성공 여부, 데이터, 메시지 등)을 통일하기 위한 래퍼 클래스이다.
        return ApiResponse.success(exchangeService.getExchangeRate(from, to, amount));
    }

    /**
     * 여행 예산(전체 금액)을 목적지 통화 기준으로 계산해주는 API.
     *
     * @param request 예산 계산에 필요한 정보(출발/도착 통화, 총 예산 등)를 담은 요청 DTO
     * @return 계산된 예산 정보를 담은 공통 응답 객체(ApiResponse)
     */
    @PostMapping("/budget")
    public ApiResponse<ExchangeResponse> calculateBudget(@Valid @RequestBody BudgetCalculateRequest request) {
        // @RequestBody : HTTP 요청 본문(JSON)을 BudgetCalculateRequest 객체로 역직렬화한다.
        // @Valid : 역직렬화된 객체에 선언된 검증 어노테이션(@NotNull 등)을 실행하여 값이 올바른지 확인한다.
        // 검증 실패 시 별도의 예외 처리기(ExceptionHandler)에서 공통적으로 에러 응답을 만들어준다고 가정한다.
        return ApiResponse.success(exchangeService.calculateBudget(request));
    }

}
