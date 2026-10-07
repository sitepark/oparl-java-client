package com.sitepark.oparlclient.v1.objects;

import com.sitepark.oparlclient.core.OparlReference;
import java.util.List;

/**
 * Der Objekttyp {@code oparl:Consultation} dient dazu, die Beratung einer Drucksache ({@code
 * oparl:Paper}) in einer Sitzung abzubilden. Dabei ist es nicht entscheidend, ob diese Beratung in
 * der Vergangenheit stattgefunden hat oder diese für die Zukunft geplant ist. Die Gesamtheit aller
 * Objekte des Typs {@code oparl:Consultation} zu einer bestimmten Drucksache bildet das ab, was in
 * der Praxis als "Beratungsfolge" der Drucksache bezeichnet wird.
 */
public class OparlConsultation extends OparlObjectV1 {
  /**
   * Rolle oder Funktion der Beratung. Zum Beispiel Anhörung, Entscheidung, Kenntnisnahme,
   * Vorberatung usw.
   */
  private String role;

  /**
   * Gremium, in dem die Drucksache beraten wird. Hier kann auch eine mit Liste von Gremien
   * angegeben werden (die verschiedenen {@code oparl:Body} und {@code oparl:System} angehören
   * können). Die Liste ist dann geordnet. Das erste Gremium der Liste ist federführend.
   */
  private List<OparlReference<OparlOrganization>> organization;

  /**
   * Referenz auf das Paper, welche nur dann ausgegeben werden muss, wenn das Consultation-Objekt
   * einzeln abgerufen wird, d.h. nicht Teil einer internen Ausgabe ist.
   */
  private OparlReference<OparlPaper> paper;

  /**
   * Referenz auf den Tagesordnungspunkt, unter dem die Drucksache beraten wird, welcher nur dann
   * ausgegeben werden muss, wenn das Consultation-Objekt einzeln abgerufen wird, d.h. nicht Teil
   * einer internen Ausgabe ist.
   */
  private OparlReference<OparlAgendaItem> agendaItem;

  /**
   * Referenz auf die Sitzung, in der die Drucksache beraten wird oder wurde, welche nur dann
   * ausgegeben werden muss, wenn das Consultation-Objekt einzeln abgerufen wird, d.h. nicht Teil
   * einer internen Ausgabe ist.
   */
  private OparlReference<OparlMeeting> meeting;

  /**
   * Drückt aus, ob bei dieser Beratung ein Beschluss zu der Drucksache gefasst wird oder wurde
   * ({@code true}) oder nicht ({@code false}).
   */
  private Boolean authoritative;

  public String getRole() {
    return this.role;
  }

  public List<OparlReference<OparlOrganization>> getOrganization() {
    return this.organization;
  }

  public OparlReference<OparlPaper> getPaper() {
    return this.paper;
  }

  public OparlReference<OparlAgendaItem> getAgendaItem() {
    return this.agendaItem;
  }

  public OparlReference<OparlMeeting> getMeeting() {
    return this.meeting;
  }

  public Boolean getAuthoritative() {
    return this.authoritative;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public void setOrganization(List<OparlReference<OparlOrganization>> organization) {
    this.organization = organization;
  }

  public void setPaper(OparlReference<OparlPaper> paper) {
    this.paper = paper;
  }

  public void setAgendaItem(OparlReference<OparlAgendaItem> agendaItem) {
    this.agendaItem = agendaItem;
  }

  public void setMeeting(OparlReference<OparlMeeting> meeting) {
    this.meeting = meeting;
  }

  public void setAuthoritative(Boolean authoritative) {
    this.authoritative = authoritative;
  }
}
