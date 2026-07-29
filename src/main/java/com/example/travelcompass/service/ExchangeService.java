package com.example.travelcompass.service;

import com.example.travelcompass.client.FrankfurterClient;
import com.example.travelcompass.dto.request.BudgetCalculateRequest;
import com.example.travelcompass.dto.response.ExchangeResponse;
import com.example.travelcompass.dto.response.FrankfurterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class ExchangeService {

    private final FrankfurterClient frankfurterClient;

    public ExchangeResponse getExchangeRate(String from, String to, double amount) {
        if (from.equalsIgnoreCase(to)) {
            return ExchangeResponse.builder()
                    .fromCurrency(from)
                    .toCurrency(to)
                    .amount(amount)
                    .convertedAmount(amount)
                    .rate(1.0)
                    .date(null)
                    .build();
        }

        FrankfurterResponse response = frankfurterClient.getLatestRate(from, to)
                .block(Duration.ofSeconds(5));

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

    public ExchangeResponse calculateBudget(BudgetCalculateRequest request) {
        long totalKrw = (long) request.getDays() * request.getCostPerNightKrw();
        return getExchangeRate("KRW", request.getTargetCurrency(), totalKrw);
    }

}
