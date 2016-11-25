package sample;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sample.utils.FileReader;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        Pane root = new Pane();


        primaryStage.setTitle("Jira");
        Text errorName = new Text(100,140,"");
        Text errorPass = new Text(100,190,"");




        Scene scene = new Scene(root,350,300);
        Button loginButton = new Button("Login");
        loginButton.setTranslateX(100);
        loginButton.setTranslateY(215);
        loginButton.setAlignment(Pos.CENTER);

        TextField name = new TextField();

        name.setPromptText("Name");
        name.setTranslateX(100);
        name.setTranslateY(100);

        PasswordField pass = new PasswordField();
        pass.setPromptText("Password");
        pass.setTranslateX(100);
        pass.setTranslateY(150);

        CheckBox remember = new CheckBox();
        remember.setText("Remember me");
        remember.setTranslateX(215);
        remember.setTranslateY(200);
        remember.setAlignment(Pos.CENTER_RIGHT);

        Label hello = new Label();
        hello.setAlignment(Pos.TOP_CENTER);

        loginButton.setOnAction(event -> {
            if(name.getText().isEmpty()||pass.getText().isEmpty()){
            if(name.getText().isEmpty()){
                errorName.setText("Please, enter user name");
                errorName.setFill(Color.RED);
            }
            else
                errorName.setText("");
            if (pass.getText().isEmpty()) {
                errorPass.setText("Please, enter password");
                errorPass.setFill(Color.RED);
            }
            else
                errorPass.setText("");

            } else {
                if (remember.isSelected()) {
                    try {
                        FileReader.saveCredentials(name.getText(), pass.getText());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    hello.setText("Hello, " + name.getText());
                    root.getChildren().setAll(hello);
                }
            }
        });



        if(FileReader.isFileExists()) {
            hello.setText("Hello, " + FileReader.getCredentials()[0]);
            root.getChildren().setAll(hello);
        }
        else
            root.getChildren().setAll(loginButton, name, pass, remember,errorName,errorPass);

        primaryStage.setScene(scene);

        primaryStage.show();
    }



    public static void main(String[] args) {
        launch(args);
    }
}
