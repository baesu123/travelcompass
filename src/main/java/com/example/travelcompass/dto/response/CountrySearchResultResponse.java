package com.example.travelcompass.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 국가 검색(자동완성 등) 결과 항목 하나를 클라이언트에게 응답으로 내려주기 위한 DTO.
 * (내부 서비스 -> 컨트롤러 -> 클라이언트 응답용. 외부 API 매핑용이 아님)
 * 한국어 국가명으로도 검색이 가능하도록 nameKo, nameEn을 함께 내려준다.
 *
 * Lombok 사용 팁:
 * - @Getter : 각 필드에 대한 getter를 자동 생성.
 * - @RequiredArgsConstructor : final로 선언된 필드(code, nameKo, nameEn)를 모두 파라미터로
 *   받는 생성자를 자동 생성한다. 필드가 final이라 값은 생성 시점에 한 번만 설정되고 이후
 *   변경할 수 없다(불변 객체).
 */
@Getter
@RequiredArgsConstructor
public class CountrySearchResultResponse {

    // 국가 코드 (예: "KR")
    private final String code;
    // 국가명 (한국어, 예: "대한민국")
    private final String nameKo;
    // 국가명 (영어, 예: "South Korea")
    private final String nameEn;

}
