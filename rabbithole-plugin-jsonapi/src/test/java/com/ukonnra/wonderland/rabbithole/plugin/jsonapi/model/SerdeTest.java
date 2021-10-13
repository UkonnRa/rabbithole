package com.ukonnra.wonderland.rabbithole.plugin.jsonapi.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SerdeTest {
  private final ObjectMapper mapper = new ObjectMapper();

  private <T> void doTest(final T obj, final TypeReference<T> type) throws JsonProcessingException {
    var jsonStr = mapper.writeValueAsString(obj);
    var newObj = mapper.readValue(jsonStr, type);
    Assertions.assertEquals(obj, newObj);
  }

  @Test
  public void testSerdeSealedInterface() throws JsonProcessingException {
    doTest(
        List.of(new Link.Obj("href:test", Map.of("meta-key", 1)), new Link.Str("link://str")),
        new TypeReference<List<Link>>() {});

    var linkages =
        List.of(
            new ResourceLinkage.Obj(new ResourceIdentifier("id", "type")),
            new ResourceLinkage.Arr(
                new ResourceIdentifier("id1", "type1"), new ResourceIdentifier("id2", "type2")));
    doTest(linkages, new TypeReference<>() {});

    doTest(
        List.of(
            new Relationships.Obj(
                new Relationship(
                    linkages.get(0),
                    new Relationship.Links(Link.of("obj-link1"), null),
                    Map.of("meta-key", 1))),
            new Relationships.Arr(
                new Relationship(
                    linkages.get(0),
                    new Relationship.Links(Link.of("arr-link1-1"), null),
                    Map.of("meta-key", 2)),
                new Relationship(
                    linkages.get(1),
                    new Relationship.Links(Link.of("relat-link2-1"), null),
                    Map.of("meta-key", false)))),
        new TypeReference<>() {});
  }

  @Test
  public void testSerdeNullHref() {
    Assertions.assertThrows(
        MismatchedInputException.class,
        () ->
            mapper.readValue(
                mapper.writeValueAsString(new Link.Obj(null, Map.of("meta-key", 1))), Link.class));
  }
}
