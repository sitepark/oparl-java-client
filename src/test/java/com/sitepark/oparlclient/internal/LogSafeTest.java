package com.sitepark.oparlclient.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class LogSafeTest {

  @Test
  void replacesLineBreaksAndControlCharacters() {
    assertEquals("a?b?c?d?e?f", LogSafe.of("a\nb\rc\td\u0000e\u007Ff"));
  }

  @Test
  void keepsPrintableCharacters() {
    assertEquals(
        "https://oparl.example.org/Körperschaft a b",
        LogSafe.of("https://oparl.example.org/Körperschaft a b"));
  }

  @Test
  void keepsNull() {
    assertNull(LogSafe.of(null));
  }
}
