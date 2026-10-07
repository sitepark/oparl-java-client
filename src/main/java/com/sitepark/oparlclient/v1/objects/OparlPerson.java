package com.sitepark.oparlclient.v1.objects;

import com.sitepark.oparlclient.core.OparlReference;
import java.util.List;

/**
 * Jede natürliche Person, die in der parlamentarischen Arbeit tätig und insbesondere Mitglied in
 * einer Gruppierung (oparl:Organization) ist, wird mit einem Objekt vom Typ {@code oparl:Person}
 * abgebildet.
 */
public class OparlPerson extends OparlObjectV1 {
  /**
   * Vorname bzw. Taufname.
   */
  private String givenName;

  /**
   * Familienname bzw. Nachname.
   */
  private String familyName;

  /**
   * Akademische Titel
   */
  private List<String> title;

  /**
   * Kontakt-Anschrift der Person. Wenn diese Eigenschaft ausgegeben wird, dann <b>muss</b> auch die
   * Eigenschaft {@code location} ausgegeben werden und auf das gleiche Location-Objekt verweisen.
   * Dieses Feld sollte die eigentliche Ausgabeform von {@code location} in OParl 1.0 werden. vgl.
   * https://github.com/OParl/spec/issues/373. Neu in OParl 1.1
   */
  private OparlLocation locationObject;

  /**
   * Mitgliedschaften der Person in Gruppierungen, z. B. Gremien und Fraktionen. Es <b>sollen</b>
   * sowohl aktuelle als auch vergangene Mitgliedschaften angegeben werden
   */
  private List<OparlMembership> membership;

  /**
   * E-Mail-Adressen der Person.
   */
  private List<String> email;

  /**
   * Namenszusatz (z.B. {@code jun.} oder {@code MdL.})
   */
  private String affix;

  /**
   * Körperschaft, zu der die Person gehört.
   */
  private OparlReference<OparlBody> body;

  /**
   * Anrede.
   */
  private String formOfAddress;

  /**
   * Referenz der Kontakt-Anschrift der Person.
   */
  private OparlReference<OparlLocation> location;

  /**
   * Kurzer Informationstext zur Person. Eine Länge von weniger als 300 Zeichen ist <b>empfohlen</b>
   */
  private String life;

  /**
   * Angabe der Quelle, aus der die Informationen für {@code life} stammen. Bei Angabe von {@code
   * life} ist diese Eigenschaft <b>empfohlen</b>
   */
  private String lifeSource;

  /**
   * Status, d.h. Rollen in der Kommune.
   */
  private List<String> status;

  /**
   * Der vollständige Name der Person mit akademischem Grad und dem gebräuchlichen Vornamen, wie er
   * zur Anzeige durch den Client genutzt werden kann.
   */
  private String name;

  /**
   * Telefonnummern der Person.
   */
  private List<String> phone;

  /**
   * Geschlecht. Vorgegebene Werte sind {@code female} und {@code male}, weitere werden durch die
   * durchgehend klein geschriebene englische Bezeichnung angegeben. Für den Fall, dass das
   * Geschlecht der Person unbekannt ist, <b>sollte</b> die Eigenschaft nicht ausgegeben werden.
   */
  private String gender;

  public String getGivenName() {
    return this.givenName;
  }

  public String getFamilyName() {
    return this.familyName;
  }

  public List<String> getTitle() {
    return this.title;
  }

  public OparlLocation getLocationObject() {
    return this.locationObject;
  }

  public List<OparlMembership> getMembership() {
    return this.membership;
  }

  public List<String> getEmail() {
    return this.email;
  }

  public String getAffix() {
    return this.affix;
  }

  public OparlReference<OparlBody> getBody() {
    return this.body;
  }

  public String getFormOfAddress() {
    return this.formOfAddress;
  }

  public OparlReference<OparlLocation> getLocation() {
    return this.location;
  }

  public String getLife() {
    return this.life;
  }

  public String getLifeSource() {
    return this.lifeSource;
  }

  public List<String> getStatus() {
    return this.status;
  }

  public String getName() {
    return this.name;
  }

  public List<String> getPhone() {
    return this.phone;
  }

  public String getGender() {
    return this.gender;
  }

  public void setGivenName(String givenName) {
    this.givenName = givenName;
  }

  public void setFamilyName(String familyName) {
    this.familyName = familyName;
  }

  public void setTitle(List<String> title) {
    this.title = title;
  }

  public void setLocationObject(OparlLocation locationObject) {
    this.locationObject = locationObject;
  }

  public void setMembership(List<OparlMembership> membership) {
    this.membership = membership;
  }

  public void setEmail(List<String> email) {
    this.email = email;
  }

  public void setAffix(String affix) {
    this.affix = affix;
  }

  public void setBody(OparlReference<OparlBody> body) {
    this.body = body;
  }

  public void setFormOfAddress(String formOfAddress) {
    this.formOfAddress = formOfAddress;
  }

  public void setLocation(OparlReference<OparlLocation> location) {
    this.location = location;
  }

  public void setLife(String life) {
    this.life = life;
  }

  public void setLifeSource(String lifeSource) {
    this.lifeSource = lifeSource;
  }

  public void setStatus(List<String> status) {
    this.status = status;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPhone(List<String> phone) {
    this.phone = phone;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }
}
