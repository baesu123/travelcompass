package com.example.travelcompass.service;

import com.example.travelcompass.client.FrankfurterClient;
import com.example.travelcompass.dto.request.BudgetCalculateRequest;
import com.example.travelcompass.dto.response.ExchangeResponse;
import com.example.travelcompass.dto.response.FrankfurterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * 환율 조회 및 여행 예산 계산을 담당하는 서비스.
 * Frankfurter라는 외부 환율 API를 WebClient(FrankfurterClient)로 호출해서
 * 실시간 환율 정보를 가져오고, 이를 바탕으로 금액을 환산하거나 여행 예산을 계산한다.
 */
@Service
@RequiredArgsConstructor // final 필드(frankfurterClient)를 인자로 받는 생성자를 롬복이 자동 생성해줌 (생성자 주입 방식)
public class ExchangeService {

    private final FrankfurterClient frankfurterClient;

    /**
     * 통화 간 환율을 조회하고, 주어진 금액을 환산한 결과를 반환한다.
     * @param from 환전 출발 통화 코드 (예: "KRW")
     * @param to 환전 도착 통화 코드 (예: "USD")
     * @param amount 환전할 금액(from 통화 기준)
     * @return 환율/환산 금액 등이 담긴 응답 DTO
     */
    public ExchangeResponse getExchangeRate(String from, String to, double amount) {
        if (from.equalsIgnoreCase(to)) {
            // 같은 통화끼리는 외부 API를 호출할 필요가 없을 뿐만 아니라,
            // Frankfurter API는 동일 통화 쌍을 조회하면 에러를 반환하므로 여기서 바로 1:1 환율로 응답한다.
            return ExchangeResponse.builder()
                    .fromCurrency(from)
                    .toCurrency(to)
                    .amount(amount)
                    .convertedAmount(amount)
                    .rate(1.0)
                    .date(null)
                    .build();
        }

        // WebClient는 원래 비동기(Mono)로 동작하지만, 여기서는 결과가 나올 때까지 기다려야 하는
        // 동기 방식 서비스 메서드이므로 block()으로 강제로 응답을 기다린다(최대 5초, 타임아웃 방지).
        FrankfurterResponse response = frankfurterClient.getLatestRate(from, to)
                .block(Duration.ofSeconds(5));

        // Frankfurter 응답의 rates는 Map 형태(통화코드 -> 환율)이지만 여기서는 통화 하나만 요청했으므로
        // 첫 번째 값만 꺼내서 사용한다.
        double rate = response.getRates().values().stream().findFirst().orElseThrow();

        return ExchangeResponse.builder()
                .fromCurrency(from)
                .toCurrency(to)
                .amount(amount)
                .convertedAmount(amount * rate)
                .rate(rate)
                .date(response.getDate())
                .build();
    }

    /**
     * 여행 예산(숙박비 등)을 원화 기준으로 계산한 뒤, 목표 통화로 환산한 결과를 반환한다.
     * @param request 여행 일수, 1박당 원화 비용, 목표 통화 코드가 담긴 요청 DTO
     * @return 목표 통화로 환산된 예산 정보가 담긴 응답 DTO
     */
    public ExchangeResponse calculateBudget(BudgetCalculateRequest request) {
        // 총 예산 = 여행 일수 * 1박당 비용(원화). long으로 캐스팅해 오버플로우를 방지한다.
        long totalKrw = (long) request.getDays() * request.getCostPerNightKrw();
        return getExchangeRate("KRW", request.getTargetCurrency(), totalKrw);
    }

}
