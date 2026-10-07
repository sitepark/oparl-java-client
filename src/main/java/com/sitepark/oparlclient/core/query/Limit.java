package com.sitepark.oparlclient.core.query;

/**
 * Requests a maximum number of objects per list page via the URL parameter {@code limit}.
 *
 * <p>According to the OParl specification, a server is not obliged to respect it, so clients must
 * not rely on the page size.
 */
public class Limit extends QueryParam {

  public static final String PARAM_NAME = "limit";

  /**
   * @param limit the maximum number of objects per page
   * @throws IllegalArgumentException if the limit is zero or negative
   */
  public static Limit of(int limit) {
    if (limit <= 0) {
      throw new IllegalArgumentException("limit must be positive: " + limit);
    }
    return new Limit(PARAM_NAME, Integer.toString(limit));
  }

  Limit(String name, String value) {
    super(name, value);
  }
}
