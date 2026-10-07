package com.sitepark.oparlclient.core.query;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

/**
 * Filters a list by the {@code created} property of its elements, via the URL parameters {@code
 * created_since} and {@code created_until}:
 *
 * <pre>{@code
 * body.getPaper().withQueryParams(Created.since(Instant.parse("2024-01-01T00:00:00Z"))).get();
 * }</pre>
 *
 * <p>The specification requires a full date-time including the time zone, so prefer the variants
 * taking an {@code OffsetDateTime}, {@code ZonedDateTime} or {@code Instant}.
 */
public class Created extends QueryParam {

  public static final String PARAM_NAME_SINCE = "created_since";

  public static final String PARAM_NAME_UNTIL = "created_until";

  /**
   * Objects created since the given date-time, e.g. {@code "2024-01-01T00:00:00+01:00"}. The
   * value is URL-encoded, so pass it unencoded.
   */
  public static Created since(String date) {
    return new Created(PARAM_NAME_SINCE, date);
  }

  /** Objects created since the given date-time, sent with its offset. */
  public static Created since(OffsetDateTime date) {
    return Created.since(formatDateTime(date));
  }

  /** Objects created since the given date-time, sent with the offset of its time zone. */
  public static Created since(ZonedDateTime date) {
    return Created.since(formatDateTime(date));
  }

  /** Objects created since the given instant, sent in UTC. */
  public static Created since(Instant date) {
    return Created.since(formatDateTime(date));
  }

  /**
   * Objects created until the given date-time, e.g. {@code "2024-01-01T00:00:00+01:00"}. The
   * value is URL-encoded, so pass it unencoded.
   */
  public static Created until(String date) {
    return new Created(PARAM_NAME_UNTIL, date);
  }

  /** Objects created until the given date-time, sent with its offset. */
  public static Created until(OffsetDateTime date) {
    return Created.until(formatDateTime(date));
  }

  /** Objects created until the given date-time, sent with the offset of its time zone. */
  public static Created until(ZonedDateTime date) {
    return Created.until(formatDateTime(date));
  }

  /** Objects created until the given instant, sent in UTC. */
  public static Created until(Instant date) {
    return Created.until(formatDateTime(date));
  }

  Created(String name, String value) {
    super(name, value);
  }
}
