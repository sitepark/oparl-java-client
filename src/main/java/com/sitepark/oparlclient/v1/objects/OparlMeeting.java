package com.sitepark.oparlclient.v1.objects;

import com.sitepark.oparlclient.core.OparlReference;
import com.sitepark.oparlclient.internal.OparlDateTime;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Eine Sitzung ist die Versammlung einer oder mehrerer Gruppierungen (oparl:Organization) zu einem
 * bestimmten Zeitpunkt an einem bestimmten Ort.
 *
 * <p>Die geladenen Teilnehmer der Sitzung sind jeweils als Objekte vom Typ oparl:Person, die in
 * entsprechender Form referenziert werden. Verschiedene Dateien (Einladung, Ergebnis- und
 * Wortprotokoll, sonstige Anlagen) können referenziert werden. Die Inhalte einer Sitzung werden
 * durch Tagesordnungspunkte (oparl:AgendaItem) abgebildet.
 */
public class OparlMeeting extends OparlObjectV1 {
  /**
   * Wenn die Sitzung ausfällt, wird cancelled auf true gesetzt.
   */
  private Boolean cancelled;

  /**
   * Datum und Uhrzeit des Anfangszeitpunkts der Sitzung. Bei einer zukünftigen Sitzung ist dies der
   * geplante Zeitpunkt, bei einer stattgefundenen <b>kann</b> es der tatsächliche Startzeitpunkt
   * sein.
   */
  @OparlDateTime private OffsetDateTime start;

  /**
   * Ergebnisprotokoll zur Sitzung. Diese Eigenschaft kann selbstverständlich erst nachdem
   * Stattfinden der Sitzung vorkommen.
   */
  private OparlFile resultsProtocol;

  /**
   * Endzeitpunkt der Sitzung als Datum/Uhrzeit. Bei einer zukünftigen Sitzung ist dies der geplante
   * Zeitpunkt, bei einer stattgefundenen <b>kann</b> es der tatsächliche Endzeitpunkt sein.
   */
  @OparlDateTime private OffsetDateTime end;

  /**
   * Gruppierungen, denen die Sitzung zugeordnet ist. Im Regelfall wird hier eine Gruppierung
   * verknüpft sein, es kann jedoch auch gemeinsame Sitzungen mehrerer Gruppierungen geben. Das
   * erste Element <b>sollte</b> dann das federführende Gremium sein.
   */
  private List<OparlReference<OparlOrganization>> organization;

  /**
   * Einladungsdokument zur Sitzung.
   */
  private OparlFile invitation;

  /**
   * Wortprotokoll zur Sitzung. Diese Eigenschaft kann selbstverständlich erst nach dem Stattfinden
   * der Sitzung vorkommen.
   */
  private OparlFile verbatimProtocol;

  /**
   * Tagesordnungspunkte der Sitzung. Die Reihenfolge ist relevant. Es kann Sitzungen ohne TOPs
   * geben.
   */
  private List<OparlAgendaItem> agendaItem;

  /**
   * Personen, die an der Sitzung teilgenommen haben (d.h. nicht nur die eingeladenen Personen,
   * sondern die tatsächlich anwesenden). Diese Eigenschaft kann selbstverständlich erst nach dem
   * Stattfinden der Sitzung vorkommen.
   */
  private List<OparlReference<OparlPerson>> participant;

  /**
   * Aktueller Status der Sitzung. <b>Empfohlen</b> ist die Verwendung von {@code terminiert}
   * (geplant), {@code eingeladen} (vor der Sitzung bis zur Freigabe des Protokolls) und {@code
   * durchgeführt} (nach Freigabe des Protokolls).
   */
  private String meetingState;

  /**
   * Dateianhang zur Sitzung. Hiermit sind Dateien gemeint, die üblicherweise mit der Einladung zu
   * einer Sitzung verteilt werden, und die nicht bereits über einzelne Tagesordnungspunkte
   * referenziert sind.
   */
  private List<OparlFile> auxiliaryFile;

  /**
   * Name der Sitzung.
   */
  private String name;

  /**
   * Sitzungsort.
   */
  private OparlLocation location;

  public Boolean getCancelled() {
    return this.cancelled;
  }

  public OffsetDateTime getStart() {
    return this.start;
  }

  public OparlFile getResultsProtocol() {
    return this.resultsProtocol;
  }

  public OffsetDateTime getEnd() {
    return this.end;
  }

  public List<OparlReference<OparlOrganization>> getOrganization() {
    return this.organization;
  }

  public OparlFile getInvitation() {
    return this.invitation;
  }

  public OparlFile getVerbatimProtocol() {
    return this.verbatimProtocol;
  }

  public List<OparlAgendaItem> getAgendaItem() {
    return this.agendaItem;
  }

  public List<OparlReference<OparlPerson>> getParticipant() {
    return this.participant;
  }

  public String getMeetingState() {
    return this.meetingState;
  }

  public List<OparlFile> getAuxiliaryFile() {
    return this.auxiliaryFile;
  }

  public String getName() {
    return this.name;
  }

  public OparlLocation getLocation() {
    return this.location;
  }

  public void setCancelled(Boolean cancelled) {
    this.cancelled = cancelled;
  }

  public void setStart(OffsetDateTime start) {
    this.start = start;
  }

  public void setResultsProtocol(OparlFile resultsProtocol) {
    this.resultsProtocol = resultsProtocol;
  }

  public void setEnd(OffsetDateTime end) {
    this.end = end;
  }

  public void setOrganization(List<OparlReference<OparlOrganization>> organization) {
    this.organization = organization;
  }

  public void setInvitation(OparlFile invitation) {
    this.invitation = invitation;
  }

  public void setVerbatimProtocol(OparlFile verbatimProtocol) {
    this.verbatimProtocol = verbatimProtocol;
  }

  public void setAgendaItem(List<OparlAgendaItem> agendaItem) {
    this.agendaItem = agendaItem;
  }

  public void setParticipant(List<OparlReference<OparlPerson>> participant) {
    this.participant = participant;
  }

  public void setMeetingState(String meetingState) {
    this.meetingState = meetingState;
  }

  public void setAuxiliaryFile(List<OparlFile> auxiliaryFile) {
    this.auxiliaryFile = auxiliaryFile;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setLocation(OparlLocation location) {
    this.location = location;
  }
}
