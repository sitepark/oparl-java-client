package com.sitepark.oparlclient.core.query;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

/**
 * Filters a list by the {@code modified} property of its elements, via the URL parameters {@code
 * modified_since} and {@code modified_until}:
 *
 * <pre>{@code
 * body.getPaper().withQueryParams(Modified.since(Instant.parse("2024-01-01T00:00:00Z"))).get();
 * }</pre>
 *
 * <p>The specification requires a full date-time including the time zone, so prefer the variants
 * taking an {@code OffsetDateTime}, {@code ZonedDateTime} or {@code Instant}.
 */
public class Modified extends QueryParam {

  public static final String PARAM_NAME_SINCE = "modified_since";

  public static final String PARAM_NAME_UNTIL = "modified_until";

  /**
   * Objects modified since the given date-time, e.g. {@code "2024-01-01T00:00:00+01:00"}. The
   * value is URL-encoded, so pass it unencoded.
   */
  public static Modified since(String date) {
    return new Modified(PARAM_NAME_SINCE, date);
  }

  /** Objects modified since the given date-time, sent with its offset. */
  public static Modified since(OffsetDateTime date) {
    return Modified.since(formatDateTime(date));
  }

  /** Objects modified since the given date-time, sent with the offset of its time zone. */
  public static Modified since(ZonedDateTime date) {
    return Modified.since(formatDateTime(date));
  }

  /** Objects modified since the given instant, sent in UTC. */
  public static Modified since(Instant date) {
    return Modified.since(formatDateTime(date));
  }

  /**
   * Objects modified until the given date-time, e.g. {@code "2024-01-01T00:00:00+01:00"}. The
   * value is URL-encoded, so pass it unencoded.
   */
  public static Modified until(String date) {
    return new Modified(PARAM_NAME_UNTIL, date);
  }

  /** Objects modified until the given date-time, sent with its offset. */
  public static Modified until(OffsetDateTime date) {
    return Modified.until(formatDateTime(date));
  }

  /** Objects modified until the given date-time, sent with the offset of its time zone. */
  public static Modified until(ZonedDateTime date) {
    return Modified.until(formatDateTime(date));
  }

  /** Objects modified until the given instant, sent in UTC. */
  public static Modified until(Instant date) {
    return Modified.until(formatDateTime(date));
  }

  Modified(String name, String value) {
    super(name, value);
  }
}
