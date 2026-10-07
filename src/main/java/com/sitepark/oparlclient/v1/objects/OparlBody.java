package com.sitepark.oparlclient.v1.objects;

import com.sitepark.oparlclient.core.OparlList;
import com.sitepark.oparlclient.core.OparlReference;
import com.sitepark.oparlclient.internal.OparlDateTime;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Der Objekttyp oparl:Body dient dazu, eine Körperschaft zu repräsentieren. Eine Körperschaft ist
 * in den meisten Fällen eine Gemeinde, eine Stadt oder ein Landkreis. In der Regel sind auf einem
 * OParl-Server Daten von genau einer Körperschaft gespeichert und es wird daher auch nur ein
 * Body-Objekt ausgegeben. Sind auf dem Server jedoch Daten von mehreren Körperschaften gespeichert,
 * <b>muss</b> für jede Körperschaft ein eigenes Body-Objekt ausgegeben werden.
 */
public class OparlBody extends OparlObjectV1 {
  /**
   * <b>ZWINGEND</b> Link zur Objektliste mit allen Dateien der Körperschaft. Neu in OParl 1.1.
   */
  private OparlReference<OparlList<OparlFile>> file;

  /**
   * Der zwölfstellige Regionalschlüssel.
   */
  private String rgs;

  /**
   * Zeitpunkt, seit dem die unter {@code license} angegebene Lizenz gilt. <i>Vorsicht bei
   * Änderungen der Lizenz die zu restriktiveren Bedingungen führen!</i>
   */
  @OparlDateTime private OffsetDateTime licenseValidSince;

  /**
   * Ort, an dem die Körperschaft beheimatet ist.
   */
  private OparlLocation location;

  /**
   * Allgemeine Website der Körperschaft.
   */
  private URI website;

  /**
   * Art der Körperschaft.
   */
  private String classification;

  /**
   * Link zur Objektliste mit allen Drucksachen der Körperschaft.
   */
  private OparlReference<OparlList<OparlPaper>> paper;

  /**
   * <b>ZWINGEND</b> Link zur Objektliste mit allen Beratungen der Körperschaft. Neu in OParl 1.1.
   */
  private OparlReference<OparlList<OparlConsultation>> consultation;

  /**
   * <b>ZWINGEND</b> Link zur Objektliste mit allen Ortsangaben der Körperschaft. Neu in OParl 1.1.
   */
  private OparlReference<OparlList<OparlLocation>> locationList;

  /**
   * <b>ZWINGEND</b> Link zur Objektliste mit allen Mitgliedschaften der Körperschaft. Neu in OParl
   * 1.1.
   */
  private OparlReference<OparlList<OparlMembership>> membership;

  /**
   * Zeitpunkt, ab dem OParl für dieses Body bereitgestellt wurde. Dies hilft, um die Datenqualität
   * einzuschätzen, denn erst ab der Einrichtung für OParl kann sichergestellt werden, dass
   * sämtliche Werte korrekt in der Original-Quelle vorliegen.
   */
  @OparlDateTime private OffsetDateTime oparlSince;

  /**
   * System, zu dem dieses Objekt gehört.
   */
  private OparlReference<OparlSystem> system;

  /**
   * Name oder Bezeichnung der mit {@code contactEmail} erreichbaren Stelle.
   */
  private String contactName;

  /**
   * Link zur Objektliste mit allen Sitzungen der Körperschaft.
   */
  private OparlReference<OparlList<OparlMeeting>> meeting;

  /**
   * Objektliste mit den Wahlperioden der Körperschaft.
   */
  private List<OparlLegislativeTerm> legislativeTerm;

  /**
   * Link zur Objektliste mit allen Personen der Körperschaft.
   */
  private OparlReference<OparlList<OparlPerson>> person;

  /**
   * <b>ZWINGEND</b> Link zur Objektliste mit allen Legislaturperioden der Körperschaft. Neu in
   * OParl 1.1. Die externe Objektliste enthält die gleichen Objekte wie {@code legislativeTerm}
   */
  private OparlReference<OparlList<OparlLegislativeTerm>> legislativeTermList;

  /**
   * Der achtstellige Amtliche Gemeindeschlüssel (Amtliche Gemeindeschlüssel können im <a
   * href="https://www.destatis.de/DE/ZahlenFakten/LaenderRegionen/Regionales/Gemeindeverzeichnis/Gemeindeverzeichnis.html">Gemeindeverzeichnis
   * (GV-ISys) des Statistischen Bundesamtes</a> eingesehen werden).
   */
  private String ags;

  /**
   * Link zur Objektliste mit allen Gruppierungen der Körperschaft.
   */
  private OparlReference<OparlList<OparlOrganization>> organization;

  /**
   * Der offizielle lange Name der Körperschaft.
   */
  private String name;

  /**
   * Kurzer Name der Körperschaft.
   */
  private String shortName;

  /**
   * Dient der Angabe einer Kontakt-E-Mail-Adresse. Die Adresse soll die Kontaktaufnahme zu einer
   * für die Körperschaft und idealerweise das parlamentarische Informationssystem zuständigen
   * Stelle ermöglichen.
   */
  private String contactEmail;

