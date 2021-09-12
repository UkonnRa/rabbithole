package com.ukonnra.wonderland.rabbithole.core.filter;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public record Filter(@Nullable List<Operator> operators, Type type) {
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
    record Symbol() implements Type {}

    record Primary() implements Type {}

    record Object() implements Type {}

    record Array(Type type) implements Type {}
  }

  public Filter(Type type) {
    this(null, type);
  }

  public static final List<Filter> FILTER_SYMBOL =
      List.of(
          new Filter(new Type.Symbol()),
          new Filter(List.of(Operator.EQ, Operator.NE), new Type.Symbol()),
          new Filter(List.of(Operator.IN), new Type.Array(new Type.Symbol())));

  public static final List<Filter> FILTER_PRIMARY =
      List.of(
          new Filter(new Type.Primary()),
          new Filter(
              List.of(
                  Operator.EQ, Operator.NE, Operator.GT, Operator.GTE, Operator.LT, Operator.LTE),
              new Type.Primary()),
          new Filter(List.of(Operator.IN), new Type.Array(new Type.Primary())));

  public static final List<Filter> FILTER_ARRAY_PRIMARY =
      List.of(
          new Filter(new Type.Array(new Type.Primary())),
          new Filter(
              List.of(Operator.EQ, Operator.NE, Operator.ANY, Operator.ALL),
              new Type.Array(new Type.Primary())),
          new Filter(List.of(Operator.EQ, Operator.NE), new Type.Primary()));

  public static final List<Filter> FILTER_ARRAY_OBJECT =
      List.of(
          new Filter(new Type.Array(new Type.Object())),
          new Filter(
              List.of(Operator.EQ, Operator.NE, Operator.ANY, Operator.ALL),
              new Type.Array(new Type.Object())),
          new Filter(List.of(Operator.EQ, Operator.NE), new Type.Object()));

  public static final List<Filter> FILTER_MAP =
      List.of(
          new Filter(new Type.Object()),
          new Filter(
              List.of(Operator.EQ, Operator.NE, Operator.ANY, Operator.ALL),
              new Type.Array(new Type.Object())));

  public static final List<Filter> FILTER_OBJECT =
      List.of(
          new Filter(new Type.Object()),
          new Filter(List.of(Operator.EQ, Operator.NE), new Type.Array(new Type.Object())));
}
