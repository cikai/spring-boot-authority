package me.cikai.common;

public class ResponseCodes {
  // 成功状态码
  public static String SIGNUP_SUCCESS = "200101";
  public static String SIGNIN_SUCCESS = "200201";
  public static String SIGNOUT_SUCCESS = "200301";
  public static String PASSWORD_UPDATED_SUCCESS = "200401";
  // 失败状态码
  // 1001xx 注册相关错误
  public static String SIGNUP_FAILURE = "100101";
  public static String SIGNUP_EMAIL_EXIST = "100102";
  public static String SIGNUP_USERNAME_EXIST = "100103";
  public static String SIGNUP_EMAIL_BLANK = "100104";
  public static String SIGNUP_USERNAME_BLANK = "100105";
  public static String SIGNUP_PASSWORD_BLANK = "100106";
  public static String SIGNUP_EMAIL_FORMAT_ERROR = "100107";
  // 1002xx 登录相关错误
  public static String SIGNIN_USER_NOT_EXIST = "100201";
  public static String SIGNIN_PASSWORD_ERROR = "100202";
  public static String SIGNIN_ALREADY_WARNING = "100203";
  // 1003xx 登出相关错误
  public static String SIGNOUT_FAILURE = "100301";
  // 1004xx 权限相关错误
  public static String TOKEN_INVALID = "100401";
  public static String TOKEN_TIMEOUT = "100402";
  public static String PASSWORD_UPDATED_FAILURE = "100403";
  // 5001xx 系统相关错误
  public static String SERVER_ERROR = "500100";
  public static String SERVER_PRIVATE_KEY_READ_ERROR = "500101";
  public static String SERVER_REDIS_CONNECT_ERROR = "500102";
}
