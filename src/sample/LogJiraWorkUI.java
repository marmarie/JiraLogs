package sample;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Created by marie on 05.12.16.
 */
public class LogJiraWorkUI extends Application {
//    Stage primaryStage = new Stage();


    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Tabs");
        Group root = new Group();
        Scene scene = new Scene(root, 370, 250, Color.WHITE);

        TabPane tabPane = new TabPane();

        BorderPane borderPane = new BorderPane();

            Tab tabD = new Tab();
            tabD.setClosable(false);

            Tab tabS = new Tab();
            tabS.setClosable(false);

            tabD.setGraphic(new Label("Log days"));
            tabS.setGraphic(new Label("Log tasks"));
             HBox hbox = new HBox();

            // hbox.getChildren().add(new Label("ALIALI"));
             hbox.setAlignment(Pos.CENTER);
             tabD.setContent(new LogMonthContent().getContent());
             tabS.setContent(new LogDaysContent().getContent());
            tabPane.getTabs().addAll(tabD, tabS);


        tabPane.setSide(Side.LEFT);
        tabPane.setTabMinWidth(30);
        tabPane.setTabMinHeight(70);
        // bind to take available space
        borderPane.prefHeightProperty().bind(scene.heightProperty());
        borderPane.prefWidthProperty().bind(scene.widthProperty());
      //  borderPane.setPrefWidth(text1.getScaleX());
        borderPane.setLeft(tabPane);
        root.getChildren().add(borderPane);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String...args){
        launch(args);
    }
}
