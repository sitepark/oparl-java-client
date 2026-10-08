package com.sitepark.oparlclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sitepark.oparlclient.core.OparlHttpException;
import com.sitepark.oparlclient.v1.objects.OparlBody;
import java.util.concurrent.CompletionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OparlClientHttpErrorTest {

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
  void resolvesSuccessfulResponse() {
    this.server.respondJson(
        "/body/1",
        200,
        "{\"id\":\"https://oparl.example.org/body/1\","
            + "\"type\":\"https://schema.oparl.org/1.1/Body\",\"name\":\"Stadt Beispiel\"}");

    OparlBody body = this.client.getAsync(this.server.uri("/body/1"), OparlBody.class).join();

    assertEquals("Stadt Beispiel", body.getName());
  }

  @Test
  void failsWithOparlErrorObject() {
    this.server.respondJson(
        "/body/404",
        404,
        "{\"type\":\"https://schema.oparl.org/1.1/Error\","
            + "\"message\":\"Körperschaft nicht gefunden\",\"debug\":\"id 404\"}");

    OparlHttpException e = this.resolveExpectingHttpError("/body/404");

    assertEquals(404, e.getStatusCode());
    assertEquals(this.server.uri("/body/404"), e.getUri());
    assertNotNull(e.getError());
    assertEquals("Körperschaft nicht gefunden", e.getError().getMessage());
    assertEquals("id 404", e.getError().getDebug());
  }

  @Test
  void recognizesErrorObjectOfOparl10() {
    this.server.respondJson(
        "/body/410",
        410,
        "{\"type\":\"https://schema.oparl.org/1.0/Error\",\"message\":\"gelöscht\"}");

    OparlHttpException e = this.resolveExpectingHttpError("/body/410");

    assertNotNull(e.getError());
    assertEquals("gelöscht", e.getError().getMessage());
  }

  @Test
  void removesLineBreaksOfServerMessageFromExceptionMessage() {
    this.server.respondJson(
        "/body/400",
        400,
        "{\"type\":\"https://schema.oparl.org/1.1/Error\","
            + "\"message\":\"falsch\\nINFO gefälschte Logzeile\"}");

    OparlHttpException e = this.resolveExpectingHttpError("/body/400");

    assertFalse(e.getMessage().contains("\n"), e.getMessage());
    assertTrue(e.getMessage().endsWith("falsch?INFO gefälschte Logzeile"), e.getMessage());
    assertEquals("falsch\nINFO gefälschte Logzeile", e.getError().getMessage());
  }

  @Test
  void failsWithHtmlErrorPage() {
    this.server.respond("/body/500", 500, "text/html", "<html><body>Oops</body></html>");

    OparlHttpException e = this.resolveExpectingHttpError("/body/500");

    assertEquals(500, e.getStatusCode());
    assertNull(e.getError());
  }

  @Test
  void ignoresJsonWithoutErrorType() {
    this.server.respondJson("/body/503", 503, "{\"message\":\"maintenance\"}");

    OparlHttpException e = this.resolveExpectingHttpError("/body/503");

    assertEquals(503, e.getStatusCode());
    assertNull(e.getError());
  }

  private OparlHttpException resolveExpectingHttpError(String path) {
    CompletionException e =
        assertThrows(
            CompletionException.class,
            () -> this.client.getAsync(this.server.uri(path), OparlBody.class).join());
    return assertInstanceOf(OparlHttpException.class, e.getCause());
  }
}
