package sample;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import sample.utils.Helper;
import sample.utils.TestHttp;

/**
 * Created by marie on 08.12.16.
 */
public class LogDaysContent {

    TextField taskName = new TextField();
    TextField taskTime = new TextField(){
        @Override
        public void replaceText(int start, int end, String text) {
            String all = taskTime.getText()+text;
            if (isCorrectSymbol(text)&&isCorrectAllText(all)||text.isEmpty()) {
                super.replaceText(start, end, text);
            }
        }

    };
    Button logWork = new Button("Log Work");
    GridPane grid= new GridPane();

//    public void validation(){
//        @Override
//        public void replaceText(int start, int end, String text) {
//            while(text.length()!=2){}
//            if (text.matches("^[0-8][h]{1}")) {
//                super.replaceText(start, end, text);
//            }
//        }
//
//        @Override
//        public void replaceSelection(String text) {
//            if (text.contains("^[0-8]h{1}") || text.isEmpty()) {
//                super.replaceSelection(text);
//            }
//        }
//    }



    public void logTime(){
        logWork.setOnAction((ActionEvent event) -> {

            String taskN = taskName.getText();
            String taskT = taskTime.getText();

            logWork.setDisable(true);
            taskName.setText("AUT-10223");
            taskTime.setText("1m");
            try {
                TestHttp.logWork(LoginPage3.getUserPreferences().getCredentials(), Helper.getIssue(taskN, taskT));
                grid.add(new Label("Logged " + taskTime.getText() + " to " + taskName.getText()), 1, 3);
                logWork.setDisable(false);
            } catch (Exception e) {
                grid.add(new Label(e.getCause().toString()), 1, 3);
            }

        });
    }



    public void setGrid(){
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));
    }

    public void addElementsToGrid(){
        grid.add(new Label("Task Name: "), 0, 0);
        grid.add(taskName, 1, 0);
        grid.add(new Label("Time:  "), 0, 1);
        grid.add(taskTime, 1, 1);
        grid.add(logWork, 0, 2);
    }

    private boolean isCorrectSymbol(String symbol){
        if(symbol.matches("[0-9]")||symbol.matches("[h|m|d]"))
            return true;
        else
            return false;
    }

    private boolean isCorrectAllText(String all){
//        if((all.matches("^\\d{1,3}|^\\d{1,5}+."))&&all.replaceAll("[0-9]","").length()<=1)
        if((all.matches("^\\d{1,3}")&&all.matches("^[0-1]+d{0,1}|^[0-8]{0,1}+h{0,1}"))&&all.replaceAll("[0-9]","").length()<=1)
            return true;
        else
            return false;
    }





    public GridPane getContent(){
        setGrid();
        taskName.setPromptText(" AUT-999");
        taskTime.setPromptText("e.g. 1d 1h 1m");

        logTime();
        addElementsToGrid();

       return grid;
    }


}
