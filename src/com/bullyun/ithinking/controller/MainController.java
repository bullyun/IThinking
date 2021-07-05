package com.bullyun.ithinking.controller;

import com.bullyun.ithinking.config.IdeaConfig;
import com.bullyun.ithinking.config.IdeaConfigLoader;
import com.bullyun.ithinking.data.Idea;
import com.bullyun.ithinking.data.IdeaTree;
import com.bullyun.ithinking.storage.IdeaFile;
import com.bullyun.ithinking.widget.IdeaEvent;
import com.bullyun.ithinking.widget.IdeaTreeView;
import com.bullyun.ithinking.widget.IdeaTreeWidget;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import static com.bullyun.ithinking.constant.Constant.DEFAULT_ITHINKING_FILE;
import static com.bullyun.ithinking.constant.Constant.ITHINKING_FILE_EXT;

public class MainController {

    @FXML
    private SplitPane splitePane;

    //
    private Stage stage;

    //
    private Timer timer;

    //数据
    private IdeaTree ideaTree;
    private IdeaConfig ideaConfig;

    //控件
    IdeaTreeWidget ideaTreeWidget;
    IdeaTreeView ideaTreeView;

    @FXML
    public void initialize() {
        ideaConfig = IdeaConfigLoader.load();

        if (openFile(ideaConfig.getFileName()) == false) {
            //打开失败，还原配置
            openNewFile();
        }

        //定时保存
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    autoSave();
                });
            }
        }, 1000, 1000);
    }

    public void boardcaseEvent(Object source, IdeaEvent event) {
        if (source == ideaTreeWidget) {
            Event.fireEvent(ideaTreeView, event);
        } else if (source == ideaTreeView) {
            Event.fireEvent(ideaTreeWidget, event);
        }
    }

    private void updateView() {
        ideaTreeWidget = new IdeaTreeWidget(ideaTree, this);
        splitePane.getItems().set(1, ideaTreeWidget);

        ideaTreeView = new IdeaTreeView(ideaTree, this);
        splitePane.getItems().set(0, ideaTreeView);
    }

    private void updateTitle() {
        if (stage != null) {
            stage.setTitle("IThinking - " + ideaConfig.getFileName());
        }
    }

    private void openNewFile() {
        ideaConfig.setDefaultFileName();
        IdeaConfigLoader.save(ideaConfig);
        ideaTree = new IdeaTree();
        ideaTree.setModified(true);

        updateView();
        updateTitle();
    }

    private boolean openFile(String fileName) {
        IdeaTree ideaTree = new IdeaTree();
        IdeaFile ideaFile = new IdeaFile(fileName);
        if (ideaFile.load(ideaTree) == false) {
            return false;
        }
        ideaConfig.setFileName(fileName);
        IdeaConfigLoader.save(ideaConfig);

        this.ideaTree = ideaTree;

        updateView();
        updateTitle();

        return true;
    }

    private boolean autoSave() {
        if (ideaTree.getModified()) {
            IdeaFile ideaFile = new IdeaFile(ideaConfig.getFileName());
            return ideaFile.save(ideaTree);
        } else {
            return true;
        }
    }

    private boolean saveTo(String filename) {
        IdeaFile ideaFile = new IdeaFile(filename);
        if (ideaFile.save(ideaTree)) {
            ideaConfig.setFileName(filename);
            IdeaConfigLoader.save(ideaConfig);
            updateTitle();
            return true;
        }
        return false;
    }

    public void exitApplication() {
        timer.cancel();
        autoSave();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        updateTitle();
    }

    @FXML
    public void onNewFile(ActionEvent event) {
        autoSave();
        openNewFile();
    }

    @FXML
    public void onOpenFile(ActionEvent event) {
        autoSave();

        FileChooser fileChooser  = new FileChooser();
        fileChooser.setInitialFileName(DEFAULT_ITHINKING_FILE);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ithinking file",
                "*." + ITHINKING_FILE_EXT));

        File file = fileChooser.showOpenDialog(stage.getOwner());
        if (file == null) {
            return;
        }
        if (openFile(file.getPath()) == false) {
            showAlart("文件错误", "打开文件失败");
        }
    }

    @FXML
    public void onSaveFile(ActionEvent event) {
        FileChooser fileChooser  = new FileChooser();
        fileChooser.setInitialFileName(ideaTree.getTop().getName() + "." + ITHINKING_FILE_EXT);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ithinking file",
                "*." + ITHINKING_FILE_EXT));

        File file = fileChooser.showSaveDialog(stage.getOwner());
        if (file == null) {
            return;
        }
        if (saveTo(file.getPath()) == false) {
            showAlart("文件错误", "保存文件失败");
        }
    }

    private void showAlart(String title, String text) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(title);
        alert.setContentText(text);
        alert.show();
    }
}
