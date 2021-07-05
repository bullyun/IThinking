package com.bullyun.ithinking.widget;

import com.bullyun.ithinking.data.Idea;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.List;

public class IdeaWidgetItem {
    private IdeaTreeWidget ideaTreeWidget;
    private IdeaWidget ideaWidget;
    private Line line;
    private Group group;

    private IdeaWidgetItem parent;
    private List<IdeaWidgetItem> childs = new ArrayList<>();
    private List<IdeaWidgetItem> uncles = new ArrayList<>();

    public IdeaWidgetItem(IdeaTreeWidget ideaTreeWidget, Idea idea) {
        this.ideaTreeWidget = ideaTreeWidget;
        this.ideaWidget = new IdeaWidget(idea, this, ideaTreeWidget);
        this.line = new Line();
        this.group = new Group();

        line.setStroke(Color.rgb(0xa0, 0xf0, 0xf0));

        group.getChildren().add(ideaWidget);
    }

    public void attachTop(Group parent) {
        parent.getChildren().add(group);
        parent.getChildren().add(line);
        setLevel(0);
    }

    public void detachTop(Group parent) {
        parent.getChildren().remove(group);
        parent.getChildren().remove(line);
    }

    public boolean isTop() {
        return parent == null;
    }

    public void addChild(int index, IdeaWidgetItem item) {
        group.getChildren().add(item.group);
        group.getChildren().add(item.line);
        item.parent = this;
        if (index < 0) {
            childs.add(item);
        } else {
            childs.add(index, item);
        }
        //更新后面的等级
        item.updateLevel(getLevel() + 1);

        refresh();
    }

    public void delChild(IdeaWidgetItem item) {
        group.getChildren().remove(item.group);
        group.getChildren().remove(item.line);
        item.parent = null;
        childs.remove(item);

        refresh();
    }

    public void addUncle(int index, IdeaWidgetItem item) {
        group.getChildren().add(item.group);
        group.getChildren().add(item.line);

        item.parent = this;
        if (index < 0) {
            uncles.add(item);
        } else {
            uncles.add(index, item);
        }
        //更新后面的等级
        item.updateLevel(getLevel() - 1);

        refresh();
    }

    public void delUncle(IdeaWidgetItem item) {
        group.getChildren().remove(item.group);
        group.getChildren().remove(item.line);
        item.parent = null;
        uncles.remove(item);

        refresh();
    }

    private void updateLevel(int level) {
        setLevel(level);
        for (IdeaWidgetItem child : childs) {
            child.updateLevel(level + 1);
        }
        for (IdeaWidgetItem child : uncles) {
            child.updateLevel(level - 1);
        }
    }

    public int indexOfChild(IdeaWidgetItem item) {
        return childs.indexOf(item);
    }

    public boolean isLink() {
        return ideaWidget.isLink();
    }

    public void setLink(boolean link) {
        ideaWidget.setLink(link);
    }

    public void refresh() {
        ideaWidget.setExpend(isExpend());
        ideaWidget.refresh();

        //判断是不是父子关系
        if (isTop() == false) {
            boolean solid;
            if (inRight()) {
                solid = getParent().getIdea() == getIdea().getParent();
            } else {
                solid = getParent().getIdea().getParent() == getIdea();
            }
            if (solid) {
                line.getStrokeDashArray().clear();
            } else {
                line.getStrokeDashArray().setAll(10.0, 10.0);
            }
        }
    }

    public boolean isExpend() {
        //非软连接，都是展开。软连接由子项决定
        if (inRight()) {
            return isLink() == false || ideaWidget.getIdea().getChilds().isEmpty() || childs.isEmpty() == false;
        } else {
            return isLink() == false
                    || (ideaWidget.getIdea().getUncles().isEmpty() && ideaWidget.getIdea().getParent() == null)
                    || uncles.isEmpty() == false;
        }
    }

    public void setLevel(int level) {
        ideaWidget.setLevel(level);
    }

    public int getLevel() {
        return ideaWidget.getLevel();
    }

    public boolean inRight() {
        return ideaWidget.inRight();
    }

    public IdeaWidgetItem getParent() {
        return parent;
    }

    public List<IdeaWidgetItem> getChilds() {
        return childs;
    }

    public List<IdeaWidgetItem> getUncles() {
        return uncles;
    }

    public boolean isMyOrHigher(IdeaWidgetItem item) {
        IdeaWidgetItem find = this;
        while (find != null) {
            if (find == item) {
                return true;
            }
            find = find.getParent();
        }
        return false;
    }

    public IdeaWidget getIdeaWidget() {
        return ideaWidget;
    }

    public Group getGroup() {
        return group;
    }

    public Line getLine() {
        return line;
    }

    public Idea getIdea() {
        return ideaWidget.getIdea();
    }

    /*
    * layoutx,layouty 相对于父窗口
    * layoutbounds 相对于本窗口，只算有效区域
    * */
    public Bounds getIdeaWidgetSceneBounds() {
        return ideaWidget.localToScene(ideaWidget.getLayoutBounds());
    }

    public Bounds getIdeaGroupSceneBounds() {
        return group.localToScene(group.getLayoutBounds());
    }

    public Bounds getIdeaWidgetBounds() {
        return ideaWidget.getLayoutBounds();
    }

    public Bounds getIdeaGroupBounds() {
        return group.getLayoutBounds();
    }

    public void setFocus() {
        ideaWidget.setFocus();
    }
}
