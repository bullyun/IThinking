package com.bullyun.ithinking;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.bullyun.ithinking.controller.MainController;

public class IThinkingApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/main.fxml"));
        Parent root = loader.load();
        MainController c = loader.getController();
        c.setStage(primaryStage);

        primaryStage.hide();
        primaryStage.setScene(new Scene(root, 1400, 900));
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            c.exitApplication();
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
