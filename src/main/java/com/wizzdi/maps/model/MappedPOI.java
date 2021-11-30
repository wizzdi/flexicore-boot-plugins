package com.wizzdi.maps.model;

import com.flexicore.model.SecuredBasic;
import com.flexicore.model.territories.Address;
import com.wizzdi.flexicore.file.model.FileResource;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class MappedPOI extends SecuredBasic {

  private String geoHash7;

  private String geoHash10;

  private String geoHash2;

  private Double y;

  private String geoHash11;

  private String geoHash4;

  private String geoHash6;

  private String geoHash8;

  private Double z;

  private String geoHash1;

  private String geoHash3;

  private String geoHash9;

  private Double lat;

  private Double x;

  private String test;

  private String test1;

  private String test2;

  @ManyToOne(targetEntity = Address.class)
  private Address address;

  @ManyToOne(targetEntity = FileResource.class)
  private FileResource icon;

  private String geoHash12;

  private Double lon;

  private String geoHash5;

  /** @return geoHash7 */
  public String getGeoHash7() {
    return this.geoHash7;
  }

  /**
   * @param geoHash7 geoHash7 to set
   * @return MappedPOI
   */
  public <T extends MappedPOI> T setGeoHash7(String geoHash7) {
    this.geoHash7 = geoHash7;
    return (T) this;
  }

  /** @return geoHash10 */
  public String getGeoHash10() {
    return this.geoHash10;
  }

  /**
   * @param geoHash10 geoHash10 to set
   * @return MappedPOI
   */
  public <T extends MappedPOI> T setGeoHash10(String geoHash10) {
    this.geoHash10 = geoHash10;
    return (T) this;
  }

  /** @return geoHash2 */
  public String getGeoHash2() {
    return this.geoHash2;
  }

  /**
   * @param geoHash2 geoHash2 to set
   * @return MappedPOI
   */
  public <T extends MappedPOI> T setGeoHash2(String geoHash2) {
    this.geoHash2 = geoHash2;
    return (T) this;
  }

  /** @return y */
  public Double getY() {
    return this.y;
  }

  /**
   * @param y y to set
   * @return MappedPOI
   */
  public <T extends MappedPOI> T setY(Double y) {
    this.y = y;
    return (T) this;
  }

  /** @return geoHash11 */
  public String getGeoHash11() {
    return this.geoHash11;
  }

  /**
   * @param geoHash11 geoHash11 to set
   * @return MappedPOI
   */
  public <T extends MappedPOI> T setGeoHash11(String geoHash11) {
    this.geoHash11 = geoHash11;
    return (T) this;
  }

  /** @return geoHash4 */
  public String getGeoHash4() {
    return this.geoHash4;
  }

  /**
   * @param geoHash4 geoHash4 to set
   * @return MappedPOI
   */
  public <T extends MappedPOI> T setGeoHash4(String geoHash4) {
    this.geoHash4 = geoHash4;
    return (T) this;
  }

  /** @return geoHash6 */
  public String getGeoHash6() {
    return this.geoHash6;
  }

  /**
   * @param geoHash6 geoHash6 to set
   * @return MappedPOI
   */
  public <T extends MappedPOI> T setGeoHash6(String geoHash6) {
    this.geoHash6 = geoHash6;
    return (T) this;
  }

  /** @return geoHash8 */
  public String getGeoHash8() {
    return this.geoHash8;
  }

  /**
   * @param geoHash8 geoHash8 to set
   * @return MappedPOI
   */
  public <T extends MappedPOI> T setGeoHash8(String geoHash8) {
    this.geoHash8 = geoHash8;
    return (T) this;
  }

  /** @return z */
  public Double getZ() {
    return this.z;
  }

  /**
   * @param z z to set
   * @return MappedPOI
   */
  public <T extends MappedPOI> T setZ(Double z) {
    this.z = z;
    return (T) this;
  }

  /** @return geoHash1 */
  public String getGeoHash1() {
    return this.geoHash1;
  }

  /**
   * @param geoHash1 geoHash1 to set
   * @return MappedPOI
   */
  public <T extends MappedPOI> T setGeoHash1(String geoHash1) {
    this.geoHash1 = geoHash1;
    return (T) this;
  }

  /** @return geoHash3 */
  public String getGeoHash3() {
    return this.geoHash3;
  }

  /**
   * @param geoHash3 geoHash3 to set
   * @return MappedPOI
   */
  public <T extends MappedPOI> T setGeoHash3(String geoHash3) {
    this.geoHash3 = geoHash3;
    return (T) this;
  }

  /** @return geoHash9 */
  public String getGeoHash9() {
    return this.geoHash9;
  }

  /**
   * @param geoHash9 geoHash9 to set
   * @return MappedPOI
   */
  public <T extends MappedPOI> T setGeoHash9(String geoHash9) {
    this.geoHash9 = geoHash9;
    return (T) this;
  }

  /** @return lat */
  public Double getLat() {
    return this.lat;
  }

  /**
   * @param lat lat to set
   * @return MappedPOI
   */
  public <T extends MappedPOI> T setLat(Double lat) {
    this.lat = lat;
    return (T) this;
  }

  /** @return x */
  public Double getX() {
    return this.x;
  }

  /**
   * @param x x to set
   * @return MappedPOI
   */
  public <T extends MappedPOI> T setX(Double x) {
    this.x = x;
    return (T) this;
  }

  /** @return test */
  public String getTest() {
    return this.test;
  }

  /**
   * @param test test to set
   * @return MappedPOI
   */
  public <T extends MappedPOI> T setTest(String test) {
    this.test = test;
    return (T) this;
  }

  /** @return test1 */
  public String getTest1() {
    return this.test1;
  }

  /**
   * @param test1 test1 to set
   * @return MappedPOI
   */
  public <T extends MappedPOI> T setTest1(String test1) {
    this.test1 = test1;
    return (T) this;
  }

  /** @return test2 */
  public String getTest2() {
    return this.test2;
  }

  /**
   * @param test2 test2 to set
   * @return MappedPOI
   */
  public <T extends MappedPOI> T setTest2(String test2) {
    this.test2 = test2;
    return (T) this;
  }

  /** @return address */
  @ManyToOne(targetEntity = Address.class)
  public Address getAddress() {
    return this.address;
  }

  /**
   * @param address address to set
   * @return MappedPOI
   */
  public <T extends MappedPOI> T setAddress(Address address) {
    this.address = address;
    return (T) this;
  }

  /** @return icon */
  @ManyToOne(targetEntity = FileResource.class)
  public FileResource getIcon() {
    return this.icon;
  }

  /**
   * @param icon icon to set
   * @return MappedPOI
   */
  public <T extends MappedPOI> T setIcon(FileResource icon) {
    this.icon = icon;
    return (T) this;
  }

  /** @return geoHash12 */
  public String getGeoHash12() {
    return this.geoHash12;
  }

  /**
   * @param geoHash12 geoHash12 to set
   * @return MappedPOI
   */
  public <T extends MappedPOI> T setGeoHash12(String geoHash12) {
    this.geoHash12 = geoHash12;
    return (T) this;
  }

  /** @return lon */
  public Double getLon() {
    return this.lon;
  }

  /**
   * @param lon lon to set
   * @return MappedPOI
   */
  public <T extends MappedPOI> T setLon(Double lon) {
    this.lon = lon;
    return (T) this;
  }

  /** @return geoHash5 */
  public String getGeoHash5() {
    return this.geoHash5;
  }

  /**
   * @param geoHash5 geoHash5 to set
   * @return MappedPOI
   */
  public <T extends MappedPOI> T setGeoHash5(String geoHash5) {
    this.geoHash5 = geoHash5;
    return (T) this;
  }
}
