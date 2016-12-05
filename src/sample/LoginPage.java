    package sample;


    import javafx.application.Application;
    import javafx.application.Platform;
    import javafx.geometry.Insets;
    import javafx.scene.Node;
    import javafx.scene.control.*;
    import javafx.scene.layout.GridPane;
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
    public class LoginPage extends Application {

        UserPreferences userPreferences = new UserPreferences();


        @Override
        public void start(Stage primaryStage) throws Exception {

            // Create the custom dialog.
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle("Login Dialog");
            dialog.setHeaderText("Look, a Custom Login Dialog");


            // Set the icon (must be included in the project).


            // Set the button types.
            ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
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
            grid.add(progressIndicator, 1, 3);

            // Enable/Disable login button depending on whether a username was entered.
            Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
            loginButton.setDisable(true);


            // Do some validation (using the Java 8 lambda syntax).
            username.textProperty().addListener((observable, oldValue, newValue) ->
            {
                loginButton.setDisable(newValue.trim().isEmpty());
            });
            dialog.getDialogPane().setContent(grid);

            // Request focus on the username field by default.
            Platform.runLater(() -> username.requestFocus());

            if (FileReader.isFileExists()) {
                userPreferences = getCredentialsFromFile();
                username.setText(userPreferences.getUserName());
                password.setText(getPassword(userPreferences.getCredentials()));
                loginButton.setDisable(false);
                saveCredentials.setVisible(false);
            }

            // Convert the result to a username-password-pair when the login button is clicked.
            dialog.setResultConverter(dialogButton ->
            {
                if (dialogButton == loginButtonType) {
                    return new Pair<>(username.getText(), password.getText());
                }
                return null;
            });

            Optional<Pair<String, String>> result = dialog.showAndWait();
            progressIndicator.setVisible(true);
            Thread thread1 = new Thread(() -> {
                try {
                    result.ifPresent(usernamePassword ->
                    {
                        userPreferences.setUserName(usernamePassword.getKey());
                        userPreferences.setCredentials(encodeCredentials(usernamePassword.getKey() + ":" + usernamePassword.getValue()));
                        int code=0;
                        try {
                            System.out.println("Thread 2 ");
                            code = basicAuthorization(userPreferences);
                                    progressIndicator.setVisible(true);
                            } catch (IOException e) {
                            e.printStackTrace();
                            System.out.println("ex");
                        }
                            if (code == 200) {
                                System.out.println("Code: "+code);
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

                } catch (Exception es) {
                    es.printStackTrace();
                }
            });
            thread1.start();

        }


        public static void main(String[] args) {
            launch(args);
        }
    }
