/** */
module rabbithole.example.core {
  requires rabbithole.plugin.jsonapi;
  requires rabbithole.processor;
  requires rabbithole.core;
  requires org.apache.logging.log4j;
  requires com.github.spotbugs.annotations;

  exports com.ukonnra.wonderland.rabbithole.example.core.domains.article;
  exports com.ukonnra.wonderland.rabbithole.example.core.domains.user;
  exports com.ukonnra.wonderland.rabbithole.example.core.domains.user.valobjs;
}
