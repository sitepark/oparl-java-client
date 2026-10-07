package com.sitepark.oparlclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import com.sitepark.oparlclient.v1.objects.OparlBody;
import com.sitepark.oparlclient.v1.objects.OparlMeeting;
import com.sitepark.oparlclient.v1.objects.OparlObjectV1;
import java.net.URI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OparlClientTypeTest {

  private static final String MEETING =
      "{\"id\":\"https://oparl.example.org/meeting/1\","
          + "\"type\":\"https://schema.oparl.org/1.1/Meeting\",\"name\":\"Ratssitzung\","
          + "\"organization\":[\"https://oparl.example.org/organization/1\"]}";

  private MockOparlServer server;

  @BeforeEach
  void startServer() throws Exception {
    this.server = new MockOparlServer();
    this.server.respondJson("/meeting/1", 200, MEETING);
  }

  @AfterEach
  void stopServer() {
    this.server.close();
  }

  @Test
  void resolvesObjectOfUnknownType() {
    OparlObjectV1 object = new OparlClient().resolveAny(this.server.uri("/meeting/1")).join();

    OparlMeeting meeting = assertInstanceOf(OparlMeeting.class, object);
    assertEquals("Ratssitzung", meeting.getName());
    assertEquals(
        URI.create("https://oparl.example.org/organization/1"),
        meeting.getOrganization().get(0).getUri());
  }

  @Test
  void resolvesVendorSpecificTypeAsGenericObject() {
    this.server.respondJson(
        "/other",
        200,
        "{\"id\":\"https://oparl.example.org/other/1\","
            + "\"type\":\"https://hersteller.example.org/oparl/Ausschussvorlage\","
            + "\"Hersteller:titel\":\"Vorlage 1\"}");

    OparlObjectV1 object = new OparlClient().resolveAny(this.server.uri("/other")).join();

    assertEquals(OparlObjectV1.class, object.getClass());
    assertEquals(URI.create("https://oparl.example.org/other/1"), object.getId());
    assertEquals("https://hersteller.example.org/oparl/Ausschussvorlage", object.getType());
    assertEquals("Vorlage 1", object.getAdditionalProperty("Hersteller:titel").asText());
  }

  @Test
  void resolvesObjectWithoutTypeAsGenericObject() {
    this.server.respondJson("/untyped", 200, "{\"name\":\"x\"}");

    OparlObjectV1 object = new OparlClient().resolveAny(this.server.uri("/untyped")).join();

    assertEquals(OparlObjectV1.class, object.getClass());
    assertEquals("x", object.getAdditionalProperty("name").asText());
  }

  @Test
  void acceptsWrongTypeByDefault() {
    this.server.respondJson(
        "/meeting/2", 200, "{\"type\":\"https://schema.oparl.org/1.1/Meeting\"}");

    OparlBody body =
        new OparlClient().resolve(this.server.uri("/meeting/2"), OparlBody.class).join();

    assertEquals("https://schema.oparl.org/1.1/Meeting", body.getType());
  }
}
