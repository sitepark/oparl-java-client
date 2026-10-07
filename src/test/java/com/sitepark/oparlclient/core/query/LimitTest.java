package com.sitepark.oparlclient.core.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sitepark.oparlclient.core.OparlReference;
import java.net.URI;
import org.junit.jupiter.api.Test;

class LimitTest {

  @Test
  void createsLimitParameter() {
    Limit limit = Limit.of(100);

    assertEquals("limit", limit.getName());
    assertEquals("100", limit.getValue());
    assertEquals("limit=100", limit.toString());
  }

  @Test
  void combinesWithOtherParameters() {
    OparlReference<Object> reference =
        new OparlReference<>(URI.create("https://oparl.example.org/papers"), null, null);

    assertEquals(
        "https://oparl.example.org/papers?omit_internal=true&limit=20",
        reference.withQueryParams(OmitInternal.TRUE, Limit.of(20)).getUri().toString());
  }

  @Test
  void rejectsNonPositiveLimit() {
    assertThrows(IllegalArgumentException.class, () -> Limit.of(0));
    assertThrows(IllegalArgumentException.class, () -> Limit.of(-1));
  }
}
