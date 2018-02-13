package me.cikai.common;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import me.cikai.model.JWTHeader;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;

/**
 * @author cikai
 * Common Utilities
 * 共通工具
 */
public class CommonUtils {

  public final static Boolean checkEmail(String email) {
    Pattern regex = compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", CASE_INSENSITIVE);
    Matcher matcher = regex.matcher(email);
    return matcher.find();
  }

  public final static String getUserIdByToken(String token) {
    String userId = "";
    try {
      DecodedJWT jwt = JWT.decode(token);
      byte[] asBytes = Base64.getDecoder().decode(jwt.getHeader());
      String headerJson = new String(asBytes, "utf-8");
      JWTHeader header = new Gson().fromJson(headerJson, JWTHeader.class);
      userId = String.valueOf(header.getUserId());
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      return "";
    }
    return userId;
  }

  public static String resultBuilder(Boolean flag, String code, int userId, String message, String token) {
    Gson gson = new Gson();
    Map<String, String> result = new LinkedHashMap<>(5);
    if (flag) {
      result.put("result", "true");
      result.put("code", code);
      result.put("user_id", String.valueOf(userId));
      result.put("token", token);
      result.put("message", message);
      return gson.toJson(result);
    }
    result.put("result", "false");
    result.put("code", code);
    result.put("user_id", String.valueOf(userId));
    result.put("message", message);
    return gson.toJson(result);
  }

}
