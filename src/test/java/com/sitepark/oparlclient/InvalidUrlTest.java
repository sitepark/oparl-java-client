package com.sitepark.oparlclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sitepark.oparlclient.core.OparlException;
import com.sitepark.oparlclient.core.OparlList;
import com.sitepark.oparlclient.core.OparlReference;
import com.sitepark.oparlclient.v1.objects.OparlBody;
import com.sitepark.oparlclient.v1.objects.OparlConsultation;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import org.junit.jupiter.api.Test;

/** Invalid URLs must be reported by the returned future, not thrown. */
class InvalidUrlTest {

  private final OparlClient client = new OparlClient();

  private static Throwable failureOf(CompletableFuture<?> future) {
    return assertThrows(CompletionException.class, future::join).getCause();
  }

  private OparlConsultation consultationWithMeeting(String url) throws Exception {
    return this.client.deserializeJson(
        "{\"meeting\":\"" + url + "\"}", new TypeReference<OparlConsultation>() {});
  }

  @Test
  void reportsRelativeReferenceThroughFuture() throws Exception {
    CompletableFuture<?> future = this.consultationWithMeeting("/meeting/1").getMeeting().resolve();

    Throwable cause = failureOf(future);
    assertInstanceOf(OparlException.class, cause);
    assertTrue(cause.getMessage().contains("Unsupported URL /meeting/1"), cause.getMessage());
  }

  @Test
  void reportsUnsupportedSchemeThroughFuture() throws Exception {
    CompletableFuture<?> future =
        this.consultationWithMeeting("ftp://oparl.example.org/meeting/1").getMeeting().resolve();

    assertInstanceOf(OparlException.class, failureOf(future));
  }

  @Test
  void reportsInvalidStringUrlThroughFuture() {
    Throwable cause =
        failureOf(this.client.resolve("https://oparl.example.org/a b", OparlBody.class));

    assertEquals(OparlException.class, cause.getClass());
    assertInstanceOf(IllegalArgumentException.class, cause.getCause());
    assertEquals(
        OparlException.class,
        failureOf(this.client.resolveAny("https://oparl.example.org/a b")).getClass());
  }

  @Test
  void synchronousGetThrowsCause() {
    assertThrows(
        OparlException.class,
        () -> this.client.get("https://oparl.example.org/a b", OparlBody.class));
  }

  @Test
  void iterationFailsWithExceptionOfPage() throws Exception {
    OparlList<OparlBody> page = this.pageWithNext("/bodies?page=2");
    List<String> names = new ArrayList<>();

    OparlException e =
        assertThrows(
            OparlException.class, () -> page.all().forEach(body -> names.add(body.getName())));

    assertEquals(List.of("a"), names);
    assertTrue(e.getMessage().startsWith("Unsupported URL"), e.getMessage());
  }

  @Test
  void reportsMissingResolverThroughFuture() {
    OparlReference<OparlBody> reference =
        new OparlReference<>(URI.create("https://oparl.example.org/body/1"), null, null);

    assertInstanceOf(IllegalStateException.class, failureOf(reference.resolve()));
    assertInstanceOf(
        IllegalStateException.class, failureOf(reference.resolveAs(OparlConsultation.class)));
  }

  private OparlList<OparlBody> pageWithNext(String next) throws Exception {
    return this.client.deserializeJson(
        "{\"data\":[{\"name\":\"a\"}],\"links\":{\"next\":\"" + next + "\"}}",
        new TypeReference<OparlList<OparlBody>>() {});
  }
}
