package com.skypro.cryptoparser.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class ListenerComponent {
  private static final Logger LOG = LoggerFactory.getLogger(ListenerComponent.class);

  @RabbitListener(queues = "price-message")
  public void listenMessage(@Payload String message) {
    LOG.info("Got message {}", message);
  }
}
