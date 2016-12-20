package sample;

import com.jfoenix.controls.JFXDatePicker;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Ali on 13.12.2016.
 */
public class LogTodayAuto {
    private Timeline timeline;
    TextField taskName = new TextField();
    GridPane grid= new GridPane();
    CheckBox autoEnable = new CheckBox();
    JFXDatePicker datePicker = new JFXDatePicker();
    private Label timerLabel = new Label();
    private static DateTimeFormatter SHORT_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    LocalTime time = LocalTime.now();
    Duration t = Duration.valueOf(time.toSecondOfDay()+"s");
    private StringProperty stringProperty = new SimpleStringProperty();



    public GridPane getContent(){
        datePicker.setShowTime(true);
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
        checkBox();


        grid.add(new Label("Task id: "), 0, 0);
        grid.add(taskName, 1, 0);
        grid.add(new Label("Enable: "), 0, 1);
        grid.add(autoEnable, 1, 1);
        grid.add(datePicker, 1, 2);
        grid.add(timerLabel, 1, 3);
    }

    public void checkBox(){
        autoEnable.setOnAction(event->{
            if(autoEnable.isSelected()) {
                LocalDate autoLogTime = datePicker.getValue();
                taskName.setDisable(true);
                datePicker.setDisable(true);
                    timeline = new Timeline(
                            new KeyFrame(Duration.seconds(1),
                                    ev -> {
                                        Duration duration = ((KeyFrame) ev.getSource()).getTime();
                                        t = t.subtract(duration);
                                        time = LocalTime.ofSecondOfDay((long)t.toSeconds());
                                        stringProperty.set(time.format(SHORT_TIME_FORMATTER));
                                    })
                    );

                    timeline.setCycleCount(Animation.INDEFINITE);
                    timeline.play();
                    timerLabel.textProperty().bind(stringProperty);
                    timerLabel.setTextFill(Color.RED);
                    timerLabel.setStyle("-fx-font-size: 3em;");
                }

            else {
                timeline.stop();
                taskName.setDisable(false);
                datePicker.setDisable(false);
            }
        });
    }


}
