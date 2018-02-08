package me.cikai.model;

public class Account {
  private int userId;
  private String email;
  private String username;
  private String password;
  private String signupAt;

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getSignupAt() {
    return signupAt;
  }

  public void setSignupAt(String signupAt) {
    this.signupAt = signupAt;
  }
}
