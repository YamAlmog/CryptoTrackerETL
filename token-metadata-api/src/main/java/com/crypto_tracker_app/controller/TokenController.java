package com.crypto_tracker_app.controller;

import com.crypto_tracker_app.Coin;

import org.springframework.web.bind.annotation.*;

import com.crypto_tracker_app.service.TokenService;

@RestController
@RequestMapping("/tokens")
public class TokenController {

    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @GetMapping("/latest/{symbol}")
    public Coin getLatestPriceBySymbol(@PathVariable String symbol) {
        System.out.println("---------> Get latest price of token: " + symbol);
        return tokenService.getLatestTokenPriceBySymbol(symbol);
    }

    @GetMapping("/highest/{symbol}")
    public Coin getHighestPriceBySymbol(@PathVariable String symbol) {
        System.out.println("---------> Get highest price of token: " + symbol);
        return tokenService.getHighestTokenPriceBySymbol(symbol);
    }

    @GetMapping("/test/{word}")
    public String test_endpoint(@PathVariable String word) {
        System.out.println("---------> Word: " + word);
        return word;
    }
}