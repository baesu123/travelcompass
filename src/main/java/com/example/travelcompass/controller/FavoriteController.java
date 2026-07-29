package com.example.travelcompass.controller;

import com.example.travelcompass.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

}
