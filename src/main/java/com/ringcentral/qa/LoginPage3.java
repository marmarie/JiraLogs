package com.ringcentral.qa;

import com.ringcentral.qa.structure.model.UserPreferences;
import com.ringcentral.qa.utils.FileReader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static com.ringcentral.qa.utils.FileReader.getCredentialsFromFile;
import static com.ringcentral.qa.utils.Helper.encodeCredentials;
import static com.ringcentral.qa.utils.Helper.getPassword;
import static com.ringcentral.qa.utils.TestHttp.basicAuthorization;


/**
 * Created by marie on 06.12.16.
 */
public class LoginPage3 extends Application {

    private static UserPreferences userPreferences = new UserPreferences();
    @Override
    public void start(Stage primaryStage) throws Exception {

        try {
            LogManager.getLogManager().readConfiguration(getClass().getClassLoader().getResourceAsStream("logging.properties"));
        } catch (IOException e) {
            System.err.println("Could not setup logger configuration: " + e.toString());
        }

        // Create the custom dialog.
        Pane root = new Pane();
        Scene scene = new Scene(root, 350, 300);
        primaryStage.setTitle("Jira");
        primaryStage.setResizable(false);
        primaryStage.setFullScreen(false);
        primaryStage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("icon.png")));

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
        username.textProperty().addListener((observable, oldValue, newValue) -> loginButton.setDisable(newValue.trim().isEmpty()));
        primaryStage.setScene(scene);
        primaryStage.show();

        if (FileReader.isFileExists()) {
            userPreferences = getCredentialsFromFile();
            username.setText(userPreferences != null ? userPreferences.getUserName() : null);
            password.setText(getPassword(userPreferences.getCredentials()));

            loginButton.setDisable(false);
            saveCredentials.setVisible(false);
        }
        loginButton.setOnAction((ActionEvent event) -> {
            userPreferences.setCredentials(encodeCredentials(username.getText() + ":" + password.getText()));
            FileReader.saveUserPreferences(userPreferences);
            progressIndicator.setVisible(true);

            loginButton.setDisable(true);
            userPreferences.setUserName(username.getText());
            Logger.getAnonymousLogger().log(Level.INFO, "saved to UP " + username.getText());
            userPreferences.setCredentials(encodeCredentials(username.getText() + ":" + password.getText()));
            Logger.getAnonymousLogger().log(Level.INFO, "Set credentials");

                CompletableFuture.supplyAsync(() -> {
                  Pair<String,String> result = basicAuthorization();
                     //   result = basicAuthorization();
                        Logger.getAnonymousLogger().log(Level.INFO, "basic Authorization");
                        progressIndicator.setVisible(false);

                    Platform.runLater(() -> {
                        if (result.getKey().equals("200")) {
                            if (saveCredentials.isSelected()){
                                try {
                                    FileReader.saveUserPreferences(userPreferences);
                                } catch (Exception e) {
                                    Logger.getAnonymousLogger().log(Level.INFO, e.getMessage());
                                }
                            }
                            try {
                                new LogJiraWorkUI().start(new Stage());
                                primaryStage.close();
                            } catch (Exception e) {
                                Logger.getAnonymousLogger().log(Level.INFO, e.getMessage());
                            }
                        } else{
                            new Alert(Alert.AlertType.INFORMATION, "Can't login because of " + result).show();
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