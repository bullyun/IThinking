package com.bullyun.ithinking.widget;

import com.bullyun.ithinking.controller.MainController;
import com.bullyun.ithinking.data.Idea;
import com.bullyun.ithinking.data.IdeaListener;
import com.bullyun.ithinking.data.IdeaTree;
import com.bullyun.ithinking.tool.DelayExecutor;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.geometry.Bounds;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Math.max;


public class IdeaTreeWidget extends Pane {

    private final int CHILD_INTERVAL_WIDTH = 50;
    private final int CHILD_INTERVAL_HEIGHT = 4;
    private final int DEFAULT_TOP_X = 100;

    private Group groupTree;
    private Rectangle background;

    //拖拽
    private double dragStartX;
    private double dragStartY;
    private IdeaWidgetItem dragIdeaWidgetItem;
    private IdeaWidgetItem dragLinkIdeaWidgetItem;

    //
    private MainController controller;
    private IdeaTree ideaTree;
    private Idea currentIdeaTop;
    private IdeaWidgetItem ideaWidgetItemTop;

    //延时调用
    private DelayExecutor layoutDelayExecutor;

    public IdeaTreeWidget(IdeaTree ideaTree, MainController controller) {
        this.controller = controller;
        this.ideaTree = ideaTree;
        this.currentIdeaTop = ideaTree.getTop();

        background = new Rectangle();
        background.setFill(Color.WHITE);
        background.widthProperty().bind(this.widthProperty());
        background.heightProperty().bind(this.heightProperty());
        background.setMouseTransparent(true);
        this.getChildren().add(background);

        groupTree = new Group();
        this.getChildren().add(groupTree);

        layoutDelayExecutor = new DelayExecutor();

        registerHandle();
        registerIdeaListener();

        openIdea(ideaTree.getTop(), true);
    }

    private void registerIdeaListener() {
        ideaTree.setIdeaListener(this, new IdeaListener() {
            @Override
            public void onAddIdea(Idea parent, int index, Idea idea) {
                List<IdeaWidgetItem> ideaWidgetItems = getIdeaWidgetItems(idea);
                List<IdeaWidgetItem> ideaWidgetItemsParent = getIdeaWidgetItems(parent);

                ideaWidgetItemsParent.forEach(ideaWidgetItemParent -> {
                    if (ideaWidgetItemParent.inRight()) {
                        if (ideaWidgetItemParent.isExpend()) {
                            IdeaWidgetItem ideaWidgetItemNew = generateRightTreeWidgetItem(idea, ideaWidgetItemParent, index, 1);
                            if (ideaWidgetItemParent.isLink() == false) {
                                ideaWidgetItemNew.setFocus();
                            }
                        } else {
                            ideaWidgetItemParent.refresh();
                        }
                    }
                });

                ideaWidgetItems.forEach(ideaWidgetItem -> {
                    if (ideaWidgetItem.isTop() || ideaWidgetItem.inRight() == false) {
                        if (ideaWidgetItem.isExpend()) {
                            generateLeftTreeWidgetItem(parent, ideaWidgetItem, -1, 1);
                        } else {
                            ideaWidgetItem.refresh();
                        }
                    }
                });
            }

            @Override
            public void onDelIdea(Idea parent, Idea idea) {
                List<IdeaWidgetItem> ideaWidgetItems = getIdeaWidgetItems(idea);
                List<IdeaWidgetItem> ideaWidgetItemsParent = getIdeaWidgetItems(parent);

                ideaWidgetItemsParent.forEach(ideaWidgetItem -> {
                    if (ideaWidgetItem.inRight()) {
                        ideaWidgetItem.refresh();
                    } else {
                        if (ideaWidgetItem.getParent().getIdea() == idea) {
                            ideaWidgetItem.getParent().delUncle(ideaWidgetItem);
                        }
                    }
                });
                ideaWidgetItems.forEach(ideaWidgetItem -> {
                    if (ideaWidgetItem.inRight()) {
                        if (ideaWidgetItem.getParent() != null && ideaWidgetItem.getParent().getIdea() == parent) {
                            ideaWidgetItem.getParent().delChild(ideaWidgetItem);
                        }
                    } else {
                        ideaWidgetItem.refresh();
                    }
                });
            }

            @Override
            public void onIdeaChange(Idea idea) {
                List<IdeaWidgetItem> ideaWidgetItems = getIdeaWidgetItems(idea);
                ideaWidgetItems.forEach(ideaWidgetItem -> {
                    ideaWidgetItem.refresh();
                });
            }

            @Override
            public void onIdeaMove(Idea idea, Idea src, Idea dst, int index) {
                List<IdeaWidgetItem> ideaWidgetItems = getIdeaWidgetItems(idea);
                List<IdeaWidgetItem> ideaWidgetItemsSrc = getIdeaWidgetItems(src);
                List<IdeaWidgetItem> ideaWidgetItemsDst = getIdeaWidgetItems(dst);

                //先删除，参考onDelIdea
                IdeaWidgetItem ideaWidgetItemReal = null;
                for (IdeaWidgetItem ideaWidgetItem : ideaWidgetItemsSrc) {
                    if (ideaWidgetItem.inRight()) {
                        ideaWidgetItem.refresh();
                    } else {
                        if (ideaWidgetItem.getParent().getIdea() == idea) {
                            ideaWidgetItem.getParent().delUncle(ideaWidgetItem);
                        }
                    }
                }
                for (IdeaWidgetItem ideaWidgetItem : ideaWidgetItems) {
                    if (ideaWidgetItem.inRight()) {
                        if (ideaWidgetItem.getParent() == null || ideaWidgetItem.getParent().getIdea() != src) {
                            continue;
                        }
                        if (ideaWidgetItem.getParent().isLink() == false) {
                            ideaWidgetItemReal = ideaWidgetItem;
                        }
                        ideaWidgetItem.getParent().delChild(ideaWidgetItem);
                    }
                }

                //添加
                for (IdeaWidgetItem ideaWidgetItem : ideaWidgetItemsDst) {
                    if (ideaWidgetItem.inRight()) {
                        if (ideaWidgetItem.isLink() == false) {
                            ideaWidgetItem.addChild(index, ideaWidgetItemReal);
                        } else {
                            if (ideaWidgetItem.isExpend()) {
                                generateRightTreeWidgetItem(idea, ideaWidgetItem, index, 1);
                            } else {
                                ideaWidgetItem.refresh();
                            }
                        }
                    }
                }
                for (IdeaWidgetItem ideaWidgetItem : ideaWidgetItems) {
                    if (ideaWidgetItem.isTop() || ideaWidgetItem.inRight() == false) {
                        if (ideaWidgetItem.isExpend()) {
                            generateLeftTreeWidgetItem(dst, ideaWidgetItem, -1, 1);
                        } else {
                            ideaWidgetItem.refresh();
                        }
                    }
                }
            }
        });
    }

