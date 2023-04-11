package com.skypro.cryptoparser.client;

import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class BinanceApiClient {
  private static final Logger LOG = LoggerFactory.getLogger(BinanceApiClient.class);
  private final RestTemplate restTemplate;

  public BinanceApiClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public List<PriceResponse> getPrice(List<String> symbols) {
    try {
      String argument = symbols.stream().map(s -> "\"" + s + "\"").collect(Collectors.joining(","));
      argument = "[" + argument + "]";
      var response =
          restTemplate.getForObject("/ticker/price?symbols=" + argument, PriceResponse[].class);
      if (response == null) {
        return List.of();
      }
      return List.of(response);
    } catch (HttpClientErrorException e) {
      LOG.error("Error in Binance API request", e);
      return List.of();
    }
  }
}
