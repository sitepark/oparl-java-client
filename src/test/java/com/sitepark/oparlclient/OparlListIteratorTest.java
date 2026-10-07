package com.sitepark.oparlclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sitepark.oparlclient.core.OparlList;
import com.sitepark.oparlclient.v1.objects.OparlBody;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

class OparlListIteratorTest {

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
  void iteratesOverAllPages() {
    this.page("/p1", "/p1", "/p2", "a", "b");
    this.page("/p2", "/p2", "/p3", "c");
    this.page("/p3", "/p3", null, "d");

    assertEquals(List.of("a", "b", "c", "d"), this.namesFrom("/p1"));
  }

  @Test
  void skipsEmptyPages() {
    this.page("/p1", "/p1", "/p2", "a");
    this.page("/p2", "/p2", "/p3");
    this.page("/p3", "/p3", null, "b");

    assertEquals(List.of("a", "b"), this.namesFrom("/p1"));
  }

  @Test
  void continuesAfterEmptyFirstPage() {
    this.page("/p1", "/p1", "/p2");
    this.page("/p2", "/p2", null, "a");

    assertEquals(List.of("a"), this.namesFrom("/p1"));
  }

  @Test
  @Timeout(value = 5, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
  void stopsAtPageThatWasAlreadyVisited() {
    this.page("/p1", "/p1", "/p2", "a");
    this.page("/p2", "/p2", "/p1", "b");

    assertEquals(List.of("a", "b"), this.namesFrom("/p1"));
  }

  @Test
  @Timeout(value = 5, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
  void stopsOnCycleWithoutSelfLinks() {
    this.page("/p1", null, "/p2", "a");
    this.page("/p2", null, "/p1", "b");

    assertEquals(List.of("a", "b"), this.namesFrom("/p1"));
  }

  @Test
  @Timeout(value = 5, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
  void stopsOnCycleThroughRedirect() {
    this.page("/p1", null, "/p2", "a");
    this.page("/p2", null, "/old/p1", "b");
    this.server.redirect("/old/p1", 301, "/p1");

    assertEquals(List.of("a", "b"), this.namesFrom("/p1"));
  }

  @Test
  void recordsSourceUrisOfLoadedPage() {
    this.server.redirect("/old/p1", 301, "/p1");
    this.page("/p1", null, null, "a");

    OparlList<OparlBody> page = this.resolve("/old/p1");

    assertEquals(Set.of(this.server.uri("/old/p1"), this.server.uri("/p1")), page.getSourceUris());
  }

  @Test
  void throwsNoSuchElementExceptionAtEnd() {
    this.page("/p1", "/p1", null, "a");
    Iterator<OparlBody> iterator = this.resolve("/p1").all().iterator();

    assertTrue(iterator.hasNext());
    assertTrue(iterator.hasNext());
    assertEquals("a", iterator.next().getName());
    assertFalse(iterator.hasNext());
    assertThrows(NoSuchElementException.class, iterator::next);
  }

  @Test
  void nextWorksWithoutHasNext() {
    this.page("/p1", "/p1", "/p2", "a");
    this.page("/p2", "/p2", null, "b");
    Iterator<OparlBody> iterator = this.resolve("/p1").all().iterator();

    assertEquals("a", iterator.next().getName());
    assertEquals("b", iterator.next().getName());
    assertThrows(NoSuchElementException.class, iterator::next);
  }

  @Test
  void handlesMissingDataAndLinks() {
    this.server.respondJson("/p1", 200, "{\"data\":null,\"links\":null}");

    assertEquals(List.of(), this.namesFrom("/p1"));
  }

  private void page(String path, String self, String next, String... names) {
    StringBuilder json = new StringBuilder(256).append("{\"data\":[");
    for (int i = 0; i < names.length; i++) {
      json.append(i > 0 ? "," : "").append("{\"name\":\"").append(names[i]).append("\"}");
    }
    json.append("],\"pagination\":{},\"links\":{");
    List<String> links = new ArrayList<>();
    if (self != null) {
      links.add("\"self\":\"" + this.server.uri(self) + "\"");
    }
    if (next != null) {
      links.add("\"next\":\"" + this.server.uri(next) + "\"");
    }
    json.append(String.join(",", links)).append("}}");
    this.server.respondJson(path, 200, json.toString());
  }

  private OparlList<OparlBody> resolve(String path) {
    return this.client
        .resolve(this.server.uri(path), new TypeReference<OparlList<OparlBody>>() {})
        .join();
  }

  private List<String> namesFrom(String path) {
    List<String> names = new ArrayList<>();
    this.resolve(path).all().forEach(body -> names.add(body.getName()));
    return names;
  }
}
