package com.bullyun.ithinking.storage;

import com.bullyun.ithinking.data.Idea;
import com.bullyun.ithinking.data.IdeaTree;
import com.bullyun.ithinking.util.FileUtil;
import com.bullyun.ithinking.util.JsonUtil;
import com.bullyun.ithinking.util.StringUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class IdeaFile {

    private String fileName;

    public IdeaFile(String fileName) {
        this.fileName = fileName;
    }

    public boolean save(IdeaTree ideaTree) {
        JSONObject jsonObject = new JSONObject();
        saveToJson(ideaTree.getTop(), false, jsonObject);
        if (FileUtil.save(StringUtil.toBytes(JsonUtil.toStyleString(jsonObject)), fileName)) {
            ideaTree.setModified(false);
            return true;
        } else {
            return false;
        }
    }

    public boolean load(IdeaTree ideaTree) {
        byte[] data = FileUtil.load(fileName);
        if (data == null || data.length == 0) {
            return false;
        }
        JSONObject jsonObject = JsonUtil.parseJson(StringUtil.fromBytes(data));
        if (jsonObject == null) {
            return false;
        }
        Map<String, Idea> ideaMap = new HashMap<>();
        if (loadFromJson(ideaTree.getTop(), jsonObject, ideaMap) < 0) {
            return false;
        }
        loadLinkFromJson(jsonObject, ideaMap);
        ideaTree.setModified(false);
        return true;
    }

    private void saveToJson(Idea idea, boolean link, JSONObject jsonObject) {
        JSONObject jsonIdea = new JSONObject();
        jsonObject.put("idea", jsonIdea);

        jsonIdea.put("uuid", idea.getUuid());
        jsonIdea.put("name", idea.getName());
        jsonIdea.put("desc", idea.getDesc());
        jsonIdea.put("link", link);

        if (link == false) {
            JSONArray jsonChilds = new JSONArray();
            jsonObject.put("childs", jsonChilds);
            for (Idea child : idea.getChilds()) {
                JSONObject childJsonObject = new JSONObject();
                saveToJson(child, child.getParent() != idea, childJsonObject);
                jsonChilds.put(childJsonObject);
            }
        }
    }

    private int loadFromJson(Idea idea, JSONObject jsonObject, Map<String, Idea> ideaMap) {
        JSONObject jsonIdea = jsonObject.getJSONObject("idea");

        idea.setUuid(JsonUtil.getString(jsonIdea, "uuid"));
        if (StringUtil.isEmpty(idea.getUuid())) {
            return -1;
        }

        idea.setName(JsonUtil.getString(jsonIdea, "name"));
        idea.setDesc(JsonUtil.getString(jsonIdea, "desc"));
        boolean link = JsonUtil.getBoolean(jsonIdea, "link");

        if (link == false) {
            JSONArray jsonChilds = JsonUtil.getArray(jsonObject, "childs");
            for (Object child : jsonChilds) {
                JSONObject childJsonObject = (JSONObject) child;

                Idea ideaChild = new Idea(idea.getIdeaTree(), "");
                int result = loadFromJson(ideaChild, childJsonObject, ideaMap);
                if (result < 0) {
                    return result;
                }
                if (result == 0) {
                    idea.addChild(-1, ideaChild);
                }
            }
            ideaMap.put(idea.getUuid(), idea);
            return 0;
        } else {
            return 1;
        }
    }

    private Idea loadLinkFromJson(JSONObject jsonObject, Map<String, Idea> ideaMap) {
        JSONObject jsonIdea = jsonObject.getJSONObject("idea");

        String uuid = JsonUtil.getString(jsonIdea, "uuid");
        Idea idea = ideaMap.get(uuid);
        boolean link = JsonUtil.getBoolean(jsonIdea, "link");

        if (link == false) {
            JSONArray jsonChilds = JsonUtil.getArray(jsonObject, "childs");
            int index = 0;
            for (Object child : jsonChilds) {
                JSONObject childJsonObject = (JSONObject) child;
                Idea ideaLink = loadLinkFromJson(childJsonObject, ideaMap);
                if (ideaLink != null) {
                    idea.addChild(index, ideaLink);
                }
                index++;
            }
            return null;
        } else {
            Idea idea2 = ideaMap.get(uuid);
            return idea2;
        }
    }

}
