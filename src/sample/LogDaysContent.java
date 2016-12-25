package sample;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import sample.utils.Helper;
import sample.utils.TestHttp;

import java.util.concurrent.CompletableFuture;

/**
 * Created by marie on 08.12.16.
 */
public class LogDaysContent {

    TextField taskName = new TextField(){
        @Override
        public void replaceText(int start, int end, String text) {
            if (Helper.isCorrectInputForTaskId(taskName.getText(),text)) {
                super.replaceText(start, end, text);

            }
        }
    };


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
    GridPane grid= new GridPane();


    public void logTime(){
        logWork.setOnAction((ActionEvent event) -> {

            String taskN = taskName.getText();
            String taskT = taskTime.getText();

            logWork.setDisable(true);
            taskName.setDisable(true);
            taskTime.setDisable(true);
            CompletableFuture.supplyAsync(() -> {
                try {
                    TestHttp.logWork(LoginPage3.getUserPreferences().getCredentials(), Helper.getIssue(taskN, taskT));
                    grid.add(new Label("Logged " + taskTime.getText() + " to " + taskName.getText()), 1, 3);
                } catch (Exception e) {
                    grid.add(new Label(e.getCause().toString()), 1, 3);
                }

                return 0;
            });
            logWork.setDisable(false);
            taskName.setDisable(false);
            taskTime.setDisable(false);
        });
    }



    public void setGrid(){
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));
    }

    public void addElementsToGrid(){
        grid.add(new Label("Task id: "), 0, 0);
        grid.add(taskName, 1, 0);
        grid.add(new Label("Time:  "), 0, 1);
        grid.add(taskTime, 1, 1);
        grid.add(logWork, 1, 2);
    }

    private boolean isCorrectSymbol(String symbol){
        if(symbol.matches("[0-9]")||symbol.matches("[h|m|d]"))
            return true;
        else
            return false;
    }

    private boolean isCorrectAllText(String all){
        if((all.matches("^\\d{1,3}|^\\d{1,5}+."))&&all.replaceAll("[0-9]","").length()<=1)
            return true;
        else
            return false;
    }





    public GridPane getContent(){
        setGrid();
        logWork.setDisable(true);
        taskName.setPromptText("AUT-999");
        taskTime.setPromptText("e.g. 1d 1h 1m");
        enableLogWork();
        logTime();
        addElementsToGrid();

       return grid;
    }

    private void enableLogWork(){
        taskName.setOnKeyTyped(event -> {
            String newText = event.getCharacter();
            if(Helper.isCorrectTaskId(taskName.getText()+newText)&&!taskTime.getText().isEmpty())
                logWork.setDisable(false);
            else
                logWork.setDisable(true);
        });

        taskTime.setOnKeyTyped(event -> {
            if(Helper.isCorrectTaskId(taskName.getText())&&!taskTime.getText().isEmpty())
                logWork.setDisable(false);
            else
                logWork.setDisable(true);
        });
    }
}
