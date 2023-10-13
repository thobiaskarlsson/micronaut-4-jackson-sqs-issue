package com.example;

import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class Service {

  private final Logger log = LoggerFactory.getLogger(getClass());

  public void consume(Object object) {
    log.info("Consumed object: {}", object);
  }
}
