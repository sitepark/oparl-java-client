package com.sitepark.oparlclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.sitepark.oparlclient.core.OparlException;
import com.sitepark.oparlclient.core.OparlFutures;
import com.sitepark.oparlclient.core.OparlHttpException;
import com.sitepark.oparlclient.core.OparlList;
import com.sitepark.oparlclient.core.OparlParseException;
import com.sitepark.oparlclient.v1.objects.OparlBody;
import com.sitepark.oparlclient.v1.objects.OparlSystem;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SynchronousAccessTest {

  private MockOparlServer server;

  private final OparlClient client = new OparlClient();

  @BeforeEach
  void startServer() throws Exception {
    this.server = new MockOparlServer();
    this.server.respondJson(
        "/system", 200, "{\"name\":\"System\",\"body\":\"" + this.server.uri("/bodies") + "\"}");
    this.server.respondJson(
        "/bodies",
        200,
        "{\"data\":[{\"name\":\"a\"}],\"pagination\":{},"
            + "\"links\":{\"next\":\""
            + this.server.uri("/bodies2")
            + "\"}}");
    this.server.respondJson(
        "/bodies2", 200, "{\"data\":[{\"name\":\"b\"}],\"pagination\":{},\"links\":{}}");
  }

  @AfterEach
  void stopServer() {
    this.server.close();
  }

  @Test
  void getsObjectsAndReferences() {
    OparlSystem system = this.client.get(this.server.uri("/system"), OparlSystem.class);
    OparlList<OparlBody> bodies = system.getBody().get();

    assertEquals("System", system.getName());
    assertEquals("a", bodies.getData().get(0).getName());
    assertEquals(
        "System",
        this.client.get(this.server.uri("/system").toString(), OparlSystem.class).getName());
  }

  @Test
  void getsWithTypeReference() {
    OparlList<OparlBody> bodies =
        this.client.get(this.server.uri("/bodies"), new TypeReference<OparlList<OparlBody>>() {});

    assertEquals("a", bodies.getData().get(0).getName());
  }

  @Test
  void getsNextPage() {
    OparlList<OparlBody> first =
        this.client.get(this.server.uri("/bodies"), new TypeReference<OparlList<OparlBody>>() {});

    OparlList<OparlBody> second = first.getNextPage();

    assertEquals("b", second.getData().get(0).getName());
    assertThrows(NoSuchElementException.class, second::getNextPage);
  }

  @Test
  void throwsCauseInsteadOfCompletionException() {
    OparlHttpException e =
        assertThrows(
            OparlHttpException.class,
            () -> this.client.get(this.server.uri("/missing"), OparlBody.class));

    assertEquals(404, e.getStatusCode());
  }

  @Test
  void throwsParseExceptionForInvalidJson() {
    this.server.respond("/broken", 200, "application/json", "{not json");

    OparlParseException e =
        assertThrows(
            OparlParseException.class,
            () -> this.client.get(this.server.uri("/broken"), OparlBody.class));

    assertInstanceOf(JsonProcessingException.class, e.getCause());
  }

  @Test
  void joinUnwrapsFutures() {
    assertEquals("x", OparlFutures.join(CompletableFuture.completedFuture("x")));

    IllegalStateException unchecked = new IllegalStateException("unchecked");
    assertEquals(
        unchecked,
        assertThrows(
            IllegalStateException.class,
            () -> OparlFutures.join(CompletableFuture.failedFuture(unchecked))));

    IOException checked = new IOException("checked");
    assertEquals(
        checked,
        assertThrows(
                OparlException.class,
                () -> OparlFutures.join(CompletableFuture.failedFuture(checked)))
            .getCause());
  }
}
