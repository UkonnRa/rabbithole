package com.ukonnra.wonderland.rabbithole.examples.vertx;

import com.ukonnra.wonderland.rabbithole.core.AbstractPreBuild;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class PreBuild extends AbstractPreBuild {
  private static final Logger LOGGER = LogManager.getLogger(PreBuild.class);

  public static void main(String[] args) throws IOException {
    var preBuild = new PreBuild();
    preBuild.handle();
  }

  @Override
  public Logger logger() {
    return LOGGER;
  }
}
