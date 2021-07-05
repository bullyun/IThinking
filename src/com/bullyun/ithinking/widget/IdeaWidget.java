package com.bullyun.ithinking.widget;

import com.bullyun.ithinking.data.Idea;
import com.bullyun.ithinking.scheme.IdeaSchemeManager;
import com.bullyun.ithinking.scheme.IdeaStyle;
import com.bullyun.ithinking.scheme.IdeaWidgetStyle;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import static java.lang.Math.max;

public class IdeaWidget extends Group {

    private Rectangle rectBackground = new Rectangle();        //背景矩形
    private Rectangle rectBorder = new Rectangle();
    private TextArea editor = new TextArea();
    private Label label = new Label();
    private Line line = new Line();

    private final int MIN_WIDTH = 50;
    private final int MAX_WIDTH = 400;

    private IdeaTreeWidget ideaTreeWidget;
    private IdeaWidgetItem ideaWidgetItem;
    private Idea idea;
    private boolean link;
    private int level;
    private boolean expend;

    //当前激活的窗口
    private static IdeaWidget focusWidget;

    //
    private boolean inPressEnterKey = false;

    public IdeaWidget(Idea idea, IdeaWidgetItem ideaWidgetItem, IdeaTreeWidget ideaTreeWidget) {
        this.ideaTreeWidget = ideaTreeWidget;
        this.idea = idea;
        this.ideaWidgetItem = ideaWidgetItem;

        label.setMouseTransparent(true);
        label.setMinWidth(MIN_WIDTH);
        label.setMaxWidth(MAX_WIDTH);
        label.setAlignment(Pos.CENTER);
        label.setText(getVisibleName());
        label.getStyleClass().add("idea-widget-label");

        rectBackground.setLayoutX(2);
        rectBackground.setLayoutY(2);
        rectBackground.setArcWidth(6);
        rectBackground.setArcHeight(6);
        rectBackground.setStrokeWidth(2);
        rectBackground.setFocusTraversable(true);
        rectBackground.setStrokeType(StrokeType.INSIDE);

        line.setStartX(3);
        line.startYProperty().bind(rectBackground.heightProperty());
        line.endXProperty().bind(rectBackground.widthProperty());
        line.endYProperty().bind(rectBackground.heightProperty());

        rectBorder.setArcWidth(6);
        rectBorder.setArcHeight(6);
        rectBorder.setStrokeWidth(2);
        rectBorder.setFill(Color.TRANSPARENT);

        editor.setVisible(false);
        editor.getStyleClass().add("idea-widget-editor");

        getChildren().add(rectBorder);
        getChildren().add(rectBackground);
        getChildren().add(line);
        getChildren().add(label);
        getChildren().add(editor);

        refresh();

        rectBackground.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                if (link == false) {
                    toEditState();
                    event.consume();
                }
            }
        });
        rectBackground.setOnMousePressed(event -> {
            rectBackground.requestFocus();
            event.consume();
        });
        rectBackground.focusedProperty().addListener((observable, oldValue, newValue) -> {
            updateFocusState();
        });
