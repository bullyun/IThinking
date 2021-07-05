package com.bullyun.ithinking.data;


import java.util.*;

public class Idea {
    IdeaTree ideaTree;

    private String uuid;
    private String name;
    private String desc;

    private Idea parent;            //如果是软连接
    private List<Idea> uncles = new ArrayList<>();      //软连接后的父对象
    private List<Idea> childs = new ArrayList<>();

    public Idea(IdeaTree ideaTree, String name) {
        this.uuid = UUID.randomUUID().toString();
        this.ideaTree = ideaTree;
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        callOnIdeaChange();
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
        callOnIdeaChange();
    }

    public Idea getParent() {
        return parent;
    }

    public void setParent(Idea parent) {
        this.parent = parent;
    }

    public List<Idea> getUncles() {
        return uncles;
    }

    public void setUncles(List<Idea> uncles) {
        this.uncles = uncles;
    }

    public List<Idea> getChilds() {
        return childs;
    }

    public void setChilds(List<Idea> childs) {
        this.childs = childs;
    }


    public IdeaTree getIdeaTree() {
        return ideaTree;
    }

    public int indexOfChilds(Idea idea) {
        return childs.indexOf(idea);
    }

    public boolean canAddChild(Idea idea) {
        if (idea == this) {
            //自己也没必要添加自己
            return false;
        }
        if (idea.getParent() != null && idea.getParent() == this) {
            //如果是引用，而且引用和本地是一个父对象，不允许
            return false;
        }
        if (childs.contains(idea)) {
            return false;
        }
        return true;
    }

    public void addChild(int index, Idea idea) {
        if (canAddChild(idea) == false) {
            return;
        }

        addChildImpl(index, idea);
        callOnAddIdea(childs.indexOf(idea), idea);
    }

    private void addChildImpl(int index, Idea idea) {
        if (index < 0) {
            childs.add(idea);
            if (idea.getParent() == null) {
                idea.setParent(this);
            } else {
                idea.getUncles().add(this);
            }
        } else {
            childs.add(index, idea);
            if (idea.getParent() == null) {
                idea.setParent(this);
            } else {
                idea.getUncles().add(this);
            }
        }
    }

    public Idea newChild() {
        Idea idea = new Idea(ideaTree, "");
        addChild(-1, idea);
        return idea;
    }

    public void delChild(Idea idea) {
        delChildImpl(idea);
        callOnDelIdea(idea);
    }

    private void delChildImpl(Idea idea) {
        childs.remove(idea);
        if (idea.getParent() == this) {
            idea.setParent(null);
        } else {
            idea.getUncles().remove(this);
        }
    }

    public boolean canMove(Idea src, Idea dst) {
        if (dst == this) {
            //目的地不能是自己
            return false;
        }
        if (src == dst) {
            //调整顺序，OK
            return true;
        }
        if (parent == null || dst == parent) {
            //没有父对象，不能移
            //父对象等于目标地址，而原对象不等于目标地址，等于有个软件连接移到真正的父对象上，不行
            return false;
        }
        if (dst.childs.contains(this)) {
            return false;
        }
        return true;
    }

    public void move(Idea src, Idea dst, int index) {
        if (canMove(src, dst) == false) {
            //如果是引用，而且引用和本地是一个父对象，不允许
            return;
        }

        src.delChildImpl(this);
        dst.addChildImpl(index, this);
        callOnIdeaMove(src, dst, index);
    }

    public boolean isTop() {
        return this == ideaTree.getTop();
    }

    public boolean isMyOrHigher(Idea higherIdea) {
        Idea find = this;
        while (find != null) {
            if (find == higherIdea) {
                return true;
            }
            find = find.getParent();
        }
        return false;
    }

    private void callOnAddIdea(int index, Idea idea) {
        ideaTree.setModified(true);
        ideaTree.getIdeaListenerMap().forEach((source, ideaListener) -> {
            ideaListener.onAddIdea(this, index, idea);
        });
    }
    private void callOnDelIdea(Idea idea) {
        ideaTree.setModified(true);
        ideaTree.getIdeaListenerMap().forEach((source, ideaListener) -> {
            ideaListener.onDelIdea(this, idea);
        });
    }
    private void callOnIdeaChange() {
        ideaTree.setModified(true);
        ideaTree.getIdeaListenerMap().forEach((source, ideaListener) -> {
            ideaListener.onIdeaChange(this);
        });
    }
    private void callOnIdeaMove(Idea src, Idea dst, int index) {
        ideaTree.setModified(true);
        ideaTree.getIdeaListenerMap().forEach((source, ideaListener) -> {
            ideaListener.onIdeaMove(this, src, dst, index);
        });
    }

}
