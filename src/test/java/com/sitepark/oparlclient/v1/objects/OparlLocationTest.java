package com.sitepark.oparlclient.v1.objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitepark.oparlclient.OparlClient;
import org.junit.jupiter.api.Test;

class OparlLocationTest {

  private static final String LOCATION =
      "{\"geojson\":{\"type\":\"Feature\","
          + "\"geometry\":{\"type\":\"Point\",\"coordinates\":[7.0982,50.7374]},"
          + "\"properties\":{\"name\":\"Rathaus\"}}}";

  @Test
  void readsGeojsonAsJsonTree() throws Exception {
    OparlLocation location =
        new OparlClient().deserializeJson(LOCATION, new TypeReference<OparlLocation>() {});

    assertEquals("Feature", location.getGeojson().get("type").asText());
    assertEquals(7.0982, location.getGeojson().at("/geometry/coordinates/0").asDouble());
  }

  @Test
  void writesGeojsonUnchanged() throws Exception {
    OparlLocation location =
        new OparlClient().deserializeJson(LOCATION, new TypeReference<OparlLocation>() {});

    String json = new ObjectMapper().writeValueAsString(location);

    assertTrue(
        json.contains("\"geometry\":{\"type\":\"Point\",\"coordinates\":[7.0982,50.7374]}"), json);
  }
}
