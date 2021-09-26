/** */
module rabbithole.core {
  requires org.apache.logging.log4j;
  requires com.github.spotbugs.annotations;
  requires java.compiler;
  requires com.google.auto.service;

  exports com.ukonnra.wonderland.rabbithole.core;
  exports com.ukonnra.wonderland.rabbithole.core.annotation;
  exports com.ukonnra.wonderland.rabbithole.core.facade;
}
