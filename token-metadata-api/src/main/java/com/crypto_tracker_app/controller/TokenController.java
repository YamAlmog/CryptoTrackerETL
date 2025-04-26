package com.crypto_tracker_app.controller;

import com.crypto_tracker_app.Coin;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.crypto_tracker_app.service.TokenService;

@RestController
@RequestMapping("/tokens")
public class TokenController {

    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }


    @GetMapping("/all_symbols")
    public List<String> getAllTokensSymbols() {
        System.out.println("---------> Get list of tokens symbols");
        return tokenService.getAllTokenSymbols();
    }

    @GetMapping("/all_ids")
    public List<String> getAllTokensIds() {
        System.out.println("---------> Get list of tokens ids");
        return tokenService.getAllTokenIds();
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

}