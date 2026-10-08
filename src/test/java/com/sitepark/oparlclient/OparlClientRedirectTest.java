package com.sitepark.oparlclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sitepark.oparlclient.core.OparlHttpException;
import com.sitepark.oparlclient.core.OparlList;
import com.sitepark.oparlclient.v1.objects.OparlBody;
import java.net.http.HttpClient;
import java.util.Set;
import java.util.concurrent.CompletionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OparlClientRedirectTest {

  private MockOparlServer server;

  @BeforeEach
  void startServer() throws Exception {
    this.server = new MockOparlServer();
    this.server
        .redirect("/old/body/1", 301, "/body/1")
        .respondJson("/body/1", 200, "{\"name\":\"Stadt Beispiel\"}");
  }

  @AfterEach
  void stopServer() {
    this.server.close();
  }

  @Test
  void defaultClientFollowsRedirects() {
    OparlBody body =
        new OparlClient().getAsync(this.server.uri("/old/body/1"), OparlBody.class).join();

    assertEquals("Stadt Beispiel", body.getName());
  }

  @Test
  void followsRelativeRedirect() {
    this.server.redirectToLocationHeader("/older/body/1", 301, "../../body/1");

    OparlBody body =
        new OparlClient().getAsync(this.server.uri("/older/body/1"), OparlBody.class).join();

    assertEquals("Stadt Beispiel", body.getName());
  }

  @Test
  void recordsFinalUriOfRedirectedList() {
    this.server.respondJson("/bodies", 200, "{\"data\":[],\"pagination\":{},\"links\":{}}");
    this.server.redirect("/old/bodies", 301, "/bodies");

    OparlList<OparlBody> list =
        new OparlClient()
            .getAsync(this.server.uri("/old/bodies"), new TypeReference<OparlList<OparlBody>>() {})
            .join();

    assertEquals(
        Set.of(this.server.uri("/old/bodies"), this.server.uri("/bodies")), list.getSourceUris());
  }

  @Test
  void customClientKeepsItsRedirectPolicy() {
    OparlClient client = OparlClient.builder().httpClient(HttpClient.newHttpClient()).build();

    CompletionException e =
        assertThrows(
            CompletionException.class,
            () -> client.getAsync(this.server.uri("/old/body/1"), OparlBody.class).join());

    assertEquals(301, assertInstanceOf(OparlHttpException.class, e.getCause()).getStatusCode());
  }
}
