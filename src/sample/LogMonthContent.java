package sample;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import sample.utils.Helper;
import sample.utils.TestHttp;

/**
 * Created by Ali on 12.12.2016.
 */
public class LogMonthContent {
    TextField taskName = new TextField();
    Button logWork = new Button("Log Work");
    GridPane grid= new GridPane();
    private DatePicker checkInDatePicker;

    public GridPane getContent(){
        setGrid();

        logTime();
        addElementsToGrid();

        return grid;
    }

    public void setGrid(){
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));
    }

    public void addElementsToGrid(){
        grid.add(new Label("Task Name: "), 0, 0);
        grid.add(taskName, 1, 0);
        grid.add(logWork, 0, 1);
    }

    public void logTime(){
        logWork.setOnAction((ActionEvent event) -> {


            logWork.setDisable(true);
            try {

                logWork.setDisable(false);
            } catch (Exception e) {
                grid.add(new Label(e.getCause().toString()), 1, 3);
            }

        });
    }

}
