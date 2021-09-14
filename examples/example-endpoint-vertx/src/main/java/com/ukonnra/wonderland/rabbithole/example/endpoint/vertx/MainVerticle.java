package com.ukonnra.wonderland.rabbithole.example.endpoint.vertx;

import com.ukonnra.wonderland.rabbithole.example.core.domains.user.User;
import io.vertx.core.AbstractVerticle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainVerticle extends AbstractVerticle {
  private static final Logger LOGGER = LogManager.getLogger(MainVerticle.class);

  private void exec(String command) throws IOException {
    Process p = Runtime.getRuntime().exec(command);
    BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
    String s;
    while ((s = stdInput.readLine()) != null) {
      LOGGER.info(s);
    }
  }

  @Override
  public void start() throws IOException {
    LOGGER.info("Start example vertx");
    var user = new User("name", "password");
    LOGGER.info("Start user: {}", user);

    exec("id");
    exec("whoami");
    exec("ls -lan");
  }
}
