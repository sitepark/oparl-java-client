package com.sitepark.oparlclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitepark.oparlclient.core.OparlList;
import com.sitepark.oparlclient.v1.objects.OparlBody;
import com.sitepark.oparlclient.v1.objects.OparlConsultation;
import com.sitepark.oparlclient.v1.objects.OparlLocation;
import java.net.URI;
import org.junit.jupiter.api.Test;

class SerializationRoundTripTest {

  private final OparlClient client = new OparlClient();

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void writesReferenceAsUrl() throws Exception {
    OparlConsultation consultation =
        this.client.deserializeJson(
            "{\"meeting\":\"https://oparl.example.org/meeting/1\"}",
            new TypeReference<OparlConsultation>() {});

    String json = this.mapper.writeValueAsString(consultation);

    assertTrue(json.contains("\"meeting\":\"https://oparl.example.org/meeting/1\""), json);
  }

  @Test
  void roundTripsObjectWithReferences() throws Exception {
    String original =
        "{\"id\":\"https://oparl.example.org/body/1\","
            + "\"type\":\"https://schema.oparl.org/1.1/Body\","
            + "\"name\":\"Stadt Beispiel\","
            + "\"system\":\"https://oparl.example.org/\","
            + "\"organization\":\"https://oparl.example.org/body/1/organization\","
            + "\"location\":{\"id\":\"https://oparl.example.org/location/1\","
            + "\"bodies\":[\"https://oparl.example.org/body/1\"]},"
            + "\"equivalent\":[\"https://www.wikidata.org/wiki/Q1\"]}";
    OparlBody body = this.client.deserializeJson(original, new TypeReference<OparlBody>() {});

    OparlBody copy =
        this.client.deserializeJson(
            this.mapper.writeValueAsString(body), new TypeReference<OparlBody>() {});

    assertEquals(body.getId(), copy.getId());
    assertEquals(body.getName(), copy.getName());
    assertEquals(body.getSystem().getUri(), copy.getSystem().getUri());
    assertEquals(body.getOrganization().getUri(), copy.getOrganization().getUri());
    assertEquals(body.getEquivalent(), copy.getEquivalent());
    OparlLocation location = copy.getLocation();
    assertEquals(URI.create("https://oparl.example.org/location/1"), location.getId());
    assertEquals(
        URI.create("https://oparl.example.org/body/1"), location.getBodies().get(0).getUri());
  }

  @Test
  void roundTripsListPage() throws Exception {
    String original =
        "{\"data\":[{\"name\":\"a\"}],\"pagination\":{\"totalElements\":2},"
            + "\"links\":{\"next\":\"https://oparl.example.org/bodies?page=2\",\"web\":\"https://ris.example.org/gremien\"}}";
    OparlList<OparlBody> page =
        this.client.deserializeJson(original, new TypeReference<OparlList<OparlBody>>() {});

    OparlList<OparlBody> copy =
        this.client.deserializeJson(
            this.mapper.writeValueAsString(page), new TypeReference<OparlList<OparlBody>>() {});

    assertEquals("a", copy.getData().get(0).getName());
    assertEquals(2, copy.getPagination().getTotalElements());
    assertEquals(
        URI.create("https://oparl.example.org/bodies?page=2"), copy.getLinks().getNext().getUri());
    assertEquals(URI.create("https://ris.example.org/gremien"), copy.getLinks().getWeb());
  }
}
