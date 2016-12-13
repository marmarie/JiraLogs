package sample;

import com.jfoenix.controls.JFXDatePicker;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * Created by Ali on 13.12.2016.
 */
public class LogTodayAuto {
    TextField taskName = new TextField();
    GridPane grid= new GridPane();
    CheckBox autoEnable = new CheckBox();
    JFXDatePicker datePicker = new JFXDatePicker();

    public GridPane getContent(){
        setGrid();
        addElementsToGrid();
        return grid;
    }
    public void setGrid(){
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 20, 10));
    }

    public void addElementsToGrid(){
        datePicker.setShowTime(true);
        grid.add(new Label("Task id: "), 0, 0);
        grid.add(taskName, 1, 0);
        grid.add(new Label("Enable: "), 0, 1);
        grid.add(autoEnable, 1, 1);
        grid.add(datePicker, 1, 2);
    }

}
