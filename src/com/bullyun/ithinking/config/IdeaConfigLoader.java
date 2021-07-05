package com.bullyun.ithinking.config;

import com.bullyun.ithinking.constant.Constant;
import com.bullyun.ithinking.util.FileUtil;
import com.bullyun.ithinking.util.JsonUtil;
import com.bullyun.ithinking.util.StringUtil;
import org.json.JSONObject;

public class IdeaConfigLoader {
    public static IdeaConfig load() {
        IdeaConfig ideaConfig = loadImpl();
        if (ideaConfig == null) {
            ideaConfig = loadDefault();
        }
        return ideaConfig;
    }

    private static IdeaConfig loadDefault() {
        return new IdeaConfig();
    }

    private static IdeaConfig loadImpl() {
        byte[] data = FileUtil.load(Constant.CONFIG_FILENAME);
        if (data == null) {
            return null;
        }
        JSONObject jsonObject = JsonUtil.parseJson(StringUtil.fromBytes(data));
        if (jsonObject == null) {
            return null;
        }
        IdeaConfig ideaConfig = new IdeaConfig();
        ideaConfig.setFileName(JsonUtil.getString(jsonObject, "ithinkingFile"));
        return ideaConfig;
    }

    public static boolean save(IdeaConfig ideaConfig) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ithinkingFile", ideaConfig.getFileName());
        return FileUtil.save(StringUtil.toBytes(JsonUtil.toStyleString(jsonObject)), Constant.CONFIG_FILENAME);
    }

}
