package sample;

import com.jfoenix.controls.JFXDatePicker;
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
import org.json.JSONException;
import sample.utils.Helper;
import sample.utils.TestHttp;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Ali on 13.12.2016.
 */
public class LogTodayAuto {
    private Timeline timeline;
    private GridPane grid= new GridPane();
    private CheckBox autoEnable = new CheckBox();
    private JFXDatePicker datePicker = new JFXDatePicker();
    private Duration t ;

    private Label timerLabel = new Label();
    private static DateTimeFormatter SHORT_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private StringProperty stringProperty = new SimpleStringProperty();

    private TextField taskName = new TextField(){
        @Override
        public void replaceText(int start, int end, String text) {
            if (Helper.isCorrectInputForTaskId1(taskName.getText(),text)) {
                super.replaceText(start, end, text);
            }
        }
    };

    public GridPane getContent(){
        autoEnable.setDisable(true);
        datePicker.setShowTime(true);
        setGrid();
        addElementsToGrid();
        enableCheckBox();
        return grid;
    }
    private void setGrid(){
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 20, 10));
        taskName.setPromptText("AUT-999");
    }

    private void enableCheckBox(){
        taskName.setOnKeyTyped(event -> {
            String newText = event.getCharacter();
            boolean disable = !(Helper.isCorrectTaskId(taskName.getText() + newText) && datePicker.getTime() != null);
            autoEnable.setDisable(disable);
        });

        datePicker.setOnMouseExited(event -> {
            boolean disable = !(Helper.isCorrectTaskId(taskName.getText())&&datePicker.getTime()!=null);
            autoEnable.setDisable(disable);
        });
    }

    private void addElementsToGrid(){
        datePicker.setEditable(false);
        checkBox();

        grid.add(new Label("Task id: "), 0, 0);
        grid.add(taskName, 1, 0);
        grid.add(new Label("Enable: "), 0, 1);
        grid.add(autoEnable, 1, 1);
        grid.add(datePicker, 1, 2);
        grid.add(timerLabel, 1, 3);
    }

    private void checkBox(){
        autoEnable.setOnAction(event->{
            if(autoEnable.isSelected()) {
                final LocalTime[] autoLogTime = {datePicker.getTime()};
                String timeInSeconds = autoLogTime[0].isBefore(LocalTime.now()) ? "86400" : "";
                t = Duration.valueOf(autoLogTime[0].toSecondOfDay() - LocalTime.now().toSecondOfDay() + timeInSeconds + "s");
                taskName.setDisable(true);
                datePicker.setDisable(true);
                    timeline = new Timeline(
                            new KeyFrame(Duration.seconds(1),
                                    ev -> {
                                        Duration duration = ((KeyFrame) ev.getSource()).getTime();

                                        t = t.subtract(duration);
                                        if(t.toSeconds()==0){
                                            t = Duration.valueOf(86400+"s");
                                            stringProperty.set("Log!");
                                            CompletableFuture.supplyAsync(() -> {
                                                TestHttp.logWork(Helper.getIssue(taskName.getText(),"8h"));
                                                return 0;
                                        });
                                        }
                                        else {
                                            autoLogTime[0] = LocalTime.ofSecondOfDay((long) t.toSeconds());
                                            stringProperty.set(autoLogTime[0].format(SHORT_TIME_FORMATTER));
                                        }
                                    })
                    );
                    //(int)t.toSeconds()

                    timeline.setCycleCount(Timeline.INDEFINITE);
                    timeline.play();
                    timerLabel.textProperty().bind(stringProperty);
                    timerLabel.setTextFill(Color.RED);
                }
            else {
                timeline.stop();
                taskName.setDisable(false);
                datePicker.setDisable(false);
            }
        });
    }


}
