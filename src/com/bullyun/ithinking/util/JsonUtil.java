package com.bullyun.ithinking.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JsonUtil {

    public static String toString(JSONObject jsonObject) {
        return jsonObject.toString();
    }

    public static String toStyleString(JSONObject jsonObject) {
        return jsonObject.toString(2);
    }


    public static JSONObject parseJson(String string) {
        try {
            return new JSONObject(new JSONTokener(string));
        }catch (JSONException e) {
            return null;
        }
    }

    public static String getString(JSONObject jsonObject, String key) {
        try {
            return jsonObject.getString(key);
        } catch (JSONException e) {
            return "";
        }
    }

    public static boolean getBoolean(JSONObject jsonObject, String key) {
        try {
            return jsonObject.getBoolean(key);
        } catch (JSONException e) {
            return false;
        }
    }

    public static JSONArray getArray(JSONObject jsonObject, String key) {
        try {
            return jsonObject.getJSONArray(key);
        } catch (JSONException e) {
            return new JSONArray();
        }
    }

}
