package com.sitepark.oparlclient.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.sitepark.oparlclient.OparlClient;
import com.sitepark.oparlclient.core.OparlList;
import com.sitepark.oparlclient.core.OparlReference;
import com.sitepark.oparlclient.v1.objects.OparlBody;
import com.sitepark.oparlclient.v1.objects.OparlConsultation;
import com.sitepark.oparlclient.v1.objects.OparlLocation;
import com.sitepark.oparlclient.v1.objects.OparlMeeting;
import com.sitepark.oparlclient.v1.objects.OparlSystem;
import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;

class OparlReferenceDeserializerTest {

  /** Records the type a reference would be resolved as, instead of sending a request. */
  private static final class TypeCapturingClient extends OparlClient {

    private JavaType resolvedType;

    @Override
    public <R> CompletableFuture<R> resolve(URI uri, TypeReference<R> typeReference) {
      this.resolvedType = (JavaType) typeReference.getType();
      return CompletableFuture.completedFuture(null);
    }
  }

  private final TypeCapturingClient client = new TypeCapturingClient();

  @Test
  void resolvesSingleReference() throws Exception {
    OparlConsultation consultation =
        this.client.deserializeJson(
            "{\"meeting\":\"https://oparl.example.org/meeting/1\"}",
            new TypeReference<OparlConsultation>() {});

    consultation.getMeeting().resolve();

    assertEquals(OparlMeeting.class, this.client.resolvedType.getRawClass());
  }

  @Test
  void resolvesReferenceInList() throws Exception {
    OparlLocation location =
        this.client.deserializeJson(
            "{\"bodies\":[\"https://oparl.example.org/body/1\"]}",
            new TypeReference<OparlLocation>() {});

    location.getBodies().get(0).resolve();

    assertEquals(OparlBody.class, this.client.resolvedType.getRawClass());
  }

  @Test
  void resolvesReferenceToExternalList() throws Exception {
    OparlSystem system =
        this.client.deserializeJson(
            "{\"body\":\"https://oparl.example.org/bodies\"}", new TypeReference<OparlSystem>() {});

    system.getBody().resolve();

    assertEquals(OparlList.class, this.client.resolvedType.getRawClass());
    assertEquals(OparlBody.class, this.client.resolvedType.containedType(0).getRawClass());
  }

  @Test
  void resolvesRootReference() throws Exception {
    OparlReference<OparlBody> reference =
        this.client.deserializeJson(
            "\"https://oparl.example.org/body/1\"",
            new TypeReference<OparlReference<OparlBody>>() {});

    reference.resolve();

    assertEquals(OparlBody.class, this.client.resolvedType.getRawClass());
  }

  @Test
  void resolvesRootListOfReferences() throws Exception {
    List<OparlReference<OparlBody>> references =
        this.client.deserializeJson(
            "[\"https://oparl.example.org/body/1\"]",
            new TypeReference<List<OparlReference<OparlBody>>>() {});

    references.get(0).resolve();

    assertEquals(OparlBody.class, this.client.resolvedType.getRawClass());
  }
}
