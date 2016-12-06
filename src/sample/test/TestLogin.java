package sample.test;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import sample.LogJiraWorkUI;
import sample.utils.FileReader;

import java.io.IOException;

import static sample.utils.Helper.encodeCredentials;
import static sample.utils.TestHttp.basicAuthorization;

/**
 * Created by Ali on 07.12.2016.
 */
public class TestLogin extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Pane root = new Pane();
        Scene scene = new Scene(root, 350, 300);
        primaryStage.setTitle("Jira");
        Button test = new Button("Test!");
        GridPane grid = new GridPane();
        grid.add(test,1,1);
        root.getChildren().addAll(grid);

        primaryStage.setScene(scene);
        primaryStage.show();


        test.setOnAction(event -> {
            Platform.runLater(() -> {
                test.setText("Saving...");
            });
            Thread saveIt = new Thread(new Runnable() {
                @Override public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (Exception ex) {
                        System.out.println("");
                    }
                    Platform.runLater(() -> {
                        test.setText("Saved!");
                    });
                }
            });
            saveIt.setDaemon(true);
            saveIt.start();
        });
    }




    public static void main(String[] args) {
        launch(args);
    }
}
