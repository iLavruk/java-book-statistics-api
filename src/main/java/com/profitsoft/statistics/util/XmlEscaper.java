package com.profitsoft.statistics.util;

/**
 * Minimal XML escaping to keep output well-formed.
 */
public final class XmlEscaper {

  private XmlEscaper() {
  }

  public static String escape(String value) {
    if (value == null) {
      return "";
    }
    StringBuilder builder = new StringBuilder(value.length() + 8);
    for (char ch : value.toCharArray()) {
      switch (ch) {
        case '&' -> builder.append("&amp;");
        case '<' -> builder.append("&lt;");
        case '>' -> builder.append("&gt;");
        case '"' -> builder.append("&quot;");
        case '\'' -> builder.append("&apos;");
        default -> builder.append(ch);
      }
    }
    return builder.toString();
  }
}