  /**
   * Dient der Angabe zusätzlicher URLs, die dieselbe Körperschaft repräsentieren. Hier können
   * beispielsweise der entsprechende Eintrag der gemeinsamen Normdatei der Deutschen
   * Nationalbibliothek (Gemeinsame Normdatei <a
   * href="http://www.dnb.de/gnd">http://www.dnb.de/gnd</a>), der DBPedia (DBPedia <a
   * href="http://www.dbpedia.org/">http://www.dbpedia.org/</a>) oder der Wikipedia (Wikipedia <a
   * href="http://de.wikipedia.org/">http://de.wikipedia.org/</a>) angegeben werden. Body- oder
   * System-Objekte mit anderen OParl-Versionen <b>dürfen nicht</b> Teil der Liste sein.
   */
  private List<URI> equivalent;

  /**
   * <b>ZWINGEND</b> Link zur Objektliste mit allen Tagesordnungspunkten der Körperschaft. Neu in
   * OParl 1.1.
   */
  private OparlReference<OparlList<OparlAgendaItem>> agendaItem;

  public OparlReference<OparlList<OparlFile>> getFile() {
    return this.file;
  }

  public String getRgs() {
    return this.rgs;
  }

  public OffsetDateTime getLicenseValidSince() {
    return this.licenseValidSince;
  }

  public OparlLocation getLocation() {
    return this.location;
  }

  public URI getWebsite() {
    return this.website;
  }

  public String getClassification() {
    return this.classification;
  }

  public OparlReference<OparlList<OparlPaper>> getPaper() {
    return this.paper;
  }

  public OparlReference<OparlList<OparlConsultation>> getConsultation() {
    return this.consultation;
  }

  public OparlReference<OparlList<OparlLocation>> getLocationList() {
    return this.locationList;
  }

  public OparlReference<OparlList<OparlMembership>> getMembership() {
    return this.membership;
  }

  public OffsetDateTime getOparlSince() {
    return this.oparlSince;
  }

  public OparlReference<OparlSystem> getSystem() {
    return this.system;
  }

  public String getContactName() {
    return this.contactName;
  }

  public OparlReference<OparlList<OparlMeeting>> getMeeting() {
    return this.meeting;
  }

  public List<OparlLegislativeTerm> getLegislativeTerm() {
    return this.legislativeTerm;
  }

  public OparlReference<OparlList<OparlPerson>> getPerson() {
    return this.person;
  }

  public OparlReference<OparlList<OparlLegislativeTerm>> getLegislativeTermList() {
    return this.legislativeTermList;
  }

  public String getAgs() {
    return this.ags;
  }

  public OparlReference<OparlList<OparlOrganization>> getOrganization() {
    return this.organization;
  }

  public String getName() {
    return this.name;
  }

  public String getShortName() {
    return this.shortName;
  }

  public String getContactEmail() {
    return this.contactEmail;
  }

  public List<URI> getEquivalent() {
    return this.equivalent;
  }

  public OparlReference<OparlList<OparlAgendaItem>> getAgendaItem() {
    return this.agendaItem;
  }

  public void setFile(OparlReference<OparlList<OparlFile>> file) {
    this.file = file;
  }

  public void setRgs(String rgs) {
    this.rgs = rgs;
  }

  public void setLicenseValidSince(OffsetDateTime licenseValidSince) {
    this.licenseValidSince = licenseValidSince;
  }

  public void setLocation(OparlLocation location) {
    this.location = location;
  }

  public void setWebsite(URI website) {
    this.website = website;
  }

  public void setClassification(String classification) {
    this.classification = classification;
  }

  public void setPaper(OparlReference<OparlList<OparlPaper>> paper) {
    this.paper = paper;
  }

  public void setConsultation(OparlReference<OparlList<OparlConsultation>> consultation) {
    this.consultation = consultation;
  }

  public void setLocationList(OparlReference<OparlList<OparlLocation>> locationList) {
    this.locationList = locationList;
  }

  public void setMembership(OparlReference<OparlList<OparlMembership>> membership) {
    this.membership = membership;
  }

  public void setOparlSince(OffsetDateTime oparlSince) {
    this.oparlSince = oparlSince;
  }

  public void setSystem(OparlReference<OparlSystem> system) {
    this.system = system;
  }

  public void setContactName(String contactName) {
    this.contactName = contactName;
  }

  public void setMeeting(OparlReference<OparlList<OparlMeeting>> meeting) {
    this.meeting = meeting;
  }

  public void setLegislativeTerm(List<OparlLegislativeTerm> legislativeTerm) {
    this.legislativeTerm = legislativeTerm;
  }

  public void setPerson(OparlReference<OparlList<OparlPerson>> person) {
    this.person = person;
  }

  public void setLegislativeTermList(
      OparlReference<OparlList<OparlLegislativeTerm>> legislativeTermList) {
    this.legislativeTermList = legislativeTermList;
  }

  public void setAgs(String ags) {
    this.ags = ags;
  }

  public void setOrganization(OparlReference<OparlList<OparlOrganization>> organization) {
    this.organization = organization;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  public void setContactEmail(String contactEmail) {
    this.contactEmail = contactEmail;
  }

  public void setEquivalent(List<URI> equivalent) {
    this.equivalent = equivalent;
  }

  public void setAgendaItem(OparlReference<OparlList<OparlAgendaItem>> agendaItem) {
    this.agendaItem = agendaItem;
  }
}
