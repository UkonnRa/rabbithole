package com.ukonnra.wonderland.rabbithole.example.endpoint.vertx;

import io.vertx.core.AbstractVerticle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainVerticle extends AbstractVerticle {
  private static final Logger LOGGER = LogManager.getLogger(MainVerticle.class);

  @Override
  public void start() throws Exception {
    LOGGER.info("Start example vertx");
  }
}
