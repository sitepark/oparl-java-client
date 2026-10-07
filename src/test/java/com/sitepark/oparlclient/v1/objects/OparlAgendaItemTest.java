package com.sitepark.oparlclient.v1.objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitepark.oparlclient.OparlClient;
import org.junit.jupiter.api.Test;

class OparlAgendaItemTest {

  private final OparlClient client = new OparlClient();

  @Test
  void deserializesPublicTrue() throws Exception {
    OparlAgendaItem agendaItem =
        this.client.deserializeJson("{\"public\":true}", new TypeReference<OparlAgendaItem>() {});

    assertEquals(Boolean.TRUE, agendaItem.getPublic());
  }

  @Test
  void deserializesPublicFalse() throws Exception {
    OparlAgendaItem agendaItem =
        this.client.deserializeJson("{\"public\":false}", new TypeReference<OparlAgendaItem>() {});

    assertEquals(Boolean.FALSE, agendaItem.getPublic());
  }

  @Test
  void serializesPublicUnderSpecName() throws Exception {
    OparlAgendaItem agendaItem = new OparlAgendaItem();
    agendaItem.setPublic(true);

    String json = new ObjectMapper().writeValueAsString(agendaItem);

    assertTrue(json.contains("\"public\":true"), json);
    assertFalse(json.contains("publicField"), json);
  }

  @Test
  void isNullIfNotSent() throws Exception {
    OparlAgendaItem agendaItem =
        this.client.deserializeJson("{}", new TypeReference<OparlAgendaItem>() {});

    assertNull(agendaItem.getPublic());
    assertNull(agendaItem.getOrder());
  }
}
