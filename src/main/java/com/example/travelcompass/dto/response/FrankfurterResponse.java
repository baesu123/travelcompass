package com.example.travelcompass.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 환율 정보를 제공하는 외부 API인 Frankfurter API의 응답 JSON을 그대로 매핑하기 위한 DTO.
 * (예: https://api.frankfurter.app/latest?from=USD&to=KRW 응답 형태)
 * Jackson이 JSON 응답을 이 클래스의 필드명과 매칭시켜 자동으로 역직렬화(deserialize)한다.
 *
 * Lombok 사용 팁:
 * - @Getter / @Setter : Jackson이 역직렬화 시 각 필드에 값을 채워 넣으려면(setter) 그리고
 *   이후 코드에서 값을 꺼내 쓰려면(getter) 두 메서드가 모두 필요하다. 그래서 응답 매핑용
 *   DTO는 보통 @Getter와 @Setter를 함께 사용한다.
 */
@Getter
@Setter
public class FrankfurterResponse {

    // 조회 기준 금액 (예: amount=1이면 1단위 기준 환율)
    private double amount;
    // 기준 통화 코드 (예: "USD")
    private String base;
    // 환율 기준 날짜 (예: "2026-07-30")
    private String date;
    // 통화 코드(key) -> 환율(value) 매핑. 예: {"KRW": 1350.5, "JPY": 150.2}
    private Map<String, Double> rates;

}
