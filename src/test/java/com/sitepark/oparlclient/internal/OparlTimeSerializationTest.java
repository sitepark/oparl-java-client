package com.sitepark.oparlclient.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitepark.oparlclient.OparlClient;
import com.sitepark.oparlclient.v1.objects.OparlLegislativeTerm;
import com.sitepark.oparlclient.v1.objects.OparlMeeting;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class OparlTimeSerializationTest {

  private static final ZoneOffset CET = ZoneOffset.ofHours(1);

  private static final ZoneOffset CEST = ZoneOffset.ofHours(2);

  @Test
  void parsesDateTimeOfSpecification() {
    assertEquals(
        OffsetDateTime.of(1969, 7, 21, 2, 56, 0, 0, ZoneOffset.UTC),
        OparlTimeSerialization.parseDateTime("1969-07-21T02:56:00+00:00"));
  }

  @Test
  void parsesDateTimeWithZuluAndFractionOfSeconds() {
    assertEquals(
        OffsetDateTime.of(2024, 1, 21, 10, 15, 30, 500_000_000, ZoneOffset.UTC),
        OparlTimeSerialization.parseDateTime("2024-01-21T10:15:30.5Z"));
  }

  @Test
  void interpretsDateTimeWithoutOffsetInGermanTime() {
    assertEquals(
        OffsetDateTime.of(2024, 1, 21, 10, 0, 0, 0, CET),
        OparlTimeSerialization.parseDateTime("2024-01-21T10:00:00"));
    assertEquals(
        OffsetDateTime.of(2024, 7, 21, 10, 0, 0, 0, CEST),
        OparlTimeSerialization.parseDateTime("2024-07-21 10:00:00"));
  }

  @Test
  void interpretsDateAsStartOfDayForDateTime() {
    assertEquals(
        OffsetDateTime.of(2024, 1, 21, 0, 0, 0, 0, CET),
        OparlTimeSerialization.parseDateTime("2024-01-21"));
  }

  @Test
  void parsesDateAndTakesDateOfDateTime() {
    assertEquals(LocalDate.of(2024, 3, 1), OparlTimeSerialization.parseDate("2024-03-01"));
    assertEquals(
        LocalDate.of(2024, 3, 1), OparlTimeSerialization.parseDate("2024-03-01T23:30:00+01:00"));
    assertEquals(LocalDate.of(2024, 3, 1), OparlTimeSerialization.parseDate("2024-03-01T23:30:00"));
  }

  @Test
  void returnsNullForInvalidValues() {
    assertNull(OparlTimeSerialization.parseDateTime("gestern"));
    assertNull(OparlTimeSerialization.parseDate("2024-13-01"));
  }

  @Test
  void formatsDateTimeAsInSpecification() {
    assertEquals(
        "2024-01-21T10:00:00+01:00",
        OparlTimeSerialization.formatDateTime(OffsetDateTime.of(2024, 1, 21, 10, 0, 0, 0, CET)));
    assertEquals(
        "2024-01-21T10:00:00+00:00",
        OparlTimeSerialization.formatDateTime(
            OffsetDateTime.of(2024, 1, 21, 10, 0, 0, 0, ZoneOffset.UTC)));
  }

  @Test
  void readsObjectsAndKeepsOtherPropertiesOnInvalidValues() throws Exception {
    OparlMeeting meeting =
        new OparlClient()
            .deserializeJson(
                "{\"name\":\"Rat\",\"start\":\"2024-01-21T18:00:00+01:00\",\"end\":\"unbekannt\","
                    + "\"created\":\"2024-01-01T09:00:00Z\"}",
                new TypeReference<OparlMeeting>() {});

    assertEquals("Rat", meeting.getName());
    assertEquals(OffsetDateTime.of(2024, 1, 21, 18, 0, 0, 0, CET), meeting.getStart());
    assertNull(meeting.getEnd());
    assertEquals(OffsetDateTime.of(2024, 1, 1, 9, 0, 0, 0, ZoneOffset.UTC), meeting.getCreated());
  }

  @Test
  void writesObjectsWithAnyObjectMapper() throws Exception {
    OparlLegislativeTerm term = new OparlLegislativeTerm();
    term.setStartDate(LocalDate.of(2020, 11, 1));
    term.setCreated(OffsetDateTime.of(2020, 10, 1, 12, 0, 0, 0, CEST));

    String json = new ObjectMapper().writeValueAsString(term);

    assertTrue(json.contains("\"startDate\":\"2020-11-01\""), json);
    assertTrue(json.contains("\"created\":\"2020-10-01T12:00:00+02:00\""), json);

    OparlLegislativeTerm copy = new ObjectMapper().readValue(json, OparlLegislativeTerm.class);
    assertEquals(term.getStartDate(), copy.getStartDate());
    assertEquals(term.getCreated(), copy.getCreated());
  }
}
