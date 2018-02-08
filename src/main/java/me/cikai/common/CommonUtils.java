package me.cikai.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;

public class CommonUtils {
  public final static Boolean checkEmail(String email) {
    Pattern regex = compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", CASE_INSENSITIVE);
    Matcher matcher = regex.matcher(email);
    return matcher.find();
  }
}
