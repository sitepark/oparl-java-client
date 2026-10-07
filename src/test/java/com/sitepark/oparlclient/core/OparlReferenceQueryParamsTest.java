package com.sitepark.oparlclient.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.sitepark.oparlclient.core.query.Created;
import com.sitepark.oparlclient.core.query.Modified;
import com.sitepark.oparlclient.core.query.OmitInternal;
import com.sitepark.oparlclient.core.query.QueryParam;
import java.net.URI;
import org.junit.jupiter.api.Test;

class OparlReferenceQueryParamsTest {

  private static String withParams(String uri, QueryParam... params) {
    return new OparlReference<Object>(URI.create(uri), null, null)
        .withQueryParams(params)
        .getUri()
        .toString();
  }

  @Test
  void encodesPlusOfTimezoneOffset() {
    assertEquals(
        "https://oparl.example.org/papers?modified_since=2014-01-01T02:00:00%2B01:00",
        withParams(
            "https://oparl.example.org/papers", Modified.since("2014-01-01T02:00:00+01:00")));
  }

  @Test
  void encodesPercentSignOfValuesPassedEncoded() {
    assertEquals(
        "https://oparl.example.org/papers?q=2014-01-01T00%253A00%253A00%252B01%253A00",
        withParams(
            "https://oparl.example.org/papers",
            new QueryParam("q", "2014-01-01T00%3A00%3A00%2B01%3A00")));
  }

  @Test
  void encodesPercentWithoutEscapeSequence() {
    assertEquals(
        "https://oparl.example.org/papers?q=100%25",
        withParams("https://oparl.example.org/papers", new QueryParam("q", "100%")));
  }

  @Test
  void encodesReservedAndNonAsciiCharacters() {
    assertEquals(
        "https://oparl.example.org/papers?q=a%26b%3Dc%23d%20%C3%A4",
        withParams("https://oparl.example.org/papers", new QueryParam("q", "a&b=c#d ä")));
  }

  @Test
  void keepsExistingEncodedQueryUnchanged() {
    assertEquals(
        "https://oparl.example.org/papers?page=2&modified_since=2014-01-01T02%3A00%3A00%2B01%3A00"
            + "&omit_internal=true",
        withParams(
            "https://oparl.example.org/papers?page=2&modified_since=2014-01-01T02%3A00%3A00%2B01%3A00",
            OmitInternal.TRUE));
  }

  @Test
  void keepsFragment() {
    assertEquals(
        "https://oparl.example.org/papers?page=2&omit_internal=true#top",
        withParams("https://oparl.example.org/papers?page=2#top", OmitInternal.TRUE));
  }

  @Test
  void handlesEmptyQuery() {
    assertEquals(
        "https://oparl.example.org/papers?omit_internal=true",
        withParams("https://oparl.example.org/papers?", OmitInternal.TRUE));
  }

  @Test
  void skipsNullParams() {
    assertEquals(
        "https://oparl.example.org/papers?omit_internal=true",
        withParams("https://oparl.example.org/papers", null, OmitInternal.TRUE, null));
  }

  @Test
  void keepsUriWithoutParams() {
    assertEquals(
        "https://oparl.example.org/papers?page=2",
        withParams("https://oparl.example.org/papers?page=2"));
  }

  @Test
  void joinsMultipleParams() {
    assertEquals(
        "created_since=2014-01-01T00:00:00%2B01:00&created_until=2014-01-31T23:59:59%2B01:00",
        QueryParam.toString(
            Created.since("2014-01-01T00:00:00+01:00"),
            Created.until("2014-01-31T23:59:59+01:00")));
  }
}
