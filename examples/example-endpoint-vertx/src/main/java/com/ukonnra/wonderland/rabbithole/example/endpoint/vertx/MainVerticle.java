package com.ukonnra.wonderland.rabbithole.example.endpoint.vertx;

import io.vertx.core.AbstractVerticle;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainVerticle extends AbstractVerticle {
  private static final Logger LOGGER = LogManager.getLogger(MainVerticle.class);

  @Override
  public void start() throws IOException {
    LOGGER.info("Start example vertx");

    try (var ins = Objects.requireNonNull(this.getClass().getResourceAsStream("/log4j2.xml"))) {
      var log4j2 = new String(ins.readAllBytes(), StandardCharsets.UTF_8);
      LOGGER.info("log4j2 file: {}", log4j2);
    }
  }
}
