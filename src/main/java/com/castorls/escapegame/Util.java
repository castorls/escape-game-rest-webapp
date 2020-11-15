package com.castorls.escapegame;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class Util {

  public static String protectString(String orig) {
    if (orig == null) {
      return null;
    }
    String strTemp = Normalizer.normalize(orig.trim(), Normalizer.Form.NFD);
    Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    return pattern.matcher(strTemp).replaceAll("").toUpperCase();
  }
}