//        rectBackground.addEventHandler(MouseEvent.ANY, event -> {
//            if (event.getEventType() == MouseEvent.DRAG_DETECTED
//                    || event.getEventType() == MouseEvent.MOUSE_DRAGGED
//                    || event.getEventType() == MouseEvent.MOUSE_RELEASED) {
//                return;
//            }
//
//            //鼠标消息不传给父对象
//            event.consume();
//        });
        rectBackground.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                toEditState();
                event.consume();
                return;
            } else if (event.getCode() == KeyCode.UNDEFINED) {
                toEditState();
                event.consume();
                return;
            } else if (event.getCode().isLetterKey() || event.getCode().isDigitKey()) {
                toEditState();
                event.consume();
                Event.fireEvent(editor, event.copyFor(event.getSource(), editor));
                return;
            }
        });
        rectBackground.setOnKeyReleased(event -> {
//            Event.fireEvent(editor, event.copyFor(event.getSource(), editor));
//            event.consume();
        });

        editor.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (inPressEnterKey) {
                    return;
                }
                if (event.isControlDown()) {
                    //在编辑状态下，ctrl+enter才是回车
                    event.consume();

                    inPressEnterKey = true;
                    try {
                        Event.fireEvent(editor, new KeyEvent(IdeaWidget.this, editor, KeyEvent.KEY_PRESSED, "", "", KeyCode.ENTER,
                                false, false, false, false));
                    } finally {
                        inPressEnterKey = false;
                    }
                } else {
                    finishEditState(true);
                    event.consume();
                }
            } else if (event.getCode() == KeyCode.ESCAPE) {
                finishEditState(false);
            }
        });
        editor.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == false) {
                finishEditState(true);
            }
            updateFocusState();
        });
    }

    private void toEditState() {
        if (link) {
            return;
        }
        IdeaWidgetStyle ideaWidgetStyle = IdeaSchemeManager.getCurrentScheme().getIdeaWidgetStyle(level);

        editor.setFont(ideaWidgetStyle.getFont());
        editor.setText(idea.getName());
        editor.setPrefWidth((int)((label.getWidth() + rectBackground.getWidth()) / 2));
        editor.setPrefHeight((int)((label.getHeight() + rectBackground.getHeight()) / 2));
        editor.setLayoutX((int)((label.getLayoutX() + rectBackground.getLayoutX()) / 2 + 1));
        editor.setLayoutY((int)((label.getLayoutY() + rectBackground.getLayoutY()) / 2 + 1));
        editor.setVisible(true);
        editor.requestFocus();
        editor.selectAll();

        label.setVisible(false);
    }

    private void finishEditState(boolean save) {
        if (editor.isVisible() == false) {
            return;
        }
        label.setVisible(true);
        editor.setVisible(false);
        if (save) {
            idea.setName(editor.getText().trim());
            refresh();
        }

        if (editor.isFocused()) {
            rectBackground.requestFocus();
        }
    }

    public static IdeaWidget getFocusWidget() {
        if (focusWidget == null) {
            return null;
        }
        focusWidget.updateFocusState();
        return focusWidget;
    }

    private void updateFocusState() {
        updateSelectUI();

        if (getFocus()) {
            if (focusWidget != this){
                focusWidget = this;

                sendFocusEvent(this);
            }
        } else {
            if (focusWidget == this) {
                focusWidget = null;

                sendFocusEvent(null);
            } else {
                focusWidget.updateFocusState();
            }
        }
    }

    private void sendFocusEvent(IdeaWidget ideaWidget) {
        Event.fireEvent(this, new IdeaEvent(IdeaEvent.IW_FOCUS, ideaWidget));
    }

    public void setFocus() {
        rectBackground.requestFocus();
    }

    public boolean getFocus() {
        return rectBackground.isFocused() || editor.isFocused();
    }

    public void setBackgroundColor(Color color) {
        rectBackground.setFill(color);
    }
    public void setTextColor(Color color) {
        label.setTextFill(color);
    }

    public String getText() {
        return label.getText();
    }

    public Idea getIdea() {
        return idea;
    }

    public IdeaWidgetItem getIdeaWidgetItem() {
        return ideaWidgetItem;
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

    public String getVisibleName() {
        String visibleName = idea.getName();
        if (link) {
            //visibleName = "$" + visibleName;
            if (expend == false) {
                if (inRight()) {
                    visibleName += " +";
                } else {
                    visibleName = "+ " + visibleName;
                }
            }
        }
        return visibleName;
    }

    public void refresh() {
        label.setText(getVisibleName());
        updateUI();
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

    public boolean inRight() {
        return level >= 0;
    }

    public boolean isTop() {
        return level == 0;
    }

    private void updateUI() {
        IdeaWidgetStyle ideaWidgetStyle = IdeaSchemeManager.getCurrentScheme().getIdeaWidgetStyle(level);
        IdeaStyle ideaStyle = IdeaSchemeManager.getCurrentScheme().getIdeaStyle();

        label.setFont(ideaWidgetStyle.getFont());
        label.setTextFill(link ? ideaStyle.getLinkColor() : ideaWidgetStyle.getTextColor());

        rectBackground.setFill(ideaWidgetStyle.getBackground());
        rectBackground.setStroke(ideaWidgetStyle.getBoaderColor());

        line.setStroke(ideaWidgetStyle.getUnderlineColor());

        updateSelectUI();
        updateLayout();
    }

    private void updateSelectUI() {
        IdeaWidgetStyle ideaWidgetStyle = IdeaSchemeManager.getCurrentScheme().getIdeaWidgetStyle(level);
        IdeaStyle ideaStyle = IdeaSchemeManager.getCurrentScheme().getIdeaStyle();
        rectBorder.setStroke(getFocus() ? ideaStyle.getSelectColor() : Color.TRANSPARENT);
    }

    private void updateLayout() {
        double fontSize = label.getFont().getSize();
        double BORDER_WIDTH = (int)(fontSize / 3);
        double BORDER_HEIGHT = (int)(fontSize / 3 - 3);

        label.setLayoutX(2 + BORDER_WIDTH);
        label.setLayoutY(1 + BORDER_HEIGHT);

        rectBackground.heightProperty().bind(label.heightProperty().add(BORDER_HEIGHT * 2));
        rectBackground.widthProperty().bind(label.widthProperty().add(BORDER_WIDTH * 2));

        rectBorder.heightProperty().bind(label.heightProperty().add(BORDER_HEIGHT * 2 + 4));
        rectBorder.widthProperty().bind(label.widthProperty().add(BORDER_WIDTH * 2 + 4));
    }

    public boolean isExpend() {
        return expend;
    }

    public void setExpend(boolean expend) {
        if (this.expend != expend) {
            this.expend = expend;
            refresh();
        }
    }
}
