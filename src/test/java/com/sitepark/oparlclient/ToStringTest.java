package com.sitepark.oparlclient;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sitepark.oparlclient.core.OparlList;
import com.sitepark.oparlclient.core.OparlReference;
import com.sitepark.oparlclient.v1.objects.OparlBody;
import com.sitepark.oparlclient.v1.objects.OparlMeeting;
import java.net.URI;
import org.junit.jupiter.api.Test;

class ToStringTest {

  private final OparlClient client = new OparlClient();

  @Test
  void describesObjectByTypeAndId() throws Exception {
    OparlBody body =
        this.client.deserializeJson(
            "{\"id\":\"https://oparl.example.org/body/1\",\"name\":\"Stadt\"}",
            new TypeReference<OparlBody>() {});

    assertEquals("OparlBody[https://oparl.example.org/body/1]", body.toString());
  }

  @Test
  void marksDeletedObjects() throws Exception {
    OparlMeeting meeting =
        this.client.deserializeJson(
            "{\"id\":\"https://oparl.example.org/meeting/1\",\"deleted\":true}",
            new TypeReference<OparlMeeting>() {});

    assertEquals("OparlMeeting[https://oparl.example.org/meeting/1, deleted]", meeting.toString());
  }

  @Test
  void describesReferenceByUri() {
    OparlReference<OparlBody> reference =
        new OparlReference<>(URI.create("https://oparl.example.org/body/1"), null, null);

    assertEquals("OparlReference[https://oparl.example.org/body/1]", reference.toString());
  }

  @Test
  void describesListPage() throws Exception {
    OparlList<OparlBody> page =
        this.client.deserializeJson(
            "{\"data\":[{},{}],\"links\":{\"next\":\"https://oparl.example.org/bodies?page=2\"}}",
            new TypeReference<OparlList<OparlBody>>() {});

    assertEquals(
        "OparlList[2 elements, next=https://oparl.example.org/bodies?page=2]", page.toString());
  }
}
