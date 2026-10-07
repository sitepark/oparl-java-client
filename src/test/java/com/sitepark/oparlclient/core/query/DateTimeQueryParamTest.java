package com.sitepark.oparlclient.core.query;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;

class DateTimeQueryParamTest {

  @Test
  void formatsOffsetDateTimeWithoutFractionOfSeconds() {
    OffsetDateTime date =
        OffsetDateTime.of(2014, 1, 1, 2, 0, 0, 123_000_000, ZoneOffset.ofHours(1));

    Modified param = Modified.since(date);

    assertEquals("modified_since", param.getName());
    assertEquals("2014-01-01T02:00:00+01:00", param.getValue());
    assertEquals("modified_since=2014-01-01T02:00:00%2B01:00", param.toString());
  }

  @Test
  void formatsNegativeOffset() {
    OffsetDateTime date = OffsetDateTime.of(2014, 1, 31, 23, 59, 59, 0, ZoneOffset.ofHours(-5));

    assertEquals("2014-01-31T23:59:59-05:00", Modified.until(date).getValue());
  }

  @Test
  void formatsZonedDateTimeWithOffsetOfThatDate() {
    ZoneId berlin = ZoneId.of("Europe/Berlin");

    assertEquals(
        "2024-01-21T00:00:00+01:00",
        Created.since(ZonedDateTime.of(2024, 1, 21, 0, 0, 0, 0, berlin)).getValue());
    assertEquals(
        "2024-08-16T00:00:00+02:00",
        Created.until(ZonedDateTime.of(2024, 8, 16, 0, 0, 0, 0, berlin)).getValue());
  }

  @Test
  void formatsInstantAsUtcWithNumericOffset() {
    Instant instant = Instant.parse("2024-08-16T10:15:30.500Z");

    assertEquals("2024-08-16T10:15:30+00:00", Created.since(instant).getValue());
    assertEquals("created_until", Created.until(instant).getName());
  }
}
