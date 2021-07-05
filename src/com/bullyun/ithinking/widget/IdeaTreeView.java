package com.bullyun.ithinking.widget;

import com.bullyun.ithinking.controller.MainController;
import com.bullyun.ithinking.data.Idea;
import com.bullyun.ithinking.data.IdeaListener;
import com.bullyun.ithinking.data.IdeaTree;
import com.sun.javafx.scene.control.skin.TreeViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import javafx.event.Event;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;

public class IdeaTreeView extends TreeView<IdeaTreeItem> {
    private final int DEFAULT_WIDTH = 150;
    private final int MAX_WIDTH = 300;

    private MainController controller;
    private IdeaTree ideaTree;
    private IdeaTreeItem root;

    public IdeaTreeView(IdeaTree ideaTree, MainController controller) {
        this.ideaTree = ideaTree;
        this.controller = controller;

        setMinWidth(DEFAULT_WIDTH);
        setPrefWidth(DEFAULT_WIDTH);
        setMaxWidth(MAX_WIDTH);

        registerIdeaListener();
        registerEventHandle();

        root = generateTreeItem(null, -1, ideaTree.getTop());
        this.setRoot(root.getTreeItem());
    }

    private List<IdeaTreeItem> getIdeaTreeItems(Idea idea) {
        List<IdeaTreeItem> ideaTreeItems = new ArrayList<>();
        getIdeaTreeItems(idea, root, ideaTreeItems);
        return ideaTreeItems;
    }

    private void getIdeaTreeItems(Idea idea, IdeaTreeItem ideaTreeItem, List<IdeaTreeItem> ideaTreeItems) {
        if (ideaTreeItem.getIdea() == idea) {
            ideaTreeItems.add(ideaTreeItem);
        }

        for (TreeItem<IdeaTreeItem> item : ideaTreeItem.getTreeItem().getChildren()) {
            IdeaTreeItem ideaTreeItemChild = IdeaTreeItem.getIdeaTreeItem(item);
            getIdeaTreeItems(idea, ideaTreeItemChild, ideaTreeItems);
        }
    }

    private IdeaTreeItem getSelectIdeaTreeItem() {
        MultipleSelectionModel<TreeItem<IdeaTreeItem>> multipleSelectionModel = this.getSelectionModel();
        TreeItem<IdeaTreeItem> itemSelect = multipleSelectionModel.getSelectedItem();
        if (itemSelect == null) {
            return null;
        }

        return IdeaTreeItem.getIdeaTreeItem(itemSelect);
    }

