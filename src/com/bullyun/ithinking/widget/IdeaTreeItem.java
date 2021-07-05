package com.bullyun.ithinking.widget;

import com.bullyun.ithinking.data.Idea;
import com.bullyun.ithinking.data.IdeaTree;
import com.bullyun.ithinking.scheme.IdeaSchemeManager;
import com.bullyun.ithinking.scheme.IdeaStyle;
import com.bullyun.ithinking.scheme.IdeaTreeStyle;
import com.bullyun.ithinking.scheme.IdeaWidgetStyle;
import com.bullyun.ithinking.util.ColorUtil;
import javafx.beans.InvalidationListener;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

import java.util.List;

public class IdeaTreeItem {
    private TreeItem<IdeaTreeItem> treeItem;
    private StringProperty styleProperty;
    private Label label;
    private Idea idea;
    private boolean link;
    private int level;
    private boolean selected;

    public IdeaTreeItem(Idea idea) {
        this.idea = idea;
        this.treeItem = new TreeItem<>(this);
        this.label = new Label("");
        this.styleProperty = new StringPropertyBase() {
            @Override
            public Object getBean() {
                return IdeaTreeItem.this;
            }
            @Override
            public String getName() {
                return "style";
            }
        };

        treeItem.setGraphic(label);
    }

    public void refresh() {
        label.setText(getVisibleName());

        IdeaTreeStyle ideaTreeStyle = IdeaSchemeManager.getCurrentScheme().getIdeaTreeStyle(level);
        IdeaStyle ideaStyle = IdeaSchemeManager.getCurrentScheme().getIdeaStyle();

        label.setTextFill(link ? ideaStyle.getLinkColor() : ideaTreeStyle.getTextColor());
        label.setFont(ideaTreeStyle.getFont());

        Color colorBg = ideaTreeStyle.getBackground();
        String style = "-fx-background-color: " + ColorUtil.toStyleColor(colorBg);
        if (selected) {
            style += ";-fx-border-width: 2;-fx-border-color: " + ColorUtil.toStyleColor(ideaStyle.getSelectColor());
        } else {
            style += ";-fx-border-width: 2;-fx-border-color: " + ColorUtil.toStyleColor(colorBg);
        }
        styleProperty.setValue(style);
    }

    public String getVisibleName() {
        String name = idea.getName();
        if (link) {
            //name = "$" + name;
        }
        //去掉换行
        name = name.replace("\n", "");
        name = name.replace("\r", "");
        return name;
    }

    public void attachTop() {
        setLevel(0);
    }

    public void addChild(int index, IdeaTreeItem ideaTreeItem) {
        if (index >= 0) {
            treeItem.getChildren().add(index, ideaTreeItem.getTreeItem());
        } else {
            treeItem.getChildren().add(ideaTreeItem.getTreeItem());
        }
        ideaTreeItem.updateLevel();
    }

    private void updateLevel() {
        setLevel(getParent().getLevel() + 1);
        for (TreeItem<IdeaTreeItem> child : treeItem.getChildren()) {
            child.getValue().updateLevel();
        }
    }

    public void delChild(IdeaTreeItem ideaTreeItem) {
        treeItem.getChildren().remove(ideaTreeItem.getTreeItem());
    }

    public IdeaTreeItem getParent() {
        if (treeItem.getParent() != null) {
            return treeItem.getParent().getValue();
        } else {
            return null;
        }
    }

    public TreeItem<IdeaTreeItem> getTreeItem() {
        return treeItem;
    }

    public static IdeaTreeItem getIdeaTreeItem(TreeItem<IdeaTreeItem> item) {
        return item.getValue();
    }

    public Label getLabel() {
        return label;
    }

    public Idea getIdea() {
        return idea;
    }

    public boolean isLink() {
        return link;
    }

    public void setLink(boolean link) {
        if (this.link != link) {
            this.link = link;
            refresh();
        }
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        if (this.level != level || level == 0) {
            this.level = level;
            refresh();
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        if (this.selected != selected) {
            this.selected = selected;
            refresh();
        }
    }

    public StringProperty styleProperty() {
        return styleProperty;
    }

//    private static IdeaTreeItem openItem;
//    public void openItem() {
//        IdeaTreeItem old = openItem;
//        openItem = this;
//        refresh();
//        if (old != null) {
//            old.refresh();
//        }
//    }
//
//    public boolean isOpenItem() {
//        return openItem == this;
//    }
//
//    public static IdeaTreeItem getOpenItem() {
//        return openItem;
//    }
}
