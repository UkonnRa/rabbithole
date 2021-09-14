package com.ukonnra.wonderland.rabbithole.core.schema;

import edu.umd.cs.findbugs.annotations.Nullable;
import java.util.List;

public record FilterSchema(String name, List<Data> data) {
  public record Data(@Nullable List<Operator> operators, Type type) {
    enum Operator {
      EQ,
      NE,
      GT,
      GTE,
      LT,
      LTE,
      ANY,
      ALL,
      IN
    }

    public sealed interface Type {
      record Primary() implements Type {}

      record Object() implements Type {}

      record Array(Type type) implements Type {}
    }

    public Data(Type type) {
      this(null, type);
    }

    public static final List<Data> FILTER_SCHEMA_PRIMARY =
        List.of(
            new Data(new Type.Primary()),
            new Data(
                List.of(
                    Operator.EQ, Operator.NE, Operator.GT, Operator.GTE, Operator.LT, Operator.LTE),
                new Type.Primary()),
            new Data(List.of(Operator.IN), new Type.Array(new Type.Primary())));

    public static final List<Data> FILTER_SCHEMA_ARRAY_PRIMARY =
        List.of(
            new Data(new Type.Array(new Type.Primary())),
            new Data(
                List.of(Operator.EQ, Operator.NE, Operator.ANY, Operator.ALL),
                new Type.Array(new Type.Primary())),
            new Data(List.of(Operator.EQ, Operator.NE), new Type.Primary()));

    public static final List<Data> FILTER_SCHEMA_ARRAY_OBJECT =
        List.of(
            new Data(new Type.Array(new Type.Object())),
            new Data(
                List.of(Operator.EQ, Operator.NE, Operator.ANY, Operator.ALL),
                new Type.Array(new Type.Object())),
            new Data(List.of(Operator.EQ, Operator.NE), new Type.Object()));

    public static final List<Data> FILTER_SCHEMA_MAP =
        List.of(
            new Data(new Type.Object()),
            new Data(
                List.of(Operator.EQ, Operator.NE, Operator.ANY, Operator.ALL),
                new Type.Array(new Type.Object())));

    public static final List<Data> FILTER_SCHEMA_OBJECT =
        List.of(
            new Data(new Type.Object()),
            new Data(List.of(Operator.EQ, Operator.NE), new Type.Array(new Type.Object())));
  }
}
