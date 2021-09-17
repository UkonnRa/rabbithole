/** */
module rabbithole.example.core {
  requires org.apache.logging.log4j;
  requires com.github.spotbugs.annotations;
  requires rabbithole.core;
  requires rabbithole.jsonapi;

  exports com.ukonnra.wonderland.rabbithole.example.core;
  exports com.ukonnra.wonderland.rabbithole.example.core.domains.article;
  exports com.ukonnra.wonderland.rabbithole.example.core.domains.user;
  exports com.ukonnra.wonderland.rabbithole.example.core.domains.user.valobjs;
}
