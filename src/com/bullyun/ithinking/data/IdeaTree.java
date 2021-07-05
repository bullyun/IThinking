package com.bullyun.ithinking.data;


import com.bullyun.ithinking.constant.Constant;
import com.bullyun.ithinking.util.FileUtil;
import com.bullyun.ithinking.util.JsonUtil;
import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.omg.CORBA.PRIVATE_MEMBER;

import javax.annotation.processing.FilerException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class IdeaTree {
    private Idea top;
    private Map<Object, IdeaListener> ideaListenerMap = new HashMap<>();
    private boolean modified;

    public IdeaTree() {
        top = new Idea(this, Constant.DEFAULT_TOP);
        modified = false;
    }

    public Idea getTop() {
        return top;
    }

    public void setTop(Idea top) {
        this.top = top;
    }

    public boolean getModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public IdeaListener getIdeaListener(Object source) {
        return ideaListenerMap.get(source);
    }

    public Map<Object, IdeaListener> getIdeaListenerMap() {
        return ideaListenerMap;
    }

    public void setIdeaListener(Object source, IdeaListener ideaListener) {
        ideaListenerMap.put(source, ideaListener);
    }
}
