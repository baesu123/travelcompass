package com.example.travelcompass.dto.response;

import lombok.Getter;
import lombok.Setter;

/**
 * 특정 국가/지역의 평균 기후(연중 평균 기온·강수량) 및 여행 준비물 관련 안내 정보를
 * 클라이언트에게 응답으로 내려주기 위한 DTO.
 * (내부 서비스 -> 컨트롤러 응답용. 실시간 외부 API 응답이 아니라, 프로젝트 내부에 미리
 * 준비해 둔 정적 기후 데이터를 담아 내려준다.)
 *
 * Lombok 사용 팁:
 * - @Getter / @Setter : 서비스 계층에서 정적 데이터를 채워 넣고(setter), 컨트롤러/뷰에서
 *   값을 꺼내 쓰기(getter) 위해 함께 사용한다.
 */
@Getter
@Setter
public class ClimateInfo {

    // 기후 정보의 기준이 되는 지역명 (예: "Eastern Asia")
    private String region;
    // 해당 지역의 평균 기온 (섭씨)
    private double averageTemperature;
    // 해당 지역의 평균 강수량
    private double averagePrecipitation;
    // 추천 복장/의류 안내 문구
    private String recommendedClothing;
    // 여행 시 참고할 팁 문구 (예: 우기 주의, 자외선 대비 등)
    private String travelTip;

}
