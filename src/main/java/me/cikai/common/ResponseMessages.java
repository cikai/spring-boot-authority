package me.cikai.common;

public class ResponseMessages {
  // 注册相关消息
  public static String SIGNUP_SUCCESS = "注册成功！";
  public static String SIGNUP_FAILURE = "注册失败！";
  public static String SIGNUP_EMAIL_EXIST = "邮箱已存在！";
  public static String SIGNUP_USERNAME_EXIST = "用户名已存在！";
  public static String SIGNUP_EMAIL_BLANK = "邮箱不能为空！";
  public static String SIGNUP_USERNAME_BLANK = "用户名不能为空！";
  public static String SIGNUP_PASSWORD_BLANK = "密码不能为空！";
  public static String SIGNUP_EMAIL_FORMAT_ERROR = "邮箱格式错误！";
  // 登录相关消息
  public static String SIGNIN_USER_NOT_EXIST = "用户不存在！";
  public static String SIGNIN_PASSWORD_ERROR = "密码错误！";
  public static String SIGNIN_SUCCESS = "登录成功！";
  public static String SIGNIN_ALREADY_WARNING = "用户已登录！";
  // 系统相关消息
  public static String SERVER_ERROR = "服务端错误！";
  public static String SERVER_PRIVATE_KEY_READ_ERROR = "服务端私钥获取失败！";
}
