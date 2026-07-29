package com.example.travelcompass.service;

import com.example.travelcompass.dto.response.TimezoneResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TimezoneServiceTest {

    private final TimezoneService timezoneService = new TimezoneService(new ObjectMapper());

    @Test
    void 일본은_한국과_시차가_없다() {
        TimezoneResponse response = timezoneService.getTimezone("jp");

        assertThat(response.getTimezone()).isEqualTo("Asia/Tokyo");
        assertThat(response.getTimeDifferenceHours()).isEqualTo(0.0);
        assertThat(response.getLocalTime().substring(11)).isEqualTo(response.getKoreaTime().substring(11));
    }

    @Test
    void 미국_동부는_한국보다_시간이_느리다() {
        TimezoneResponse response = timezoneService.getTimezone("us");

        assertThat(response.getTimezone()).isEqualTo("America/New_York");
        assertThat(response.getTimeDifferenceHours()).isNegative();
    }

    @Test
    void 존재하지_않는_시간대_매핑이면_예외가_발생한다() {
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> timezoneService.getTimezone("bv"))
                .isInstanceOf(com.example.travelcompass.common.exception.BusinessException.class);
    }

}
