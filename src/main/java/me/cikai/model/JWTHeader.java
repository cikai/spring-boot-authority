package me.cikai.model;

public class JWTHeader {
  private String typ;
  private String alg;
  private int userId;
  private long timestamp;

  public String getTyp() {
    return typ;
  }

  public void setTyp(String typ) {
    this.typ = typ;
  }

  public String getAlg() {
    return alg;
  }

  public void setAlg(String alg) {
    this.alg = alg;
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

}
