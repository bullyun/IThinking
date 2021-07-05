package com.bullyun.ithinking.scheme;

import com.bullyun.ithinking.data.IdeaTree;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import java.util.Collections;

public class IdeaScheme {
    private IdeaWidgetStyle[] ideaWidgetStyles;
    private IdeaTreeStyle[] ideaTreeStyles;
    private IdeaStyle ideaStyle;

    public IdeaScheme() {
        ideaStyle = new IdeaStyle();
        ideaStyle.setSelectColor(Color.rgb(0x97, 0xde, 0xff));
        //ideaStyle.setLinkColor(Color.rgb(0xff, 0x55, 0x55));
        ideaStyle.setLinkColor(Color.rgb(0xff, 0x88, 0x88));
        //ideaStyle.setTopColor(Color.rgb(0x2a, 0x7a, 0xc2));
        ideaStyle.setTopColor(Color.rgb(0x4E, 0xAE, 0xDE));

        ideaWidgetStyles = new IdeaWidgetStyle[4];
        ideaTreeStyles = new IdeaTreeStyle[4];

        IdeaWidgetStyle ideaWidgetStyle = new IdeaWidgetStyle();
        ideaWidgetStyle.setFont(createFont(false, 30));
        ideaWidgetStyle.setTextColor(Color.WHITE);
        ideaWidgetStyle.setBackground(ideaStyle.getTopColor());
        ideaWidgetStyle.setBoaderColor(ideaStyle.getTopColor());
        ideaWidgetStyle.setUnderlineColor(Color.TRANSPARENT);
        ideaWidgetStyles[0] = ideaWidgetStyle;
        IdeaTreeStyle ideaTreeStyle = new IdeaTreeStyle();
        ideaTreeStyle.setFont(createFont(false, 16));
        ideaTreeStyle.setTextColor(Color.WHITE);
        ideaTreeStyle.setBackground(Color.rgb(0x45, 0x4c, 0x73));
        ideaTreeStyle.setBoaderColor(Color.rgb(0x45, 0x4c, 0x73));
        ideaTreeStyles[0] = ideaTreeStyle;

        ideaWidgetStyle = new IdeaWidgetStyle();
        ideaWidgetStyle.setFont(createFont(false, 24));
        ideaWidgetStyle.setTextColor(Color.BLACK);
        ideaWidgetStyle.setBackground(Color.TRANSPARENT);
        //ideaWidgetStyle.setBoaderColor(Color.rgb(0xEE, 0xB4, 0xFF));
        ideaWidgetStyle.setBoaderColor(Color.rgb(0x4E, 0xAE, 0xDE));
        ideaWidgetStyle.setUnderlineColor(Color.TRANSPARENT);
        ideaWidgetStyles[1] = ideaWidgetStyle;
        ideaTreeStyle = new IdeaTreeStyle();
        ideaTreeStyle.setFont(createFont(false, 16));
        ideaTreeStyle.setTextColor(Color.WHITE);
        ideaTreeStyle.setBackground(Color.rgb(0x49, 0x77, 0xe6));
        ideaTreeStyle.setBoaderColor(Color.rgb(0x49, 0x77, 0xe6));
        ideaTreeStyles[1] = ideaTreeStyle;

        ideaWidgetStyle = new IdeaWidgetStyle();
        ideaWidgetStyle.setFont(createFont(false, 20));
        ideaWidgetStyle.setTextColor(Color.BLACK);
        ideaWidgetStyle.setBackground(Color.TRANSPARENT);
        ideaWidgetStyle.setBoaderColor(Color.TRANSPARENT);
        ideaWidgetStyle.setUnderlineColor(Color.rgb(0xE8, 0xA1, 0x07));
        ideaWidgetStyles[2] = ideaWidgetStyle;
        ideaTreeStyle = new IdeaTreeStyle();
        ideaTreeStyle.setFont(createFont(false, 16));
        ideaTreeStyle.setTextColor(Color.BLACK);
        ideaTreeStyle.setBackground(Color.rgb(0xE5, 0xE5, 0xFF));
        ideaTreeStyle.setBoaderColor(Color.rgb(0xE5, 0xE5, 0xFF));
        ideaTreeStyles[2] = ideaTreeStyle;

        ideaWidgetStyle = new IdeaWidgetStyle();
        ideaWidgetStyle.setFont(createFont(false, 16));
        ideaWidgetStyle.setTextColor(Color.BLACK);
        ideaWidgetStyle.setBackground(Color.TRANSPARENT);
        ideaWidgetStyle.setBoaderColor(Color.TRANSPARENT);
        ideaWidgetStyle.setUnderlineColor(Color.TRANSPARENT);
        ideaWidgetStyles[3] = ideaWidgetStyle;
        ideaTreeStyle = new IdeaTreeStyle();
        ideaTreeStyle.setFont(createFont(false, 14));
        ideaTreeStyle.setTextColor(Color.BLACK);
        ideaTreeStyle.setBackground(Color.rgb(0xFF, 0xFF, 0xFF));
        ideaTreeStyle.setBoaderColor(Color.rgb(0xFF, 0xFF, 0xFF));
        ideaTreeStyles[3] = ideaTreeStyle;
    }

    private Font createFont(boolean bold, int size) {
        return Font.font("", bold ? FontWeight.BOLD : FontWeight.NORMAL, FontPosture.REGULAR, size);
    }

    public IdeaWidgetStyle getIdeaWidgetStyle(int level) {
        if (level >= 0 && level < ideaWidgetStyles.length) {
            return ideaWidgetStyles[level];
        } else {
            return ideaWidgetStyles[ideaWidgetStyles.length - 1];
        }
    }

    public IdeaTreeStyle getIdeaTreeStyle(int level) {
        if (level >= 0 && level < ideaTreeStyles.length) {
            return ideaTreeStyles[level];
        } else {
            return ideaTreeStyles[ideaTreeStyles.length - 1];
        }
    }

    public IdeaStyle getIdeaStyle() {
        return ideaStyle;
    }
}
