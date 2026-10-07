package com.sitepark.oparlclient.v1.objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sitepark.oparlclient.OparlClient;
import java.util.List;
import org.junit.jupiter.api.Test;

class CommonPropertiesTest {

  private static final List<TypeReference<? extends OparlObjectV1>> TYPES =
      List.of(
          new TypeReference<OparlAgendaItem>() {},
          new TypeReference<OparlBody>() {},
          new TypeReference<OparlConsultation>() {},
          new TypeReference<OparlFile>() {},
          new TypeReference<OparlLegislativeTerm>() {},
          new TypeReference<OparlLocation>() {},
          new TypeReference<OparlMeeting>() {},
          new TypeReference<OparlMembership>() {},
          new TypeReference<OparlOrganization>() {},
          new TypeReference<OparlPaper>() {},
          new TypeReference<OparlPerson>() {},
          new TypeReference<OparlSystem>() {});

  private final OparlClient client = new OparlClient();

  @Test
  void readsKeywordOfEveryObjectType() throws Exception {
    for (TypeReference<? extends OparlObjectV1> type : TYPES) {
      OparlObjectV1 object =
          this.client.deserializeJson("{\"keyword\":[\"haushalt\",\"finanzen\"]}", type);

      assertEquals(List.of("haushalt", "finanzen"), object.getKeyword(), type.getType().toString());
      assertNull(object.getAdditionalProperty("keyword"), type.getType().toString());
    }
  }

  @Test
  void readsLicenseOfEveryObjectTypeAsString() throws Exception {
    for (TypeReference<? extends OparlObjectV1> type : TYPES) {
      OparlObjectV1 object =
          this.client.deserializeJson(
              "{\"license\":\"https://creativecommons.org/licenses/by/4.0/\"}", type);

      assertEquals(
          "https://creativecommons.org/licenses/by/4.0/",
          object.getLicense(),
          type.getType().toString());
    }
  }

  @Test
  void keepsLicenseThatIsNoUrl() throws Exception {
    OparlPaper paper =
        this.client.deserializeJson(
            "{\"license\":\"Nutzung frei, Quelle: Stadt Beispiel\"}",
            new TypeReference<OparlPaper>() {});

    assertEquals("Nutzung frei, Quelle: Stadt Beispiel", paper.getLicense());
  }
}
