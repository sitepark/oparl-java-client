package com.sitepark.oparlclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.sitepark.oparlclient.core.OparlException;
import com.sitepark.oparlclient.core.OparlList;
import com.sitepark.oparlclient.core.OparlParseException;
import com.sitepark.oparlclient.v1.objects.OparlBody;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** A response without content must fail, so that it is never mistaken for a valid result. */
class EmptyResponseTest {

  private MockOparlServer server;

  private final OparlClient client = new OparlClient();

  @BeforeEach
  void startServer() throws Exception {
    this.server = new MockOparlServer();
    this.server.respondJson("/null", 200, "null");
    this.server.respond("/empty", 200, "application/json", "");
  }

  @AfterEach
  void stopServer() {
    this.server.close();
  }

  private static Throwable failureOf(CompletableFuture<?> future) {
    return assertThrows(CompletionException.class, future::join).getCause();
  }

  @Test
  void failsForNullBody() {
    Throwable cause = failureOf(this.client.getAsync(this.server.uri("/null"), OparlBody.class));

    assertInstanceOf(OparlException.class, cause);
    assertTrue(cause.getMessage().startsWith("Empty response"), cause.getMessage());
  }

  @Test
  void failsForEmptyBody() {
    OparlParseException e =
        assertInstanceOf(
            OparlParseException.class,
            failureOf(this.client.getAsync(this.server.uri("/empty"), OparlBody.class)));

    assertEquals(this.server.uri("/empty"), e.getUri());
    assertInstanceOf(JsonProcessingException.class, e.getCause());
  }

  @Test
  void resolveAnyFailsForNullBody() {
    assertInstanceOf(
        OparlException.class, failureOf(this.client.getAnyAsync(this.server.uri("/null"))));
  }

  @Test
  void resolveAnyFailsForEmptyBody() {
    assertInstanceOf(
        OparlException.class, failureOf(this.client.getAnyAsync(this.server.uri("/empty"))));
  }

  @Test
  void strictIterationFailsForNullPage() throws Exception {
    this.server.respondJson(
        "/p1",
        200,
        "{\"data\":[{\"name\":\"a\"}],\"links\":{\"next\":\"" + this.server.uri("/null") + "\"}}");
    OparlList<OparlBody> page =
        this.client.get(this.server.uri("/p1"), new TypeReference<OparlList<OparlBody>>() {});
    List<String> names = new ArrayList<>();

    OparlException e =
        assertThrows(
            OparlException.class, () -> page.all().forEach(body -> names.add(body.getName())));

    assertEquals(List.of("a"), names);
    assertEquals(this.server.uri("/null"), e.getUri());
  }
}
