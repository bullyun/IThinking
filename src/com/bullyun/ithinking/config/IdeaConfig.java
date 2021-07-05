package com.bullyun.ithinking.config;

import com.bullyun.ithinking.constant.Constant;
import com.bullyun.ithinking.util.StringUtil;

public class IdeaConfig {
    private String fileName = Constant.DEFAULT_ITHINKING_FILE;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setDefaultFileName() {
        this.fileName = Constant.DEFAULT_ITHINKING_FILE;
    }
}