    private void registerHandle() {
        this.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                //软连接的展开
                IdeaWidgetItem ideaWidgetItem = getIdeaWidgetItemFromEvent(event);
                if (ideaWidgetItem != null && ideaWidgetItem.isLink()) {
                    if (ideaWidgetItem.isExpend() == false) {
                        expendTreeWidget(ideaWidgetItem);
                    } else {
                        foldTreeWidget(ideaWidgetItem);
                    }
                }
            }
        });
        this.setOnMousePressed(event -> {
            dragStartX = event.getX();
            dragStartY = event.getY();

            IdeaWidget ideaWidget = IdeaWidget.getFocusWidget();
            if (ideaWidget != null) {
                ideaWidget.setFocus();
            }
        });
        this.setOnMouseReleased(event -> {
            setCursor(Cursor.DEFAULT);
            dragIdeaWidgetItem = null;

            if (dragLinkIdeaWidgetItem != null) {
                Idea idea = dragLinkIdeaWidgetItem.getIdea();
                Point2D point2D = new Point2D(event.getSceneX(), event.getSceneY());
                addLinkIdeaByPoint(idea, point2D);

                dragLinkIdeaWidgetItem = null;
            }
        });
        this.setOnMouseDragged(event -> {
            if (event.getTarget() == this) {
                //拖整个画面
                double distanceX = event.getX() - dragStartX;
                double distanceY = event.getY() - dragStartY;
                dragStartX = event.getX();
                dragStartY = event.getY();

                double x = groupTree.getLayoutX() + distanceX;
                double y = groupTree.getLayoutY() + distanceY;

                groupTree.setLayoutX(x);
                groupTree.setLayoutY(y);

                return;
            }

            //拖IDEA
            if (dragIdeaWidgetItem != null) {
                IdeaWidgetItem ideaWidgetItem = dragIdeaWidgetItem;

                Point2D point2D = new Point2D(event.getSceneX(), event.getSceneY());
                IdeaWidgetItem ideaWidgetItemPoint = getIdeaWidgetItemFromPos(point2D);
                if (ideaWidgetItemPoint == null
                        || ideaWidgetItemPoint.isTop()
                        || ideaWidgetItemPoint.isMyOrHigher(ideaWidgetItem)
                        || ideaWidgetItemPoint.inRight() == false) {
                    return;
                }

                Bounds bounds = ideaWidgetItemPoint.getIdeaWidgetSceneBounds();
                if (bounds.contains(point2D)) {
                    IdeaWidgetItem ideaWidgetItemPointParent = ideaWidgetItemPoint.getParent();
                    if (ideaWidgetItemPointParent == null || ideaWidgetItemPointParent.isLink()) {
                        //父对象不能是软连接
                        return;
                    }
                    //插入兄弟
                    int index = ideaWidgetItemPointParent.indexOfChild(ideaWidgetItemPoint);
                    ideaWidgetItem.getIdea().move(ideaWidgetItem.getParent().getIdea(),
                            ideaWidgetItemPointParent.getIdea(),
                            index);
                } else {
                    if (ideaWidgetItemPoint.isLink()) {
                        //父对象不能是软连接
                        return;
                    }
                    ideaWidgetItem.getIdea().move(ideaWidgetItem.getParent().getIdea(),
                            ideaWidgetItemPoint.getIdea(),
                            -1);
                }
            }
        });
        this.setOnDragDetected(event -> {
            if (event.getTarget() == this) {
                setCursor(Cursor.MOVE);
                return;
            }
            IdeaWidgetItem ideaWidgetItem = getIdeaWidgetItemFromEvent(event);
            if (ideaWidgetItem == null) {
                return;
            }
            if (event.isControlDown()) {
                dragLinkIdeaWidgetItem = ideaWidgetItem;
            } else {
                if (ideaWidgetItem.isTop()
                        || ideaWidgetItem.getParent().isLink()
                        || ideaWidgetItem.inRight() == false) {
                    return;
                }
                dragIdeaWidgetItem = ideaWidgetItem;
            }
        });
        this.addEventHandler(ScrollEvent.SCROLL, event -> {
            if (event.isControlDown()) {
                if (event.getDeltaY() > 0) {
                    groupTree.setScaleX(groupTree.getScaleX() * (1/0.9));
                    groupTree.setScaleY(groupTree.getScaleY() * (1/0.9));
                } else {
                    groupTree.setScaleX(groupTree.getScaleX() * 0.9);
                    groupTree.setScaleY(groupTree.getScaleY() * 0.9);
                }
            } else {
                if (event.getDeltaY() > 0) {
                    groupTree.setLayoutY((int)(groupTree.getLayoutY() + getLayoutBounds().getHeight() / 3));
                } else {
                    groupTree.setLayoutY((int)(groupTree.getLayoutY() - getLayoutBounds().getHeight() / 3));
                }
            }
        });
        this.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.TAB) {
                //添加
                IdeaWidgetItem ideaWidgetItem = getIdeaWidgetItemFromEvent(event);
                if (ideaWidgetItem == null) {
                    return;
                }

                if (ideaWidgetItem.isLink()) {
                    expendTreeWidget(ideaWidgetItem);
                    setChildFocus(ideaWidgetItem);
                } else {
                    if (ideaWidgetItem.inRight()) {
                        ideaWidgetItem.getIdea().newChild();
                    }
                }

                event.consume();
            } else if (event.getCode() == KeyCode.DELETE) {
                IdeaWidgetItem ideaWidgetItem = getIdeaWidgetItemFromEvent(event);
                if (ideaWidgetItem == null || ideaWidgetItem.isTop() || ideaWidgetItem.getParent().isLink()) {
                    return;
                }

                ideaWidgetItem.getParent().getIdea().delChild(ideaWidgetItem.getIdea());

                event.consume();
            } else if (event.getCode() == KeyCode.LEFT) {
                //坐，选择父节点
                IdeaWidgetItem ideaWidgetItem = getIdeaWidgetItemFromEvent(event);
                if (ideaWidgetItem == null) {
                    return;
                }
                if (ideaWidgetItem.inRight()) {
                    if (ideaWidgetItem.getParent() != null) {
                        ideaWidgetItem.getParent().setFocus();
                    } else {
                        setChildFocus(ideaWidgetItem, false);
                    }
                } else {
                    if (ideaWidgetItem.isLink()) {
                        expendTreeWidget(ideaWidgetItem);
                    }
                    setChildFocus(ideaWidgetItem);
                }
                event.consume();
            } else if (event.getCode() == KeyCode.RIGHT) {
                //坐，选择父节点
                IdeaWidgetItem ideaWidgetItem = getIdeaWidgetItemFromEvent(event);
                if (ideaWidgetItem == null) {
                    return;
                }
                if (ideaWidgetItem.inRight()) {
                    if (ideaWidgetItem.isLink()) {
                        expendTreeWidget(ideaWidgetItem);
                    }
                    setChildFocus(ideaWidgetItem);
                } else {
                    ideaWidgetItem.getParent().setFocus();
                }
                event.consume();
            } else if (event.getCode() == KeyCode.SHIFT) {
                IdeaWidgetItem ideaWidgetItem = getIdeaWidgetItemFromEvent(event);
                if (ideaWidgetItem == null) {
                    return;
                }

                switchIdea(ideaWidgetItem);
                event.consume();
            }
        });
        this.addEventHandler(IdeaEvent.ANY, event -> {
            if (event.getEventType() == IdeaEvent.ITV_ADD_LINK) {
                Idea idea = event.getIdea();
                Point2D point2D = event.getMouse();

                addLinkIdeaByPoint(idea, point2D);
            } else if (event.getEventType() == IdeaEvent.ITV_OPEN_IDEA) {
                Idea idea = event.getIdea();
                openIdea(idea, true);
            } else if (event.getEventType() == IdeaEvent.IW_FOCUS) {
                IdeaWidget ideaWidget = event.getIdeaWidget();
                if (ideaWidget == null) {
                    return;
                }
                controller.boardcaseEvent(this, new IdeaEvent(IdeaEvent.ITV_SELECT_IDEA, ideaWidget.getIdea(), null));
                ensureIdeaWidgetVisible(ideaWidget);
            }
        });
    }

    private IdeaWidgetItem getIdeaWidgetItemFromEvent(Event event) {
        EventTarget eventTarget = event.getTarget();
        if (!(eventTarget instanceof Node)) {
            return null;
        }
        if (eventTarget instanceof IdeaWidget) {
            return ((IdeaWidget) eventTarget).getIdeaWidgetItem();
        }
        Parent parent = ((Node) eventTarget).getParent();
        while (parent != null) {
            if (parent instanceof IdeaWidget) {
                return ((IdeaWidget) parent).getIdeaWidgetItem();
            }
            parent = parent.getParent();
        }

        return null;
    }

    private List<IdeaWidgetItem> getIdeaWidgetItems(Idea idea) {
        List<IdeaWidgetItem> ideaWidgetItems = new ArrayList<>();
        getIdeaWidgetItems(idea, ideaWidgetItemTop, ideaWidgetItems);
        return ideaWidgetItems;
    }

    private IdeaWidgetItem getIdeaWidgetItem(Idea idea) {
        List<IdeaWidgetItem> ideaWidgetItems = getIdeaWidgetItems(idea);
        for (IdeaWidgetItem ideaWidgetItem : ideaWidgetItems) {
            if (ideaWidgetItem.isLink() == false) {
                return ideaWidgetItem;
            }
        }
        return null;
    }

    private void getIdeaWidgetItems(Idea idea, IdeaWidgetItem ideaWidgetItem, List<IdeaWidgetItem> ideaWidgetItems) {
        if (ideaWidgetItem.getIdea() == idea) {
            ideaWidgetItems.add(ideaWidgetItem);
        }

        for (IdeaWidgetItem ideaWidgetItemChild : ideaWidgetItem.getChilds()) {
            getIdeaWidgetItems(idea, ideaWidgetItemChild, ideaWidgetItems);
        }

        for (IdeaWidgetItem ideaWidgetItemUncle : ideaWidgetItem.getUncles()) {
            getIdeaWidgetItems(idea, ideaWidgetItemUncle, ideaWidgetItems);
        }
    }

    private IdeaWidgetItem getIdeaWidgetItemFromPos(Point2D point2D) {
        return getIdeaWidgetItemFromPos(ideaWidgetItemTop, point2D);
    }

    private IdeaWidgetItem getIdeaWidgetItemFromPos(IdeaWidgetItem ideaWidgetItem, Point2D point2D) {
        Bounds bounds = ideaWidgetItem.getIdeaGroupSceneBounds();
        if (point2D.getX() >= bounds.getMinX()
                && point2D.getY() >= bounds.getMinY()
                && point2D.getY() <= bounds.getMaxY()) {

            if (ideaWidgetItem.getUncles().isEmpty() == false) {
                for (IdeaWidgetItem ideaWidgetItemUncle : ideaWidgetItem.getUncles()) {
                    ideaWidgetItemUncle = getIdeaWidgetItemFromPos(ideaWidgetItemUncle, point2D);
                    if (ideaWidgetItemUncle != null) {
                        return ideaWidgetItemUncle;
                    }
                }
            }
            if (ideaWidgetItem.getChilds().isEmpty() == false) {
                for (IdeaWidgetItem ideaWidgetItemChild : ideaWidgetItem.getChilds()) {
                    ideaWidgetItemChild = getIdeaWidgetItemFromPos(ideaWidgetItemChild, point2D);
                    if (ideaWidgetItemChild != null) {
                        return ideaWidgetItemChild;
                    }
                }
            }

            bounds = ideaWidgetItem.getIdeaWidgetSceneBounds();
            if (ideaWidgetItem.inRight()) {
                if (ideaWidgetItem.getChilds().isEmpty() == false) {
                    if (bounds.contains(point2D)) {
                        return ideaWidgetItem;
                    }
                } else {
                    if (point2D.getX() >= bounds.getMinX()
                            && point2D.getY() >= bounds.getMinY()
                            && point2D.getY() <= bounds.getMaxY()) {
                        return ideaWidgetItem;
                    }
                }
            } else {
                if (bounds.contains(point2D)) {
                    return ideaWidgetItem;
                }
            }
        }
        return null;
    }

    private void addLinkIdeaByPoint(Idea idea, Point2D point2D) {
        IdeaWidgetItem ideaWidgetItemPoint = getIdeaWidgetItemFromPos(point2D);
        if (ideaWidgetItemPoint == null || ideaWidgetItemPoint.inRight() == false) {
            return;
        }

        Bounds bounds = ideaWidgetItemPoint.getIdeaWidgetSceneBounds();
        if (bounds.contains(point2D)) {
            IdeaWidgetItem ideaWidgetItemPointParent = ideaWidgetItemPoint.getParent();
            if (ideaWidgetItemPointParent == null || ideaWidgetItemPointParent.isLink()) {
                //父对象不能是软连接
                return;
            }

            int index = ideaWidgetItemPointParent.getIdea().indexOfChilds(ideaWidgetItemPoint.getIdea());
            ideaWidgetItemPointParent.getIdea().addChild(index, idea);
        } else {
            if (ideaWidgetItemPoint.isLink()) {
                //父对象不能是软连接
                return;
            }
            ideaWidgetItemPoint.getIdea().addChild(-1, idea);
        }
    }

    private void generateTreeWidgetItem() {
        ideaWidgetItemTop = generateRightTreeWidgetItem(currentIdeaTop, null, -1, 3);
        generateLeftChildTreeWidgetItem(currentIdeaTop, ideaWidgetItemTop, 3);
    }

    private void openIdea(Idea idea, boolean layout) {
        if (ideaWidgetItemTop != null) {
            ideaWidgetItemTop.detachTop(groupTree);
        }
        currentIdeaTop = idea;
        generateTreeWidgetItem();

        if (layout) {
            ideaWidgetItemTop.setFocus();

            layoutDelayExecutor.delayExecute(() -> {
                setTopIdeaWidgetToCenter();
            });
        }
    }

    private void switchIdea(IdeaWidgetItem ideaWidgetItem) {
        Bounds oldBounds = sceneToLocal(ideaWidgetItem.getIdeaWidgetSceneBounds());

        Idea idea = ideaWidgetItem.getIdea();
        if (idea == currentIdeaTop) {
            //还原成顶部
            if (idea == ideaTree.getTop()) {
                return;
            }
            openIdea(ideaTree.getTop(), false);
        } else {
            openIdea(idea, false);
        }

        final IdeaWidgetItem ideaWidgetItem2 = getIdeaWidgetItem(idea);
        ideaWidgetItem2.setFocus();
        layoutDelayExecutor.delayExecute(() -> {
            Bounds newBounds = sceneToLocal(ideaWidgetItem2.getIdeaWidgetSceneBounds());
            groupTree.setLayoutX(groupTree.getLayoutX()
                    + (oldBounds.getMinX() + (int)(oldBounds.getWidth() / 2))
                    - (newBounds.getMinX() + (int)(newBounds.getWidth() / 2)));
            groupTree.setLayoutY(groupTree.getLayoutY()
                    + (oldBounds.getMinY() + (int)(oldBounds.getHeight() / 2))
                    - (newBounds.getMinY() + (int)(newBounds.getHeight() / 2)));
        });
    }

    private void setTopIdeaWidgetToCenter() {
        Bounds boundsTop = ideaWidgetItemTop.getIdeaWidgetSceneBounds();
        boundsTop = sceneToLocal(boundsTop);
        Bounds bounds = getLayoutBounds();

        groupTree.setLayoutX(groupTree.getLayoutX() - boundsTop.getMinX() + (int)(bounds.getWidth() / 3));
        groupTree.setLayoutY(groupTree.getLayoutY() - boundsTop.getMinY() + (int)((bounds.getHeight() - boundsTop.getHeight()) / 2));
    }

    private void expendTreeWidget(IdeaWidgetItem ideaWidgetItem) {
        if (ideaWidgetItem.isExpend()) {
            return;
        }
        if (ideaWidgetItem.inRight()) {
            generateRightChildTreeWidgetItem(ideaWidgetItem.getIdea(), ideaWidgetItem, 2);
        } else {
            generateLeftChildTreeWidgetItem(ideaWidgetItem.getIdea(), ideaWidgetItem, 2);
        }
    }

    private void setChildFocus(IdeaWidgetItem ideaWidgetItem, boolean right) {
        if (right) {
            if (ideaWidgetItem.getChilds().isEmpty() == false) {
                ideaWidgetItem.getChilds().get(0).setFocus();
            }
        } else {
            if (ideaWidgetItem.getUncles().isEmpty() == false) {
                ideaWidgetItem.getUncles().get(0).setFocus();
            }
        }
    }

    private void setChildFocus(IdeaWidgetItem ideaWidgetItem) {
        setChildFocus(ideaWidgetItem, ideaWidgetItem.inRight());
    }

    private void foldTreeWidget(IdeaWidgetItem ideaWidgetItem) {
        if (ideaWidgetItem.inRight()) {
            List<IdeaWidgetItem> ideaWidgetItems = new ArrayList<>(ideaWidgetItem.getChilds());
            for (IdeaWidgetItem child : ideaWidgetItems) {
                ideaWidgetItem.delChild(child);
            }
        } else {
            List<IdeaWidgetItem> ideaWidgetItems = new ArrayList<>(ideaWidgetItem.getUncles());
            for (IdeaWidgetItem child : ideaWidgetItems) {
                ideaWidgetItem.delUncle(child);
            }
        }
    }

    private void generateRightChildTreeWidgetItem(Idea idea, IdeaWidgetItem item, int linkExpend) {
        for (Idea child : idea.getChilds()) {
            generateRightTreeWidgetItem(child, item, -1, linkExpend - 1);
        }
    }

    private IdeaWidgetItem generateRightTreeWidgetItem(Idea idea, IdeaWidgetItem parentItem, int index, int linkExpend) {
        IdeaWidgetItem ideaWidgetItem = new IdeaWidgetItem(this, idea);

        if (parentItem == null) {
            ideaWidgetItem.attachTop(groupTree);
        } else {
            parentItem.addChild(index, ideaWidgetItem);
            ideaWidgetItem.setLink(parentItem.isLink() || idea.getParent() != parentItem.getIdea());
        }

        if (ideaWidgetItem.isLink() == false || linkExpend > 0) {
            generateRightChildTreeWidgetItem(idea, ideaWidgetItem, linkExpend);
        }

        ideaWidgetItem.refresh();
        return ideaWidgetItem;
    }

    private void generateLeftChildTreeWidgetItem(Idea idea, IdeaWidgetItem item, int linkExpend) {
        List<Idea> uncles = new ArrayList<>();
        uncles.addAll(idea.getUncles());
        if (idea.getParent() != null) {
            uncles.add(uncles.size() / 2, idea.getParent());
        }
        for (Idea child : uncles) {
            generateLeftTreeWidgetItem(child, item, -1, linkExpend - 1);
        }
    }

    private IdeaWidgetItem generateLeftTreeWidgetItem(Idea idea, IdeaWidgetItem parentItem, int index, int linkExpend) {
        IdeaWidgetItem ideaWidgetItem = new IdeaWidgetItem(this, idea);

        parentItem.addUncle(index, ideaWidgetItem);
        ideaWidgetItem.setLink(parentItem.isLink() || idea != parentItem.getIdea().getParent());

        if (ideaWidgetItem.isLink() == false || linkExpend > 0) {
            generateLeftChildTreeWidgetItem(idea, ideaWidgetItem, linkExpend);
        }

        ideaWidgetItem.refresh();
        return ideaWidgetItem;
    }

    @Override
    protected void layoutChildren() {
        //double y = ideaWidgetItemTop.getIdeaWidget().getLayoutY();

        autoSizeIdeaWidgetItem(ideaWidgetItemTop);

        layoutDelayExecutor.doDelayExecute();

        //确保top节点位置不变
        //groupTree.setLayoutY(groupTree.getLayoutY() + (y - ideaWidgetItemTop.getIdeaWidget().getLayoutY()));
    }

    private void ensureIdeaWidgetVisible(IdeaWidget ideaWidget) {
        layoutChildren();

        Bounds bounds = ideaWidget.localToScene(ideaWidget.getLayoutBounds());
        bounds = sceneToLocal(bounds);
        Bounds bounds1 = getLayoutBounds();

        if (bounds.getMaxX() > bounds1.getMaxX()) {
            groupTree.setLayoutX(groupTree.getLayoutX() - (bounds.getMaxX() - bounds1.getMaxX()));
        } else if (bounds.getMinX() < bounds1.getMinX()) {
            groupTree.setLayoutX(groupTree.getLayoutX() - (bounds.getMinX() - bounds1.getMinX()));
        }

        if (bounds.getMaxY() > bounds1.getMaxY()) {
            groupTree.setLayoutY(groupTree.getLayoutY() - (bounds.getMaxY() - bounds1.getMaxY()));
        } else if (bounds.getMinY() < bounds1.getMinY()) {
            groupTree.setLayoutY(groupTree.getLayoutY() - (bounds.getMinY() - bounds1.getMinY()));
        }
    }

    private void autoSizeIdeaWidgetItem(IdeaWidgetItem ideaWidgetItem) {
        //横向铺开
        Idea idea = ideaWidgetItem.getIdea();
        IdeaWidget ideaWidget = ideaWidgetItem.getIdeaWidget();
        Bounds bounds = ideaWidget.getLayoutBounds();
        Dimension2D sizeWidget = new Dimension2D(bounds.getWidth(), bounds.getHeight());

        if (ideaWidgetItem.getChilds().isEmpty() == false) {
            Dimension2D sizeChilds = new Dimension2D(0, 0);

            List<Bounds> boundsChilds = new ArrayList<>(ideaWidgetItem.getChilds().size());
            for (IdeaWidgetItem ideaWidgetItemChild : ideaWidgetItem.getChilds()) {
                autoSizeIdeaWidgetItem(ideaWidgetItemChild);

                Bounds boundsChild = ideaWidgetItemChild.getIdeaGroupBounds();
                boundsChilds.add(boundsChild);

                sizeChilds = new Dimension2D(max(sizeChilds.getWidth(), boundsChild.getWidth()),
                        sizeChilds.getHeight() + boundsChild.getHeight() + CHILD_INTERVAL_HEIGHT);
            }
            sizeChilds = new Dimension2D(sizeChilds.getWidth(),
                    sizeChilds.getHeight() - CHILD_INTERVAL_HEIGHT);

            //调整各个控件的位置
            ideaWidget.setLayoutX(0);
            double x = sizeWidget.getWidth() + CHILD_INTERVAL_WIDTH;
            double y;
            if (sizeChilds.getHeight() >= sizeWidget.getHeight()) {
                ideaWidget.setLayoutY((int)((sizeChilds.getHeight() - sizeWidget.getHeight()) / 2));
                y = 0;
            } else {
                ideaWidget.setLayoutY(0);
                y = (int)((sizeWidget.getHeight() - sizeChilds.getHeight()) / 2);
            }

            int i = 0;
            for (IdeaWidgetItem ideaWidgetItemChild : ideaWidgetItem.getChilds()) {
                //设置子区域的位置
                Bounds boundsChild = boundsChilds.get(i);
                ideaWidgetItemChild.getGroup().setLayoutX(x);
                ideaWidgetItemChild.getGroup().setLayoutY(y);

                //设置连接线的位置
                autoSizeIdeaWidgetLine(ideaWidgetItemChild);

                y += boundsChild.getHeight() + CHILD_INTERVAL_HEIGHT;
                i++;
            }
        } else {
            ideaWidget.setLayoutX(0);
            ideaWidget.setLayoutY(0);
        }

        if (ideaWidgetItem.getUncles().isEmpty() == false) {
            Dimension2D sizeUncles = new Dimension2D(0, 0);

            List<Bounds> boundsUncles = new ArrayList<>(idea.getUncles().size());
            for (IdeaWidgetItem ideaWidgetItemUncle : ideaWidgetItem.getUncles()) {
                autoSizeIdeaWidgetItem(ideaWidgetItemUncle);

                Bounds boundsUncle = ideaWidgetItemUncle.getIdeaGroupBounds();
                boundsUncles.add(boundsUncle);

                sizeUncles = new Dimension2D(max(sizeUncles.getWidth(), boundsUncle.getWidth()),
                        sizeUncles.getHeight() + boundsUncle.getHeight() + CHILD_INTERVAL_HEIGHT);
            }
            sizeUncles = new Dimension2D(sizeUncles.getWidth(),
                    sizeUncles.getHeight() - CHILD_INTERVAL_HEIGHT);

            //调整各个控件的位置
            double x = 0 - CHILD_INTERVAL_WIDTH;
            double y;
            if (ideaWidgetItem.getChilds().isEmpty() == false) {
                y = ideaWidget.getLayoutY() - (int)((sizeUncles.getHeight() - sizeWidget.getHeight()) / 2);
            } else {
                if (sizeUncles.getHeight() >= sizeWidget.getHeight()) {
                    ideaWidget.setLayoutY((int)((sizeUncles.getHeight() - sizeWidget.getHeight()) / 2));
                    y = 0;
                } else {
                    ideaWidget.setLayoutY(0);
                    y = (int)((sizeWidget.getHeight() - sizeUncles.getHeight()) / 2);
                }
            }

            int i = 0;
            for (IdeaWidgetItem ideaWidgetItemUncle : ideaWidgetItem.getUncles()) {
                //设置子区域的位置
                Bounds boundsUncle = boundsUncles.get(i);
                ideaWidgetItemUncle.getGroup().setLayoutX(x - ideaWidgetItemUncle.getIdeaWidgetBounds().getWidth());
                ideaWidgetItemUncle.getGroup().setLayoutY(y);

                //设置连接线的位置
                autoSizeIdeaWidgetLine(ideaWidgetItemUncle);

                y += boundsUncle.getHeight() + CHILD_INTERVAL_HEIGHT;
                i++;
            }
        }
    }

    private void autoSizeIdeaWidgetLine(IdeaWidgetItem ideaWidgetItem) {
        if (ideaWidgetItem.getParent() != null) {
            IdeaWidgetItem ideaWidgetItemParent = ideaWidgetItem.getParent();

            Bounds bounds1 = ideaWidgetItemParent.getIdeaWidgetSceneBounds();
            bounds1 = ideaWidgetItemParent.getGroup().sceneToLocal(bounds1);

            Bounds bounds2 = ideaWidgetItem.getIdeaWidgetSceneBounds();
            bounds2 = ideaWidgetItemParent.getGroup().sceneToLocal(bounds2);

            if (bounds1.getMinX() < bounds2.getMinX()) {
                ideaWidgetItem.getLine().setStartX(bounds1.getMaxX());
                ideaWidgetItem.getLine().setStartY((int)((bounds1.getMinY() + bounds1.getMaxY()) / 2));

                ideaWidgetItem.getLine().setEndX(bounds2.getMinX());
                ideaWidgetItem.getLine().setEndY((int)((bounds2.getMinY() + bounds2.getMaxY()) / 2));
            } else {
                ideaWidgetItem.getLine().setStartX(bounds1.getMinX());
                ideaWidgetItem.getLine().setStartY((int)((bounds1.getMinY() + bounds1.getMaxY()) / 2));

                ideaWidgetItem.getLine().setEndX(bounds2.getMaxX());
                ideaWidgetItem.getLine().setEndY((int)((bounds2.getMinY() + bounds2.getMaxY()) / 2));
            }

        } else {
            ideaWidgetItem.getLine().setVisible(false);
        }
    }

}
