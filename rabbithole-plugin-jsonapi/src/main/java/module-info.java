/** */
module rabbithole.plugin.jsonapi {
  requires rabbithole.core;
  requires com.github.spotbugs.annotations;
  requires java.compiler;

  exports com.ukonnra.wonderland.rabbithole.plugin.jsonapi;
  exports com.ukonnra.wonderland.rabbithole.plugin.jsonapi.annotation;
  exports com.ukonnra.wonderland.rabbithole.plugin.jsonapi.schema;
}
