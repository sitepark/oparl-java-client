package com.sitepark.oparlclient.v1.objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.sitepark.oparlclient.core.OparlReference;
import java.util.List;

/**
 * Dieser Objekttyp dient dazu, einen Ortsbezug formal abzubilden. Ortsangaben können sowohl aus
 * Textinformationen bestehen (beispielsweise dem Namen einer Straße/eines Platzes oder eine genaue
 * Adresse) als auch aus Geodaten. Ortsangaben sind auch nicht auf einzelne Positionen beschränkt,
 * sondern können eine Vielzahl von Positionen, Flächen, Strecken etc. abdecken.
 */
public class OparlLocation extends OparlObjectV1 {
  /**
   * Geodaten-Repräsentation des Orts. Der Wert dieser Eigenschaft <b>muss</b> der Spezifikation von
   * GeoJSON entsprechen, d.h. es <b>muss</b> ein vollständiges {@code Feature}-Objekt ausgegeben
   * werden.
   */
  private JsonNode geojson;

  /**
   * Untergeordnete Ortsangabe der Anschrift, z.B. Stadtbezirk, Ortsteil oder Dorf.
   */
  private String subLocality;

  /**
   * Rückreferenzen auf Body-Objekte. Wird nur dann ausgegeben, wenn das Location-Objekt nicht als
   * eingebettetes Objekt aufgerufen wird.
   */
  private List<OparlReference<OparlBody>> bodies;

  /**
   * Postleitzahl der Anschrift.
   */
  private String postalCode;

  /**
   * Rückreferenzen auf Person-Objekte. Wird nur dann ausgegeben, wenn das Location-Objekt nicht als
   * eingebettetes Objekt aufgerufen wird.
   */
  private List<OparlReference<OparlPerson>> persons;

  /**
   * Raumangabe der Anschrift
   */
  private String room;

  /**
   * Straße und Hausnummer der Anschrift.
   */
  private String streetAddress;

  /**
   * Ortsangabe der Anschrift.
   */
  private String locality;

  /**
   * Rückreferenzen auf Paper-Objekte. Wird nur dann ausgegeben, wenn das Location-Objekt nicht als
   * eingebettetes Objekt aufgerufen wird.
   */
  private List<OparlReference<OparlPaper>> papers;

  /**
   * Rückreferenzen auf Organization-Objekte. Wird nur dann ausgegeben, wenn das Location-Objekt
   * nicht als eingebettetes Objekt aufgerufen wird.
   */
  private List<OparlReference<OparlOrganization>> organizations;

  /**
   * Textuelle Beschreibung eines Orts, z. B. in Form einer Adresse.
   */
  private String description;

  /**
   * Rückreferenzen auf Meeting-Objekte. Wird nur dann ausgegeben, wenn das Location-Objekt nicht
   * als eingebettetes Objekt aufgerufen wird.
   */
  private List<OparlReference<OparlMeeting>> meetings;

  public JsonNode getGeojson() {
    return this.geojson;
  }

  public String getSubLocality() {
    return this.subLocality;
  }

  public List<OparlReference<OparlBody>> getBodies() {
    return this.bodies;
  }

  public String getPostalCode() {
    return this.postalCode;
  }

  public List<OparlReference<OparlPerson>> getPersons() {
    return this.persons;
  }

  public String getRoom() {
    return this.room;
  }

  public String getStreetAddress() {
    return this.streetAddress;
  }

  public String getLocality() {
    return this.locality;
  }

  public List<OparlReference<OparlPaper>> getPapers() {
    return this.papers;
  }

  public List<OparlReference<OparlOrganization>> getOrganizations() {
    return this.organizations;
  }

  public String getDescription() {
    return this.description;
  }

  public List<OparlReference<OparlMeeting>> getMeetings() {
    return this.meetings;
  }

  public void setGeojson(JsonNode geojson) {
    this.geojson = geojson;
  }

  public void setSubLocality(String subLocality) {
    this.subLocality = subLocality;
  }

  public void setBodies(List<OparlReference<OparlBody>> bodies) {
    this.bodies = bodies;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  public void setPersons(List<OparlReference<OparlPerson>> persons) {
    this.persons = persons;
  }

  public void setRoom(String room) {
    this.room = room;
  }

  public void setStreetAddress(String streetAddress) {
    this.streetAddress = streetAddress;
  }

  public void setLocality(String locality) {
    this.locality = locality;
  }

  public void setPapers(List<OparlReference<OparlPaper>> papers) {
    this.papers = papers;
  }

  public void setOrganizations(List<OparlReference<OparlOrganization>> organizations) {
    this.organizations = organizations;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setMeetings(List<OparlReference<OparlMeeting>> meetings) {
    this.meetings = meetings;
  }
}
