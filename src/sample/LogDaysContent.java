package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import sample.utils.Helper;
import sample.utils.TestHttp;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Created by marie on 08.12.16.
 */
public class LogDaysContent {

    ComboBox<String> taskNames = new ComboBox<>();


//    TextField taskName = new TextField(){
//        @Override
//        public void replaceText(int start, int end, String text) {
//            if (Helper.isCorrectInputForTaskId(taskName.getText(),text)) {
//                super.replaceText(start, end, text);
//            }
//        }
//    };

    TextField taskTime = new TextField(){
        @Override
        public void replaceText(int start, int end, String text) {
            String all = taskTime.getText()+text;
            if (isCorrectSymbol(text)&&isCorrectAllText(all)||(start!=end&&text.matches("[0-9]"))||text.isEmpty()) {
                super.replaceText(start, end, text);
                if(text.matches("[h|m|d]")&&all.replaceAll("[h|m|d]","").length()>0){
                    String time = Helper.getTimeInSeconds(all);

                    if(Integer.parseInt(time)>28800){
                        taskTime.setText("1d");
                    }
                }
            }
        }
    };
    Button logWork = new Button("Log Work");
    Button logTodayTasks = new Button("Do it!");
    Label logTasks = new Label("Or you can log all tasks you've been working on today");
    GridPane grid= new GridPane();
    int i = 5;

    public void logTime(){
        logWork.setOnAction((ActionEvent event) -> {

            String taskN = taskNames.getValue();
            String taskT = taskTime.getText();

            disableAllButtonsOnUI(true);
            CompletableFuture.supplyAsync(() -> {
                try {
                    TestHttp.logWork(Helper.getIssue(taskN, taskT));
                    grid.add(new Label("Logged " + taskTime.getText() + " to " + taskNames.getValue()), 1, 3);
                } catch (Exception e) {
                    grid.add(new Label(e.getCause().toString()), 1, 3);
                }
                return 0;
            });
            disableAllButtonsOnUI(false);
        });
    }

    public void logTasks(){
        logTodayTasks.setOnAction((ActionEvent event) -> {
            disableAllButtonsOnUI(true);

            CompletableFuture.supplyAsync(() -> {
                try {
                    List<String> tasks = TestHttp.log8hToTodayTasks();
                    for (String id: tasks) {
                        grid.add(new Label("Logged " + String.valueOf(8/tasks.size()) + "hours to " + id), 1, i++);
                    }
                } catch (Exception e) {
                    grid.add(new Label(e.getCause().toString()), 1, i);
                }
                return 0;
            });
            disableAllButtonsOnUI(false);
        });
    }

    public void disableAllButtonsOnUI(boolean value){
        logWork.setDisable(value);
        taskNames.setDisable(value);
        taskTime.setDisable(value);
        logTodayTasks.setDisable(value);
    }

    public void setGrid(){
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));
    }

    public void addElementsToGrid(){
        grid.add(new Label("Task id: "), 0, 0);
        grid.add(taskNames, 1, 0);
        grid.add(new Label("Time:  "), 0, 1);
        grid.add(taskTime, 1, 1);
        grid.add(logWork, 1, 2);
        grid.add(logTasks,1,4);
        logTasks.setMinWidth(100);
        grid.add(logTodayTasks,4,4);
    }

    private boolean isCorrectSymbol(String symbol){
        return symbol.matches("[0-9]") || symbol.matches("[h|m|d]");
    }

    private boolean isCorrectAllText(String all){
        return (all.matches("^\\d{1,3}|^\\d{1,5}+.")) && all.replaceAll("[0-9]", "").length() <= 1;
    }

    public GridPane getContent(){
        setGrid();
        logWork.setDisable(true);
        setTaskNames();
        taskTime.setPromptText("e.g. 1d 1h 1m");
        enableLogWork();
        logTime();
        logTasks();
        addElementsToGrid();
       return grid;
    }

    private void setTaskNames(){
       taskNames.getItems().addAll(TestHttp.getIssuesForToday());
       taskNames.setEditable(true);
       taskNames.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
           if (!Helper.isCorrectInputForTaskId(oldValue, newValue)) {taskNames.getEditor().deleteNextChar();}
       });
    }

    private void enableLogWork(){
        taskNames.getEditor().setOnKeyTyped(event -> {
            String newText = event.getCharacter();
            if(Helper.isCorrectTaskId(taskNames.getValue()+newText)&&!taskTime.getText().isEmpty())
                logWork.setDisable(false);
            else
                logWork.setDisable(true);
        });


        taskTime.setOnKeyTyped(event -> {
            if(Helper.isCorrectTaskId(taskNames.getValue())&&!taskTime.getText().isEmpty())
                logWork.setDisable(false);
            else
                logWork.setDisable(true);
        });
    }
}
