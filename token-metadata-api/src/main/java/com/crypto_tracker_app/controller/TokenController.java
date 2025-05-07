package com.crypto_tracker_app.controller;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crypto_tracker_app.Coin;
import com.crypto_tracker_app.service.TokenService;

@RestController
@RequestMapping("/tokens")
public class TokenController {

    private static final Logger logger = Logger.getLogger(TokenController.class.getName());
    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }


    @GetMapping("/all_symbols")
    public List<String> getAllTokensSymbols() {
        logger.info("---------> Get list of tokens symbols");
        return tokenService.getAllTokenSymbols();
    }

    @GetMapping("/all_ids")
    public List<String> getAllTokensIds() {
        logger.info("---------> Get list of tokens ids");
        return tokenService.getAllTokenIds();
    }

    @GetMapping("/latest/{symbol}")
    public Coin getLatestRecordBySymbol(@PathVariable String symbol) {
        logger.log(Level.INFO, "---------> Get latest record of token: ", symbol);
        return tokenService.getLatestTokenRecordBySymbol(symbol);
    }

    @GetMapping("/highest/{symbol}")
    public Coin getHighestPriceBySymbol(@PathVariable String symbol) {
        logger.log(Level.INFO, "---------> Get highest price record of token: ", symbol);
        return tokenService.getHighestTokenPriceBySymbol(symbol);
    }

}