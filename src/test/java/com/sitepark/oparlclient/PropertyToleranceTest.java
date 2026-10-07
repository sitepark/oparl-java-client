package com.sitepark.oparlclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitepark.oparlclient.core.OparlList;
import com.sitepark.oparlclient.v1.objects.OparlAgendaItem;
import com.sitepark.oparlclient.v1.objects.OparlConsultation;
import com.sitepark.oparlclient.v1.objects.OparlLegislativeTerm;
import com.sitepark.oparlclient.v1.objects.OparlMeeting;
import com.sitepark.oparlclient.v1.objects.OparlPerson;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Servers that send values which do not match the type required by the specification. */
class PropertyToleranceTest {

  private static final String MEMBERSHIP_URL = "https://oparl.example.org/membership/1";

  private final OparlClient client = new OparlClient();

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void leavesOutInvalidElementsOfList() throws Exception {
    OparlPerson person =
        this.person(
            "{\"membership\":[\""
                + MEMBERSHIP_URL
                + "\",{\"id\":\"https://oparl.example.org/membership/2\"}]}");

    assertEquals(1, person.getMembership().size());
    assertEquals(
        URI.create("https://oparl.example.org/membership/2"),
        person.getMembership().get(0).getId());
  }

  @Test
  void keepsOriginalValueAsAdditionalProperty() throws Exception {
    String membership =
        "[\"" + MEMBERSHIP_URL + "\",{\"id\":\"https://oparl.example.org/membership/2\"}]";

    OparlPerson person = this.person("{\"membership\":" + membership + "}");

    assertEquals(this.mapper.readTree(membership), person.getAdditionalProperty("membership"));
  }

  @Test
  void leavesListNullIfNoElementIsValid() throws Exception {
    OparlPerson person = this.person("{\"membership\":[\"" + MEMBERSHIP_URL + "\"]}");

    assertNull(person.getMembership());
    assertTrue(person.getAdditionalProperty("membership").isArray());
  }

  @Test
  void leavesSingleObjectNullIfInvalid() throws Exception {
    OparlMeeting meeting =
        this.client.deserializeJson(
            "{\"location\":\"https://oparl.example.org/location/1\"}",
            new TypeReference<OparlMeeting>() {});

    assertNull(meeting.getLocation());
    assertEquals(
        "https://oparl.example.org/location/1",
        meeting.getAdditionalProperty("location").textValue());
  }

  @Test
  void keepsValidAndNullValuesUntouched() throws Exception {
    OparlPerson person =
        this.person("{\"membership\":[{\"id\":\"" + MEMBERSHIP_URL + "\"}],\"location\":null}");

    assertEquals(1, person.getMembership().size());
    assertTrue(person.getAdditionalProperties().isEmpty());
  }

  @Test
  void readsListPageDespiteInvalidEmbeddedObjects() throws Exception {
    OparlList<OparlPerson> page =
        this.client.deserializeJson(
            "{\"data\":[{\"name\":\"A\",\"membership\":[\""
                + MEMBERSHIP_URL
                + "\"]},{\"name\":\"B\"}]}",
            new TypeReference<OparlList<OparlPerson>>() {});

    assertEquals(List.of("A", "B"), page.getData().stream().map(OparlPerson::getName).toList());
  }

  @Test
  void writesOriginalValueBack() throws Exception {
    OparlPerson person =
        this.person("{\"name\":\"A\",\"membership\":[\"" + MEMBERSHIP_URL + "\"]}");

    JsonNode written = this.mapper.readTree(this.mapper.writeValueAsString(person));

    assertEquals(this.mapper.readTree("[\"" + MEMBERSHIP_URL + "\"]"), written.get("membership"));
    assertFalse(written.toString().contains("null"), written.toString());
  }

  @Test
  void leavesOutUnparsableUrlsOfReferenceList() throws Exception {
    OparlMeeting meeting =
        this.meeting("{\"participant\":[\"http://[broken\",\"https://oparl.example.org/p/1\"]}");

    assertEquals(1, meeting.getParticipant().size());
    assertEquals(
        URI.create("https://oparl.example.org/p/1"), meeting.getParticipant().get(0).getUri());
    assertEquals(2, meeting.getAdditionalProperty("participant").size());
  }

  @Test
  void leavesOutReferencesThatAreNoUrl() throws Exception {
    OparlMeeting meeting =
        this.meeting("{\"participant\":[42,{\"name\":\"x\"},\"https://oparl.example.org/p/1\"]}");

    assertEquals(1, meeting.getParticipant().size());
  }

  @Test
  void leavesSingleReferenceNullIfInvalid() throws Exception {
    OparlConsultation consultation =
        this.client.deserializeJson(
            "{\"meeting\":42,\"role\":\"Beratung\"}", new TypeReference<OparlConsultation>() {});

    assertNull(consultation.getMeeting());
    assertEquals(42, consultation.getAdditionalProperty("meeting").intValue());
    assertEquals("Beratung", consultation.getRole());
  }

  @Test
  void readsSingleValueAsList() throws Exception {
    OparlPerson person = this.person("{\"email\":\"info@example.org\"}");

    assertEquals(List.of("info@example.org"), person.getEmail());
    assertTrue(person.getAdditionalProperties().isEmpty());
  }

  @Test
  void leavesNumberNullIfInvalid() throws Exception {
    OparlAgendaItem item =
        this.client.deserializeJson(
            "{\"order\":\"eins\",\"name\":\"TOP 1\"}", new TypeReference<OparlAgendaItem>() {});

    assertNull(item.getOrder());
    assertEquals("eins", item.getAdditionalProperty("order").textValue());
    assertEquals("TOP 1", item.getName());
  }

  @Test
  void keepsInvalidDateAsAdditionalProperty() throws Exception {
    OparlLegislativeTerm term =
        this.client.deserializeJson(
            "{\"startDate\":\"0000-00-00\"}", new TypeReference<OparlLegislativeTerm>() {});

    assertNull(term.getStartDate());
    assertEquals("0000-00-00", term.getAdditionalProperty("startDate").textValue());
    assertEquals(
        "0000-00-00",
        this.mapper.readTree(this.mapper.writeValueAsString(term)).get("startDate").textValue());
  }

  private OparlMeeting meeting(String json) throws Exception {
    return this.client.deserializeJson(json, new TypeReference<OparlMeeting>() {});
  }

  private OparlPerson person(String json) throws Exception {
    return this.client.deserializeJson(json, new TypeReference<OparlPerson>() {});
  }
}
