package com.bullyun.ithinking.util;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lan
 * @since 2017-11-07
 */
public final class StringUtil {

    private StringUtil() {
    }

    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

    public static boolean isNotEmpty(String s) {
        return !isEmpty(s);
    }

    public static boolean isNotBlank(String s) {
        return s != null && s.length() != 0;
    }

    public static boolean isEquals(String s1, String s2) {
        s1 = (s1 == null) ? "" : s1;
        s2 = (s2 == null) ? "" : s2;
        return s1.equals(s2);
    }

    public static boolean isNotEquals(String s1, String s2) {
        return !isEquals(s1, s2);
    }

    public static int toInt(String num) {
        try {
            return Integer.valueOf(num);
        } catch (Exception e) {
            return 0;
        }
    }

    public static long toLong(String num) {
        try {
            return Long.valueOf(num);
        } catch (Exception e) {
            return 0;
        }
    }

    public static String fromInt(Integer num) {
        try {
            return String.valueOf(num);
        } catch (Exception e) {
            return "0";
        }
    }

    public static String fromLong(Long num) {
        try {
            return String.valueOf(num);
        } catch (Exception e) {
            return "0";
        }
    }

    public static String fromDouble(Double num) {
        try {
            return (num == null) ? "" : String.valueOf(num);
        } catch (Exception e) {
            return "0";
        }
    }

    public static String fromDate(Date time) {
        try {
            return String.valueOf(time.getTime());
        } catch (Exception e) {
            return null;
        }
    }

    public static String safeString(String str) {
        return (str == null) ? "" : str;
    }

    public static String findJsonValue(String key, String json) {
        if (key == null || json == null) {
            return null;
        }
        String regex = "\"" + key + "\":(\\s*)(\"(.*?)\"|(\\d*))";
        Matcher matcher = Pattern.compile(regex).matcher(json);
        String value = null;
        try {
            if (matcher.find()) {
                value = matcher.group().split("\\:")[1].replace("\"", "").trim();
            }
        } catch (Exception e) {
            return null;
        }
        return value;
    }

    public static String findXMLValue(String key, String xml) {
        if (key == null || xml == null) {
            return null;
        }
        String regex = "<" + key + ">.*</" + key + ">";
        Matcher matcher = Pattern.compile(regex).matcher(xml);
        String value = null;
        try {
            if (matcher.find()) {
                value = matcher.group();
                value = value.substring(value.indexOf(">") + 1, value.lastIndexOf("<"));
            }
        } catch (Exception e) {
            return null;
        }
        return value;
    }

    /**
     * 驼峰转 下划线
     */
    public static String toUnderlineCase(String camelCaseStr) {
        Pattern humpPattern = Pattern.compile("[A-Z]");
        Matcher matcher = humpPattern.matcher(camelCaseStr);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String replacement = "_" + matcher.group(0).toLowerCase();
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static String fromBytes(byte[] data) {
        try {
            return new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    public static byte[] toBytes(String string) {
        try {
            return string.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            return new byte[0];
        }
    }

}
