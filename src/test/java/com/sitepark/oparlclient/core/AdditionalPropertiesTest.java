package com.sitepark.oparlclient.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sitepark.oparlclient.OparlClient;
import com.sitepark.oparlclient.v1.objects.OparlBody;
import com.sitepark.oparlclient.v1.objects.OparlPerson;
import java.util.List;
import org.junit.jupiter.api.Test;

class AdditionalPropertiesTest {

  private final OparlClient client = new OparlClient();

  private static final String PERSON =
      "{\"id\":\"https://oparl.example.org/person/1\","
          + "\"name\":\"Prof. Dr. Max Muster\","
          + "\"BeispielHersteller:faxNumber\":\"012345678\","
          + "\"BeispielHersteller:office\":{\"room\":\"2.13\",\"floor\":2}}";

  private OparlPerson person() throws Exception {
    return this.client.deserializeJson(PERSON, new TypeReference<OparlPerson>() {});
  }

  @Test
  void keepsVendorSpecificProperties() throws Exception {
    OparlPerson person = this.person();

    assertEquals(
        List.of("BeispielHersteller:faxNumber", "BeispielHersteller:office"),
        List.copyOf(person.getAdditionalProperties().keySet()));
    assertEquals(
        "012345678", person.getAdditionalProperty("BeispielHersteller:faxNumber").asText());
    assertEquals(2, person.getAdditionalProperty("BeispielHersteller:office").get("floor").asInt());
  }

  @Test
  void doesNotContainMappedProperties() throws Exception {
    OparlPerson person = this.person();

    assertEquals("Prof. Dr. Max Muster", person.getName());
    assertNull(person.getAdditionalProperty("name"));
    assertNull(person.getAdditionalProperty("id"));
  }

  @Test
  void isEmptyWithoutAdditionalProperties() throws Exception {
    OparlBody body =
        this.client.deserializeJson("{\"name\":\"Stadt\"}", new TypeReference<OparlBody>() {});

    assertTrue(body.getAdditionalProperties().isEmpty());
    assertNull(body.getAdditionalProperty("BeispielHersteller:faxNumber"));
  }

  @Test
  void isUnmodifiable() throws Exception {
    OparlPerson person = this.person();

    assertThrows(
        UnsupportedOperationException.class,
        () -> person.getAdditionalProperties().put("x", TextNode.valueOf("y")));
  }

  @Test
  void writesAdditionalPropertiesBack() throws Exception {
    String json = new ObjectMapper().writeValueAsString(this.person());

    assertTrue(json.contains("\"BeispielHersteller:faxNumber\":\"012345678\""), json);
    assertTrue(
        json.contains("\"BeispielHersteller:office\":{\"room\":\"2.13\",\"floor\":2}"), json);
    assertTrue(!json.contains("additionalProperties"), json);
  }

  @Test
  void keepsAdditionalPropertiesOfListPages() throws Exception {
    OparlList<OparlBody> page =
        this.client.deserializeJson(
            "{\"data\":[],\"pagination\":{\"BeispielHersteller:cursor\":\"abc\"},"
                + "\"links\":{},\"BeispielHersteller:generated\":\"2024-01-01\"}",
            new TypeReference<OparlList<OparlBody>>() {});

    assertEquals("2024-01-01", page.getAdditionalProperty("BeispielHersteller:generated").asText());
    assertEquals(
        "abc", page.getPagination().getAdditionalProperty("BeispielHersteller:cursor").asText());
  }
}
