package com.sitepark.oparlclient.core.query;

/**
 * Asks the server to omit internal lists, e.g. {@code auxiliaryFile} of papers or {@code
 * agendaItem} of meetings, via the URL parameter {@code omit_internal}. Reduces the size of list
 * pages considerably, e.g. when updating a local copy.
 */
public class OmitInternal extends QueryParam {

  public static final String PARAM_NAME = "omit_internal";

  public static final OmitInternal TRUE = new OmitInternal(PARAM_NAME, "true");

  public static final OmitInternal FALSE = new OmitInternal(PARAM_NAME, "false");

  OmitInternal(String name, String value) {
    super(name, value);
  }
}
