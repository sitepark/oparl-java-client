package com.sitepark.oparlclient;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.sitepark.oparlclient.v1.objects.OparlConsultation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * With {@code resolveAny} the response is first read as JSON tree and mapped afterwards; references
 * must still be resolvable.
 */
class ReferencesAfterTreeMappingTest {

  private MockOparlServer server;

  @BeforeEach
  void startServer() throws Exception {
    this.server = new MockOparlServer();
    this.server.respondJson(
        "/consultation/1",
        200,
        "{\"type\":\"https://schema.oparl.org/1.1/Consultation\",\"meeting\":\""
            + this.server.uri("/meeting/1")
            + "\"}");
    this.server.respondJson(
        "/meeting/1",
        200,
        "{\"type\":\"https://schema.oparl.org/1.1/Meeting\",\"name\":\"Ratssitzung\"}");
  }

  @AfterEach
  void stopServer() {
    this.server.close();
  }

  @Test
  void resolvesReferencesOfObjectsResolvedWithResolveAny() {
    OparlConsultation consultation =
        (OparlConsultation) new OparlClient().resolveAny(this.server.uri("/consultation/1")).join();

    assertEquals("Ratssitzung", consultation.getMeeting().get().getName());
  }
}
