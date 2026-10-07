package com.sitepark.oparlclient.v1.objects;

import com.sitepark.oparlclient.core.OparlReference;
import com.sitepark.oparlclient.internal.OparlDate;
import java.time.LocalDate;

/**
 * Über Objekte dieses Typs wird die Mitgliedschaft von Personen in Gruppierungen dargestellt. Diese
 * Mitgliedschaften können zeitlich begrenzt sein. Zudem kann abgebildet werden, dass eine Person
 * eine bestimmte Rolle bzw. Position innerhalb der Gruppierung innehat, beispielsweise den Vorsitz
 * einer Fraktion.
 */
public class OparlMembership extends OparlObjectV1 {
  /**
   * Gibt an, ob die Person in der Gruppierung stimmberechtigtes Mitglied ist.
   */
  private Boolean votingRight;

  /**
   * Datum, an dem die Mitgliedschaft endet.
   */
  @OparlDate private LocalDate endDate;

  /**
   * Rolle der Person für die Gruppierung. Kann genutzt werden, um verschiedene Arten von
   * Mitgliedschaften zum Beispiel in Gremien zu unterscheiden.
   */
  private String role;

  /**
   * Die Gruppierung, für die die Person in der unter {@code organization} angegebenen Organisation
   * sitzt. Beispiel: Mitgliedschaft als Vertreter einer Ratsfraktion, einer Gruppierung oder einer
   * externen Organisation.
   */
  private OparlReference<OparlOrganization> onBehalfOf;

  /**
   * Die Gruppierung, in der die Person Mitglied ist oder war.
   */
  private OparlReference<OparlOrganization> organization;

  /**
   * Rückreferenz auf Person, welches nur dann ausgegeben werden muss, wenn das Membership-Objekt
   * einzeln abgerufen wird, d.h. nicht Teil einer internen Ausgabe ist.
   */
  private OparlReference<OparlPerson> person;

  /**
   * Datum, an dem die Mitgliedschaft beginnt.
   */
  @OparlDate private LocalDate startDate;

  public Boolean getVotingRight() {
    return this.votingRight;
  }

  public LocalDate getEndDate() {
    return this.endDate;
  }

  public String getRole() {
    return this.role;
  }

  public OparlReference<OparlOrganization> getOnBehalfOf() {
    return this.onBehalfOf;
  }

  public OparlReference<OparlOrganization> getOrganization() {
    return this.organization;
  }

  public OparlReference<OparlPerson> getPerson() {
    return this.person;
  }

  public LocalDate getStartDate() {
    return this.startDate;
  }

  public void setVotingRight(Boolean votingRight) {
    this.votingRight = votingRight;
  }

  public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public void setOnBehalfOf(OparlReference<OparlOrganization> onBehalfOf) {
    this.onBehalfOf = onBehalfOf;
  }

  public void setOrganization(OparlReference<OparlOrganization> organization) {
    this.organization = organization;
  }

  public void setPerson(OparlReference<OparlPerson> person) {
    this.person = person;
  }

  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }
}
