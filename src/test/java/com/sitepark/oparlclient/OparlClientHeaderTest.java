package com.sitepark.oparlclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sitepark.oparlclient.v1.objects.OparlBody;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OparlClientHeaderTest {

  private MockOparlServer server;

  @BeforeEach
  void startServer() throws Exception {
    this.server = new MockOparlServer();
    this.server.respondJson("/body/1", 200, "{\"name\":\"Stadt Beispiel\"}");
  }

  @AfterEach
  void stopServer() {
    this.server.close();
  }

  @Test
  void sendsAcceptHeader() {
    new OparlClient().resolve(this.server.uri("/body/1"), OparlBody.class).join();

    assertEquals("application/json", this.server.lastRequestHeader("Accept"));
  }

  @Test
  void sendsDefaultUserAgentWithVersion() {
    new OparlClient().resolve(this.server.uri("/body/1"), OparlBody.class).join();

    String userAgent = this.server.lastRequestHeader("User-Agent");
    assertEquals(OparlClient.DEFAULT_USER_AGENT, userAgent);
    assertTrue(userAgent.matches("oparl-java-client/\\d+\\.\\d+\\.\\d+.*"), userAgent);
  }

  @Test
  void sendsConfiguredUserAgent() {
    OparlClient client =
        OparlClient.builder().userAgent("my-app/1.0 (+https://example.org/contact)").build();

    client.resolve(this.server.uri("/body/1"), OparlBody.class).join();

    assertEquals(
        "my-app/1.0 (+https://example.org/contact)", this.server.lastRequestHeader("User-Agent"));
  }

  @Test
  void rejectsInvalidUserAgent() {
    assertThrows(IllegalArgumentException.class, () -> OparlClient.builder().userAgent(" "));
    assertThrows(
        IllegalArgumentException.class, () -> OparlClient.builder().userAgent("my-app\r\nX: y"));
  }
}
