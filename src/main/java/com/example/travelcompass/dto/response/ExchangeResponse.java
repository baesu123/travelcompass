package com.example.travelcompass.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 환율 계산(예산 계산기 등) 결과를 클라이언트에게 응답으로 내려주기 위한 DTO.
 * (내부 서비스 -> 컨트롤러 -> 클라이언트 응답용. 외부 API 원본 응답이 아니라, 서비스에서
 * 계산을 마친 결과를 담는다.)
 *
 * Lombok 사용 팁:
 * - @Getter : 각 필드에 대한 getter를 자동 생성.
 * - @Builder : ExchangeResponse.builder()...build() 형태로 가독성 좋게 객체를 생성.
 * - @AllArgsConstructor : 모든 필드를 받는 생성자를 자동 생성 (Builder가 내부적으로 사용).
 */
@Getter
@Builder
@AllArgsConstructor
public class ExchangeResponse {

    // 변환 전 통화 코드 (예: "KRW")
    private String fromCurrency;
    // 변환 후 통화 코드 (예: "USD")
    private String toCurrency;
    // 변환 전 금액
    private double amount;
    // 환율 적용 후 변환된 금액
    private double convertedAmount;
    // 적용된 환율 (fromCurrency 1단위 당 toCurrency 금액)
    private double rate;
    // 환율 기준 날짜
    private String date;

}
