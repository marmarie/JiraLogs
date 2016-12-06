package sample;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Pair;
import sample.utils.FileReader;
import structure.model.UserPreferences;

import java.io.IOException;
import java.util.Optional;

import static sample.utils.FileReader.getCredentialsFromFile;
import static sample.utils.Helper.encodeCredentials;
import static sample.utils.Helper.getPassword;
import static sample.utils.TestHttp.basicAuthorization;

/**
 * Created by marie on 24.11.16.
 */
public class LoginPage2 extends Application {
    UserPreferences userPreferences = new UserPreferences();


    @Override
    public void start(Stage primaryStage) throws Exception {

        Pane root = new Pane();
        Scene scene = new Scene(root, 350, 300);
        primaryStage.setTitle("Jira");
        Button loginButton = new Button("Login");

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField username = new TextField();
        username.setPromptText("Username");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        CheckBox saveCredentials = new CheckBox("Remember me");
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);


        grid.add(new Label("Username:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(password, 1, 1);
        grid.add(saveCredentials, 1, 2);
        grid.add(loginButton, 1, 3);
        grid.add(progressIndicator, 1, 4);

        // Enable/Disable login button depending on whether a username was entered.
        loginButton.setDisable(true);

        root.getChildren().setAll(grid);
        // Do some validation (using the Java 8 lambda syntax).
        username.textProperty().addListener((observable, oldValue, newValue) ->
        {
            loginButton.setDisable(newValue.trim().isEmpty());
        });
        primaryStage.setScene(scene);
        primaryStage.show();


        if (FileReader.isFileExists()) {
            userPreferences = getCredentialsFromFile();
            username.setText(userPreferences.getUserName());
            password.setText(getPassword(userPreferences.getCredentials()));
            loginButton.setDisable(false);
            saveCredentials.setVisible(false);
        }

        loginButton.setOnAction(event -> {
            userPreferences.setUserName(username.getText());
            userPreferences.setCredentials(encodeCredentials(username.getText() + ":" + password.getText()));

            Platform.runLater(() -> {
                progressIndicator.setVisible(true);
            });

            Thread thread1 = new Thread(() -> {
                progressIndicator.setVisible(true);
                int code = 0;
                try {
                    System.out.println(Thread.currentThread());
                    code = basicAuthorization(userPreferences);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("ex");
                }
                int finalCode = code;
                Platform.runLater(() -> {
                    if (finalCode == 200) {
                        System.out.println("Code: " + finalCode);
                        if (saveCredentials.isSelected()){
                            try {
                                FileReader.saveUserPreferences(userPreferences);

                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            new LogJiraWorkUI().start(new Stage());
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("Error");
                        }
                    } else{
                        new Alert(Alert.AlertType.INFORMATION, "Code " + String.valueOf(finalCode)).show();
                    }
                });


            });
            thread1.setDaemon(true);
            thread1.start();
            System.out.println(Thread.currentThread());
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