    private void registerEventHandle() {
        this.setOnMouseDragged(event -> {
        });
        this.setOnMouseDragReleased(event -> {
        });
        this.setOnDragDone(event -> {
        });
        this.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                IdeaTreeItem ideaWidgetItem = getSelectIdeaTreeItem();
                if (ideaWidgetItem == null) {
                    return;
                }
                controller.boardcaseEvent(this, new IdeaEvent(IdeaEvent.ITV_OPEN_IDEA, ideaWidgetItem.getIdea(), null));
                event.consume();
            }
        });
        this.setOnMouseReleased(event -> {
            IdeaTreeItem ideaWidgetItem = getSelectIdeaTreeItem();
            if (ideaWidgetItem == null) {
                return;
            }
            Point2D point2D = new Point2D(event.getSceneX(), event.getSceneY());
            controller.boardcaseEvent(this, new IdeaEvent(IdeaEvent.ITV_ADD_LINK, ideaWidgetItem.getIdea(), point2D));
        });

        this.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                IdeaTreeItem ideaTreeItem = oldValue.getValue();
                ideaTreeItem.setSelected(false);
            }
            if (newValue != null) {
                IdeaTreeItem ideaTreeItem = newValue.getValue();
                ideaTreeItem.setSelected(true);
            }
        });

        this.setCellFactory(param -> {
            TreeCell<IdeaTreeItem> treeCell = new TreeCell<IdeaTreeItem>() {
                @Override
                protected void updateItem(IdeaTreeItem item, boolean empty) {
                    super.updateItem(item, empty);
                    styleProperty().unbind();
                    if (item == null || empty) {
                        setText(null);
                        setGraphic(null);
                        setStyle(null);
                        return;
                    }
                    setGraphic(item.getLabel());
                    styleProperty().bind(item.styleProperty());

                    //
                    //IdeaTreeView.this.setStyle("-fx-arrows-visible: false");
                }
            };
            treeCell.addEventFilter(MouseEvent.MOUSE_PRESSED, (MouseEvent e) -> {
                if (e.getClickCount() == 2) {
                    e.consume();
                } else if (e.getClickCount() == 1) {
                    //getSelectionModel().select(treeCell.getTreeItem());
                    //e.consume();
                }
            });
            return treeCell;
        });
        this.addEventHandler(IdeaEvent.ITV_SELECT_IDEA, event -> {
            List<IdeaTreeItem> ideaTreeItems = getIdeaTreeItems(event.getIdea());
            ideaTreeItems.forEach(ideaTreeItem -> {
                if (ideaTreeItem.isLink() == false) {
                    setSelectAndVisible(ideaTreeItem);
                }
            });
        });
    }

    private void setSelectAndVisible(IdeaTreeItem ideaTreeItem) {
        //全部展开
        getSelectionModel().select(ideaTreeItem.getTreeItem());

        //滚动到某项可见
        show(ideaTreeItem.getTreeItem());
    }

    private VirtualFlow<TreeCell<IdeaTreeItem>> getFlow() {
        TreeViewSkin skin = (TreeViewSkin) getSkin();
        for (Object node : skin.getChildren()) {
            if (node instanceof VirtualFlow) {
                return (VirtualFlow<TreeCell<IdeaTreeItem>>) node;
            }
        }
        return null;
    }

    private void show(TreeItem<IdeaTreeItem> item) {
        VirtualFlow<TreeCell<IdeaTreeItem>> flow = getFlow();
        flow.show(getRow(item));
    }

    private void registerIdeaListener() {
        ideaTree.setIdeaListener(this, new IdeaListener() {
            @Override
            public void onAddIdea(Idea parent, int index, Idea idea) {
                List<IdeaTreeItem> ideaTreeItems = getIdeaTreeItems(parent);
                ideaTreeItems.forEach(ideaTreeItem -> {
                    if (ideaTreeItem.isLink() == false) {
                        generateTreeItem(ideaTreeItem, index, idea);
                    }
                });
            }

            @Override
            public void onDelIdea(Idea parent, Idea idea) {
                List<IdeaTreeItem> ideaTreeItems = getIdeaTreeItems(idea);
                ideaTreeItems.forEach(ideaTreeItem -> {
                    if (ideaTreeItem.getParent().getIdea() == parent) {
                        ideaTreeItem.getParent().delChild(ideaTreeItem);
                    }
                });
            }

            @Override
            public void onIdeaChange(Idea idea) {
                List<IdeaTreeItem> ideaTreeItems = getIdeaTreeItems(idea);
                ideaTreeItems.forEach(ideaTreeItem -> {
                    ideaTreeItem.refresh();
                });
            }

            @Override
            public void onIdeaMove(Idea idea, Idea src, Idea dst, int index) {
                List<IdeaTreeItem> ideaTreeItems = getIdeaTreeItems(idea);
                List<IdeaTreeItem> ideaTreeItemsSrc = getIdeaTreeItems(src);
                List<IdeaTreeItem> ideaTreeItemsDst = getIdeaTreeItems(dst);

                IdeaTreeItem ideaTreeItemReal = null;
                for (IdeaTreeItem ideaTreeItem : ideaTreeItems) {
                    if (ideaTreeItemsSrc.contains(ideaTreeItem.getParent()) == false) {
                        continue;
                    }
                    if (ideaTreeItem.getParent().isLink() == false) {
                        ideaTreeItemReal = ideaTreeItem;
                    }
                    ideaTreeItem.getParent().delChild(ideaTreeItem);
                }
                for (IdeaTreeItem ideaTreeItemDst : ideaTreeItemsDst) {
                    if (ideaTreeItemDst.isLink() == false) {
                        ideaTreeItemDst.addChild(index, ideaTreeItemReal);
                    }
                }
            }
        });
    }

    private IdeaTreeItem generateTreeItem(IdeaTreeItem ideaItemParent, int index, Idea idea) {
        IdeaTreeItem item = new IdeaTreeItem(idea);

        if (ideaItemParent == null) {
            item.attachTop();
        } else {
            ideaItemParent.addChild(index, item);
            item.setLink(ideaItemParent.isLink() || idea.getParent() != ideaItemParent.getIdea());
        }

        if (item.getLevel() >= 0 && item.getLevel() <= 2) {
            item.getTreeItem().setExpanded(true);
        }

        if (item.isLink() == false) {
            //真正的子集才展开
            for (Idea ideaChild : idea.getChilds()) {
                generateTreeItem(item, -1, ideaChild);
            }
        }

        return item;
    }
}
