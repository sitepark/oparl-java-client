package com.sitepark.oparlclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sitepark.oparlclient.core.OparlReference;
import com.sitepark.oparlclient.v1.objects.OparlObjectV1;
import com.sitepark.oparlclient.v1.objects.OparlPaper;
import com.sitepark.oparlclient.v1.objects.OparlPerson;
import java.net.URI;
import java.util.Set;
import org.junit.jupiter.api.Test;

class EqualityTest {

  private final OparlClient client = new OparlClient();

  @Test
  void objectsWithSameIdAreEqual() throws Exception {
    OparlPaper before = this.paper("{\"id\":\"https://oparl.example.org/paper/1\",\"name\":\"A\"}");
    OparlPaper after =
        this.paper(
            "{\"id\":\"https://oparl.example.org/paper/1\",\"name\":\"B\","
                + "\"modified\":\"2024-01-02T03:04:05+01:00\"}");

    assertEquals(before, after);
    assertEquals(before.hashCode(), after.hashCode());
    assertEquals(1, Set.of(before).size());
  }

  @Test
  void objectsWithDifferentIdAreNotEqual() throws Exception {
    assertNotEquals(
        this.paper("{\"id\":\"https://oparl.example.org/paper/1\"}"),
        this.paper("{\"id\":\"https://oparl.example.org/paper/2\"}"));
  }

  @Test
  void objectsOfDifferentClassesAreNotEqual() throws Exception {
    String json = "{\"id\":\"https://oparl.example.org/1\"}";

    assertNotEquals(
        this.paper(json), this.client.deserializeJson(json, new TypeReference<OparlPerson>() {}));
    assertNotEquals(
        this.paper(json), this.client.deserializeJson(json, new TypeReference<OparlObjectV1>() {}));
  }

  @Test
  void objectWithoutIdIsOnlyEqualToItself() throws Exception {
    OparlPaper paper = this.paper("{\"name\":\"A\"}");

    assertEquals(paper, paper);
    assertNotEquals(paper, this.paper("{\"name\":\"A\"}"));
  }

  @Test
  void referencesWithSameUrlAreEqual() {
    OparlReference<OparlPaper> a =
        new OparlReference<>(URI.create("https://oparl.example.org/paper/1"), null, null);
    OparlReference<OparlPaper> b =
        new OparlReference<>(URI.create("https://oparl.example.org/paper/1"), null, this.client);

    assertEquals(a, b);
    assertEquals(a.hashCode(), b.hashCode());
    assertNotEquals(
        a, new OparlReference<>(URI.create("https://oparl.example.org/paper/2"), null, null));
  }

  private OparlPaper paper(String json) throws Exception {
    return this.client.deserializeJson(json, new TypeReference<OparlPaper>() {});
  }
}
