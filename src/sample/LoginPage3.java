package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import sample.utils.FileReader;
import structure.model.UserPreferences;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import static sample.utils.FileReader.getCredentialsFromFile;
import static sample.utils.Helper.encodeCredentials;
import static sample.utils.Helper.getPassword;
import static sample.utils.TestHttp.basicAuthorization;


/**
 * Created by marie on 06.12.16.
 */
public class LoginPage3 extends Application {

    static UserPreferences userPreferences = new UserPreferences();

    Integer result;

    @Override
    public void start(Stage primaryStage) throws Exception {

        final int[] code = {0};
        // Create the custom dialog.
        Pane root = new Pane();
        Scene scene = new Scene(root, 350, 300);
        primaryStage.setTitle("Jira");
        primaryStage.setResizable(false);
        primaryStage.setFullScreen(false);


        Button loginButton = new Button("Login");

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


        loginButton.setOnAction((ActionEvent event) -> {

            progressIndicator.setVisible(true);
            loginButton.setDisable(true);
            userPreferences.setUserName(username.getText());
            userPreferences.setCredentials(encodeCredentials(username.getText() + ":" + password.getText()));

                CompletableFuture.supplyAsync(() -> {
                    try {
                        result = basicAuthorization(userPreferences);
                        progressIndicator.setVisible(false);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Platform.runLater(() -> {
                        if (result == 200) {
                            if (saveCredentials.isSelected()){
                                try {
                                    FileReader.saveUserPreferences(userPreferences);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            try {
                                new LogJiraWorkUI().start(new Stage());
                                primaryStage.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else{
                            new Alert(Alert.AlertType.INFORMATION, "Code " + String.valueOf(result)).show();
                        }
                    });
                    loginButton.setDisable(false);
                    return result;

                });

        });



    }

    public static UserPreferences getUserPreferences(){
        return userPreferences;
    }

    public static void main(String[] args) {
        launch(args);
    }
}