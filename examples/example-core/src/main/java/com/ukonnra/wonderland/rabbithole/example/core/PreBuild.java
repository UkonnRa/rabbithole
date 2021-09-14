package com.ukonnra.wonderland.rabbithole.example.core;

import com.ukonnra.wonderland.rabbithole.core.AbstractPreBuild;
import com.ukonnra.wonderland.rabbithole.core.PluginBundle;
import com.ukonnra.wonderland.rabbithole.example.core.domains.article.Article;
import com.ukonnra.wonderland.rabbithole.example.core.domains.user.User;
import java.io.IOException;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PreBuild extends AbstractPreBuild {
  private static final Logger LOGGER = LogManager.getLogger(PreBuild.class);

  protected PreBuild() {
    super(List.of(Article.class, User.class), new PluginBundle(List.of()));
  }

  public static void main(String[] args) throws IOException {
    new PreBuild().handle();
  }

  @Override
  public Logger logger() {
    return LOGGER;
  }
}
