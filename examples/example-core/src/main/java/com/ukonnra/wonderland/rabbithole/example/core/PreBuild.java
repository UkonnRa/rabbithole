package com.ukonnra.wonderland.rabbithole.example.core;

import com.ukonnra.wonderland.rabbithole.core.AbstractPreBuild;
import com.ukonnra.wonderland.rabbithole.jsonapi.JsonapiPreBuildMixin;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class PreBuild extends AbstractPreBuild<PreBuild> implements JsonapiPreBuildMixin {
  private static final Logger LOGGER = LogManager.getLogger(PreBuild.class);

  protected PreBuild() {
    super(PreBuild.class);
  }

  public static void main(String[] args) throws IOException {
    var preBuild = new PreBuild();
    preBuild.handle();
  }

  @Override
  public @NotNull Logger logger() {
    return LOGGER;
  }
}
