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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exchange")
public class ExchangeController {

    private final ExchangeService exchangeService;

    @GetMapping("/rate")
    public ApiResponse<ExchangeResponse> getRate(@RequestParam String from,
                                                  @RequestParam String to,
                                                  @RequestParam double amount) {
        return ApiResponse.success(exchangeService.getExchangeRate(from, to, amount));
    }

    @PostMapping("/budget")
    public ApiResponse<ExchangeResponse> calculateBudget(@Valid @RequestBody BudgetCalculateRequest request) {
        return ApiResponse.success(exchangeService.calculateBudget(request));
    }

}
