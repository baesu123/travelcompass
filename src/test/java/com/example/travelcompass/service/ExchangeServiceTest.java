package com.example.travelcompass.service;

import com.example.travelcompass.client.FrankfurterClient;
import com.example.travelcompass.dto.request.BudgetCalculateRequest;
import com.example.travelcompass.dto.response.ExchangeResponse;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

class ExchangeServiceTest {

    private final ExchangeService exchangeService = new ExchangeService(new FrankfurterClient(WebClient.builder()));

    @Test
    void KRW를_외화로_변환한다() {
        ExchangeResponse response = exchangeService.getExchangeRate("KRW", "USD", 10000);

        assertThat(response.getFromCurrency()).isEqualTo("KRW");
        assertThat(response.getToCurrency()).isEqualTo("USD");
        assertThat(response.getConvertedAmount()).isEqualTo(10000 * response.getRate());
    }

    @Test
    void 외화를_KRW로_변환한다() {
        ExchangeResponse response = exchangeService.getExchangeRate("USD", "KRW", 100);

        assertThat(response.getConvertedAmount()).isEqualTo(100 * response.getRate());
        assertThat(response.getRate()).isGreaterThan(1);
    }

    @Test
    void 외화를_외화로_변환한다() {
        ExchangeResponse response = exchangeService.getExchangeRate("USD", "JPY", 50);

        assertThat(response.getFromCurrency()).isEqualTo("USD");
        assertThat(response.getToCurrency()).isEqualTo("JPY");
        assertThat(response.getConvertedAmount()).isEqualTo(50 * response.getRate());
    }

    @Test
    void 동일_통화는_API_호출_없이_1대1로_변환한다() {
        ExchangeResponse response = exchangeService.getExchangeRate("KRW", "KRW", 5000);

        assertThat(response.getRate()).isEqualTo(1.0);
        assertThat(response.getConvertedAmount()).isEqualTo(5000);
    }

    @Test
    void 여행_예산을_현지_화폐로_계산한다() {
        BudgetCalculateRequest request = new BudgetCalculateRequest();
        request.setDays(7);
        request.setCostPerNightKrw(120000);
        request.setTargetCurrency("JPY");

        ExchangeResponse response = exchangeService.calculateBudget(request);

        assertThat(response.getFromCurrency()).isEqualTo("KRW");
        assertThat(response.getToCurrency()).isEqualTo("JPY");
        assertThat(response.getAmount()).isEqualTo(7 * 120000);
        assertThat(response.getConvertedAmount()).isEqualTo(response.getAmount() * response.getRate());
    }

}
