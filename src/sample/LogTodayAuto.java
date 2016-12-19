package sample;

import com.jfoenix.controls.JFXDatePicker;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import javax.jnlp.IntegrationService;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    private Duration time = Duration.seconds(150), splitTime = Duration.ZERO;
    private DoubleProperty timeSeconds =  new SimpleDoubleProperty();
    private DoubleProperty splitTimeSeconds = new SimpleDoubleProperty();
    String strTime = "";



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
                taskName.setDisable(true);
                datePicker.setDisable(true);
                if (timeline != null) {
                    splitTime = Duration.ZERO;
                    splitTimeSeconds.set(splitTime.toSeconds());
                } else {
                    timeline = new Timeline(
                            new KeyFrame(Duration.seconds(1),
                                    t -> {
                                        Duration duration = ((KeyFrame) t.getSource()).getTime();
                                        time = time.subtract(duration);
                                        timeSeconds.set(time.toSeconds());
                                    })
                    );

                    timeline.setCycleCount(Timeline.INDEFINITE);
                    timeline.play();
                    int seconds = (int) timeSeconds.getValue().doubleValue();
                    timerLabel.textProperty().setValue(String.format("%d:%02d:%02d",seconds/ 3600, (seconds % 3600) / 60, (seconds % 60)));
                    timerLabel.setTextFill(Color.RED);
                    timerLabel.setStyle("-fx-font-size: 4em;");
                }
            }
            else {
                timeline.stop();
                taskName.setDisable(false);
                datePicker.setDisable(false);
            }
        });
    }


}
