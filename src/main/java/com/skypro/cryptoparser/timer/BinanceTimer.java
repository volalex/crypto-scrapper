package com.skypro.cryptoparser.timer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skypro.cryptoparser.client.BinanceApiClient;
import com.skypro.cryptoparser.model.PriceMessage;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BinanceTimer {
  private static final Logger LOG = LoggerFactory.getLogger(BinanceTimer.class);
  private final BinanceApiClient binanceApiClient;
  private final List<String> symbols;
  private final RabbitTemplate rabbitTemplate;
  private final ObjectMapper objectMapper;

  public BinanceTimer(
      @Value("${application.symbols}") List<String> symbols,
      BinanceApiClient binanceApiClient,
      RabbitTemplate rabbitTemplate,
      ObjectMapper objectMapper) {
    this.binanceApiClient = binanceApiClient;
    this.symbols = symbols;
    this.rabbitTemplate = rabbitTemplate;
    this.objectMapper = objectMapper;
  }

  @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
  public void readPrices() {
    Instant instant = Instant.now();
    this.binanceApiClient.getPrice(symbols).stream()
        .map(price -> new PriceMessage(price.symbol(), Double.parseDouble(price.price()), instant))
        .forEach(
            message -> {
              try {
                rabbitTemplate.convertAndSend(
                    "price-message", "price", objectMapper.writer().writeValueAsString(message));
              } catch (JsonProcessingException e) {
                LOG.error("Failed to convert message to json", e);
              }
            });
  }
}
