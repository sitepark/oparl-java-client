package com.sitepark.oparlclient.v1.objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class OparlTypesTest {

  @Test
  void mapsTypesOfOparl11() {
    assertEquals(OparlBody.class, OparlTypes.classOf("https://schema.oparl.org/1.1/Body"));
    assertEquals(
        OparlAgendaItem.class, OparlTypes.classOf("https://schema.oparl.org/1.1/AgendaItem"));
    assertEquals(OparlSystem.class, OparlTypes.classOf("https://schema.oparl.org/1.1/System"));
  }

  @Test
  void mapsTypesOfOparl10AndVariants() {
    assertEquals(OparlPaper.class, OparlTypes.classOf("https://schema.oparl.org/1.0/Paper"));
    assertEquals(OparlPaper.class, OparlTypes.classOf("http://schema.oparl.org/1.1/Paper"));
    assertEquals(OparlPaper.class, OparlTypes.classOf("https://schema.oparl.org/1.1/Paper/"));
  }

  @Test
  void returnsNullForUnknownTypes() {
    assertNull(OparlTypes.classOf(null));
    assertNull(OparlTypes.classOf("https://schema.oparl.org/1.1/Error"));
    assertNull(OparlTypes.classOf("https://schema.oparl.org/2.0/Body"));
    assertNull(OparlTypes.classOf("Body"));
  }

  @Test
  void returnsTypeOfClass() {
    assertEquals("https://schema.oparl.org/1.1/Meeting", OparlTypes.typeOf(OparlMeeting.class));
    assertNull(OparlTypes.typeOf(OparlObjectV1.class));
    assertNull(OparlTypes.typeOf(String.class));
  }
}
