package com.sitepark.oparlclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitepark.oparlclient.core.OparlReference;
import com.sitepark.oparlclient.v1.objects.OparlConsultation;
import com.sitepark.oparlclient.v1.objects.OparlFile;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class DeserializationRobustnessTest {

  private final OparlClient client = new OparlClient();

  private OparlFile file(String json) throws Exception {
    return this.client.deserializeJson(json, new TypeReference<OparlFile>() {});
  }

  private OparlConsultation consultation(String json) throws Exception {
    return this.client.deserializeJson(json, new TypeReference<OparlConsultation>() {});
  }

  @Test
  void encodesSpacesInUrl() throws Exception {
    OparlFile file = this.file("{\"accessUrl\":\"https://oparl.example.org/files/a b.pdf\"}");

    assertEquals(URI.create("https://oparl.example.org/files/a%20b.pdf"), file.getAccessUrl());
  }

  @Test
  void keepsValidUrlUnchanged() throws Exception {
    OparlFile file =
        this.file("{\"accessUrl\":\"https://oparl.example.org/files/a%20b.pdf?x=1&y=%2B#top\"}");

    assertEquals(
        "https://oparl.example.org/files/a%20b.pdf?x=1&y=%2B#top", file.getAccessUrl().toString());
  }

  @Test
  void encodesInvalidUrlsInList() throws Exception {
    OparlFile file =
        this.file(
            "{\"paper\":[\"https://oparl.example.org/paper/1\","
                + "\"https://oparl.example.org/paper/a|b\"]}");

    assertEquals(
        List.of(
            URI.create("https://oparl.example.org/paper/1"),
            URI.create("https://oparl.example.org/paper/a%7Cb")),
        file.getPaper().stream().map(OparlReference::getUri).collect(Collectors.toList()));
  }

  @Test
  void ignoresUrlThatCanNotBeRepaired() throws Exception {
    OparlFile file = this.file("{\"accessUrl\":\"http://[oparl.example.org\",\"name\":\"Datei\"}");

    assertNull(file.getAccessUrl());
    assertEquals("Datei", file.getName());
  }

  @Test
  void keepsEmptyUrlAsEmptyUri() throws Exception {
    assertEquals(URI.create(""), this.file("{\"accessUrl\":\"\"}").getAccessUrl());
  }

  @Test
  void encodesSpacesInReference() throws Exception {
    OparlConsultation consultation =
        this.consultation("{\"meeting\":\"https://oparl.example.org/meeting/a b\"}");

    assertEquals(
        URI.create("https://oparl.example.org/meeting/a%20b"), consultation.getMeeting().getUri());
  }

  @Test
  void ignoresReferenceThatCanNotBeRepaired() throws Exception {
    OparlConsultation consultation =
        this.consultation("{\"meeting\":\"http://[oparl.example.org\",\"role\":\"Beratung\"}");

    assertNull(consultation.getMeeting());
    assertEquals("Beratung", consultation.getRole());
  }

  @Test
  void usesIdOfEmbeddedObjectAsReference() throws Exception {
    OparlConsultation consultation =
        this.consultation(
            "{\"meeting\":{\"id\":\"https://oparl.example.org/meeting/1\",\"name\":\"Rat\"}}");

    assertEquals(
        URI.create("https://oparl.example.org/meeting/1"), consultation.getMeeting().getUri());
  }

  @Test
  void ignoresReferenceThatIsNoUrl() throws Exception {
    OparlConsultation consultation = this.consultation("{\"meeting\":42}");

    assertNull(consultation.getMeeting());
  }

  @Test
  void readsFileSizeLargerThanInt() throws Exception {
    OparlFile file = this.file("{\"size\":3000000000}");

    assertEquals(Long.valueOf(3_000_000_000L), file.getSize());
  }

  @Test
  void serializesFileSize() throws Exception {
    OparlFile file = new OparlFile();
    file.setSize(3_000_000_000L);

    String json = new ObjectMapper().writeValueAsString(file);

    assertTrue(json.contains("\"size\":3000000000"), json);
  }
}
