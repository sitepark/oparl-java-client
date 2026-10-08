package com.sitepark.oparlclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import com.sitepark.oparlclient.core.OparlTimeoutException;
import com.sitepark.oparlclient.v1.objects.OparlBody;
import java.time.Duration;
import java.util.concurrent.CompletionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OparlClientTimeoutTest {

  private MockOparlServer server;

  @BeforeEach
  void startServer() throws Exception {
    this.server = new MockOparlServer();
    this.server.respondJsonDelayed(
        "/slow/body", Duration.ofMillis(1500), "{\"name\":\"Stadt Beispiel\"}");
  }

  @AfterEach
  void stopServer() {
    this.server.close();
  }

  @Test
  void usesDefaultTimeout() {
    assertEquals(OparlClient.DEFAULT_REQUEST_TIMEOUT, new OparlClient().getRequestTimeout());
    assertEquals(
        OparlClient.DEFAULT_REQUEST_TIMEOUT,
        OparlClient.builder().requestTimeout(null).build().getRequestTimeout());
  }

  @Test
  void failsIfRequestTakesLongerThanTimeout() {
    OparlClient client = OparlClient.builder().requestTimeout(Duration.ofMillis(200)).build();

    CompletionException e =
        assertThrows(
            CompletionException.class,
            () -> client.getAsync(this.server.uri("/slow/body"), OparlBody.class).join());

    assertInstanceOf(OparlTimeoutException.class, e.getCause());
  }

  @Test
  void failsIfBodyStallsLongerThanTimeout() {
    this.server.respondStalled("/stalled/body", Duration.ofSeconds(2));
    OparlClient client = OparlClient.builder().requestTimeout(Duration.ofMillis(300)).build();

    CompletionException e =
        assertTimeoutPreemptively(
            Duration.ofSeconds(3),
            () ->
                assertThrows(
                    CompletionException.class,
                    () ->
                        client.getAsync(this.server.uri("/stalled/body"), OparlBody.class).join()));

    assertInstanceOf(OparlTimeoutException.class, e.getCause());
  }

  @Test
  void succeedsWithinTimeout() {
    OparlClient client = OparlClient.builder().requestTimeout(Duration.ofSeconds(5)).build();

    OparlBody body = client.getAsync(this.server.uri("/slow/body"), OparlBody.class).join();

    assertEquals("Stadt Beispiel", body.getName());
  }

  @Test
  void rejectsNonPositiveTimeout() {
    assertThrows(
        IllegalArgumentException.class, () -> OparlClient.builder().requestTimeout(Duration.ZERO));
    assertThrows(
        IllegalArgumentException.class,
        () -> OparlClient.builder().requestTimeout(Duration.ofSeconds(-1)));
  }
}
