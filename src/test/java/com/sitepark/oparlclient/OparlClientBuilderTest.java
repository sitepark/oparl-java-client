package com.sitepark.oparlclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.sitepark.oparlclient.v1.objects.OparlConsultation;
import com.sitepark.oparlclient.v1.objects.OparlMeeting;
import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;
import org.junit.jupiter.api.Test;

class OparlClientBuilderTest {

  @Test
  void usesDefaults() {
    OparlClient client = OparlClient.builder().build();

    assertEquals(OparlClient.DEFAULT_REQUEST_TIMEOUT, client.getRequestTimeout());
    assertNotNull(client.getClient());
    assertEquals(HttpClient.Redirect.NORMAL, client.getClient().followRedirects());
  }

  @Test
  void usesGivenHttpClientAndTimeout() {
    try (HttpClient httpClient = HttpClient.newHttpClient()) {
      OparlClient client =
          OparlClient.builder()
              .httpClient(httpClient)
              .requestTimeout(Duration.ofSeconds(30))
              .build();

      assertSame(httpClient, client.getClient());
      assertEquals(Duration.ofSeconds(30), client.getRequestTimeout());
    }
  }

  @Test
  void rejectsNonPositiveTimeout() {
    assertThrows(
        IllegalArgumentException.class, () -> OparlClient.builder().requestTimeout(Duration.ZERO));
  }

  @Test
  void appliesObjectMapperCustomizer() {
    OparlClient client =
        OparlClient.builder()
            .objectMapperCustomizer(
                mapper -> mapper.enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES))
            .build();

    assertThrows(
        MismatchedInputException.class,
        () -> client.deserializeJson("{\"deleted\":null}", new TypeReference<OparlMeeting>() {}));
  }

  @Test
  void keepsOwnDeserializersWhenCustomized() throws Exception {
    OparlClient client =
        OparlClient.builder()
            .objectMapperCustomizer(
                mapper -> mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES))
            .build();

    OparlConsultation consultation =
        client.deserializeJson(
            "{\"meeting\":\"https://oparl.example.org/meeting/a b\"}",
            new TypeReference<OparlConsultation>() {});

    assertEquals(
        URI.create("https://oparl.example.org/meeting/a%20b"), consultation.getMeeting().getUri());
    assertNull(consultation.getAuthoritative());
  }
}
