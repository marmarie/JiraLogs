package sample;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
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


        // Create the custom dialog.
        Pane root = new Pane();
        Scene scene = new Scene(root, 350, 300);
        primaryStage.setTitle("Jira");


        // Set the icon (must be included in the project).


        // Set the button types.
        Button loginButton = new Button("Login");
//            Button loading = new Button("Loading!!!");
//           loading.setOnAction(n -> new ProgressIndicator().);

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
        grid.add(loginButton, 1, 2);

        // Enable/Disable login button depending on whether a username was entered.
        loginButton.setDisable(true);

        root.getChildren().setAll(grid, progressIndicator);
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
            Thread thread1 = new Thread(() -> {

                userPreferences.setUserName(username.getText());
                userPreferences.setCredentials(encodeCredentials(username.getText() + ":" + password.getText()));
                int code = 0;
                try {
                    System.out.println("Thread 2 ");
                    code = basicAuthorization(userPreferences);
                    progressIndicator.setVisible(true);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("ex");
                }
                if (code == 200) {
                    System.out.println("Code: " + code);
                    if (saveCredentials.isSelected())
                        try {
                            FileReader.saveUserPreferences(userPreferences);
                            new LogJiraWorkUI().start(new Stage());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                } else new Alert(Alert.AlertType.INFORMATION, "Code " + String.valueOf(code)).show();

            });
            thread1.start();
        });
//
//
//            Optional<Pair<String, String>> result = dialog.showAndWait();

//            Thread thread1 = new Thread(() -> {
//                try {
//                    result.ifPresent(usernamePassword ->
//                    {
//                        userPreferences.setUserName(usernamePassword.getKey());
//                        userPreferences.setCredentials(encodeCredentials(usernamePassword.getKey() + ":" + usernamePassword.getValue()));
//                        int code=0;
//                        try {
//                            System.out.println("Thread 2 ");
//                            code = basicAuthorization(userPreferences);
//                                    progressIndicator.setVisible(true);
//                            } catch (IOException e) {
//                            e.printStackTrace();
//                            System.out.println("ex");
//                        }
//                            if (code == 200) {
//                                System.out.println("Code: "+code);
//                                if (saveCredentials.isSelected())
//                                    try {
//                                        FileReader.saveUserPreferences(userPreferences);
//                                        new LogJiraWorkUI().start(new Stage());
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                            } else new Alert(Alert.AlertType.INFORMATION, "Code " + String.valueOf(code)).show();
//
//                    });
//
//                } catch (Exception es) {
//                    es.printStackTrace();
//                }
//            });
//            thread1.start();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
