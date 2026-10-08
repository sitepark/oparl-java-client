package com.sitepark.oparlclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitepark.oparlclient.core.OparlHttpException;
import com.sitepark.oparlclient.core.OparlList;
import com.sitepark.oparlclient.v1.objects.OparlBody;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OparlListStrictIterationTest {

  private MockOparlServer server;

  private final OparlClient client = new OparlClient();

  @BeforeEach
  void startServer() throws Exception {
    this.server = new MockOparlServer();
  }

  @AfterEach
  void stopServer() {
    this.server.close();
  }

  @Test
  void iteratesOverAllPagesInForEachLoop() {
    this.page("/p1", "/p2", "a", "b");
    this.page("/p2", "/p3");
    this.page("/p3", null, "c");

    List<String> names = new ArrayList<>();
    for (OparlBody body : this.load("/p1").all()) {
      names.add(body.getName());
    }

    assertEquals(List.of("a", "b", "c"), names);
  }

  @Test
  void streamsAllPages() {
    this.page("/p1", "/p2", "a", "b");
    this.page("/p2", null, "c");

    List<String> names =
        this.load("/p1").stream().map(OparlBody::getName).collect(Collectors.toList());

    assertEquals(List.of("a", "b", "c"), names);
  }

  @Test
  void fetchesPagesLazilyWhenStreaming() {
    this.page("/p1", "/p2", "a", "b");
    this.page("/p2", "/p3", "c");
    this.page("/p3", null, "d");

    List<String> names =
        this.load("/p1").stream().limit(1).map(OparlBody::getName).collect(Collectors.toList());

    assertEquals(List.of("a"), names);
    assertEquals(0, this.server.requestCount("/p3"));
  }

  @Test
  void failsIfPageCanNotBeFetched() {
    this.page("/p1", "/p2", "a");
    this.server.respond("/p2", 500, "text/plain", "error");
    List<String> names = new ArrayList<>();

    OparlHttpException e =
        assertThrows(
            OparlHttpException.class,
            () -> this.load("/p1").all().forEach(body -> names.add(body.getName())));

    assertEquals(List.of("a"), names);
    assertEquals(this.server.uri("/p2"), e.getUri());
    assertEquals(500, e.getStatusCode());
  }

  @Test
  void streamFailsIfPageCanNotBeFetched() {
    this.page("/p1", "/p2", "a");
    this.server.respond("/p2", 500, "text/plain", "error");

    assertThrows(
        OparlHttpException.class, () -> this.load("/p1").stream().collect(Collectors.toList()));
  }

  @Test
  void endsOnCycleWithoutFailing() {
    this.page("/p1", "/p2", "a");
    this.page("/p2", "/p1", "b");

    assertEquals(2, this.load("/p1").stream().count());
  }

  @Test
  void canIterateMoreThanOnce() {
    this.page("/p1", null, "a");
    Iterable<OparlBody> all = this.load("/p1").all();

    assertEquals("a", all.iterator().next().getName());
    assertEquals("a", all.iterator().next().getName());
  }

  @Test
  void navigatesToNextPage() {
    this.page("/p1", "/p2", "a");
    this.page("/p2", null, "b");
    OparlList<OparlBody> first = this.load("/p1");

    assertTrue(first.hasNextPage());
    OparlList<OparlBody> second = first.fetchNextPageAsync().join();

    assertEquals("b", second.getData().get(0).getName());
    assertFalse(second.hasNextPage());
    CompletionException e =
        assertThrows(CompletionException.class, () -> second.fetchNextPageAsync().join());
    assertInstanceOf(NoSuchElementException.class, e.getCause());
  }

  @Test
  void serializesPageWithoutSourceUris() throws Exception {
    this.page("/p1", null, "a");

    String json = new ObjectMapper().writeValueAsString(this.load("/p1"));

    assertTrue(json.contains("\"data\":[{"), json);
    assertFalse(json.contains("sourceUris"), json);
    assertEquals(1, this.server.requestCount("/p1"));
  }

  private void page(String path, String next, String... names) {
    StringBuilder json = new StringBuilder(256).append("{\"data\":[");
    for (int i = 0; i < names.length; i++) {
      json.append(i > 0 ? "," : "").append("{\"name\":\"").append(names[i]).append("\"}");
    }
    json.append("],\"pagination\":{},\"links\":{");
    if (next != null) {
      json.append("\"next\":\"").append(this.server.uri(next)).append('"');
    }
    json.append("}}");
    this.server.respondJson(path, 200, json.toString());
  }

  private OparlList<OparlBody> load(String path) {
    return this.client
        .getAsync(this.server.uri(path), new TypeReference<OparlList<OparlBody>>() {})
        .join();
  }
}
