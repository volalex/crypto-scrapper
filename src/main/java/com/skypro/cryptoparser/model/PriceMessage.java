package com.skypro.cryptoparser.model;

import java.time.Instant;

public record PriceMessage(String symbol, double price, Instant instant) {}
