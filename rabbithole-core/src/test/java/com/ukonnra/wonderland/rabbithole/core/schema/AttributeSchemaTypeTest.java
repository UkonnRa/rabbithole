package com.ukonnra.wonderland.rabbithole.core.schema;

import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import com.ukonnra.wonderland.rabbithole.core.AnnotationProcessor;
import javax.tools.JavaFileObject;
import org.junit.jupiter.api.Test;

public class AttributeSchemaTypeTest {
  private static final JavaFileObject TEST_AGGREGATE =
      JavaFileObjects.forSourceString(
          "example.aggregate.test.TestAggregate",
          """
    package example.aggregate.test;

    public record TestAggregate() {}
    """);

  @Test
  public void testInit() {
    var result = Compiler.javac().withProcessors(new AnnotationProcessor()).compile(TEST_AGGREGATE);
    System.out.println(result);
  }
}
