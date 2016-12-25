package sample;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import org.json.JSONException;
import sample.utils.Helper;
import sample.utils.TestHttp;
import structure.JiraIssue;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

/**
 * Created by Ali on 12.12.2016.
 */
public class LogMonthContent {
    Button logWork = new Button("Log Work");
    GridPane grid= new GridPane();
    private DatePicker checkInDatePicker = new DatePicker();

    TextField taskName = new TextField(){
        @Override
        public void replaceText(int start, int end, String text) {
            if (Helper.isCorrectInputForTaskId(taskName.getText(),text)) {
                super.replaceText(start, end, text);

            }
        }
    };

    public GridPane getContent(){
        setGrid();
        calendarWork();
        logTime();
        addElementsToGrid();

        return grid;
    }

    public void setGrid(){
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 20, 10));
    }

    public void addElementsToGrid(){
        taskName.setPromptText("AUT-999");
        logWork.setDisable(true);
        enableLogWork();
        grid.add(new Label("Task id: "), 0, 0);
        grid.add(taskName, 1, 0);
        grid.add(new Label("Start date: "), 0, 1);

        grid.add(checkInDatePicker, 1, 1);
        grid.add(logWork, 1, 2);

    }

    public void calendarWork(){
        checkInDatePicker.setValue(LocalDate.now());
        final Callback<DatePicker, DateCell> dayCellFactory =
                new Callback<DatePicker, DateCell>() {
                    @Override
                    public DateCell call(final DatePicker datePicker) {
                        return new DateCell() {
                            @Override
                            public void updateItem(LocalDate item, boolean empty) {
                                super.updateItem(item, empty);
                                if (item.isBefore(LocalDate.now().minusDays(41)) || item.isAfter(LocalDate.now())) {
                                    setDisable(true);
                                    setStyle("-fx-background-color: #ffc0cb;");
                                }
                                long p = ChronoUnit.DAYS.between( checkInDatePicker.getValue(), LocalDate.now());
                                setTooltip(new Tooltip("You're about to log work for " + p + " days"));
                            }
                        };
                    }
                };
        checkInDatePicker.setDayCellFactory(dayCellFactory);
    }

    private void logDays(LocalDate localDate) throws IOException, JSONException {
        String basicIssue = taskName.getText();
        String cred = LoginPage3.getUserPreferences().getCredentials();
        long days = ChronoUnit.DAYS.between(localDate, LocalDate.now());
        System.out.println(days);


        HashMap<String,String> dateAndLogTime = TestHttp.getLogWork(cred,Integer.parseInt(String.valueOf(days)));
        System.out.println(dateAndLogTime.toString());
        JiraIssue issueToLog = TestHttp.getIssueListForLog(basicIssue,dateAndLogTime,Integer.parseInt(String.valueOf(days)));
        System.out.println(issueToLog.getWorkLogs());
        //TestHttp.logWork(cred,issueToLog);
    }

    public void logTime(){
        logWork.setOnAction((ActionEvent event) -> {
            taskName.setDisable(true);
            logWork.setDisable(true);
            checkInDatePicker.setDisable(true);
            CompletableFuture.supplyAsync(() -> {
                        try {
                            logDays(checkInDatePicker.getValue());

                        } catch (Exception e) {
                            grid.add(new Label(e.toString()), 1, 3);
                        }
                        return 0;
                    });
            logWork.setDisable(false);
            taskName.setDisable(false);
            checkInDatePicker.setDisable(false);
        });
    }

    private void enableLogWork(){
        taskName.setOnKeyTyped(event -> {
            String newText = event.getCharacter();
            if(Helper.isCorrectTaskId(taskName.getText()+newText))
                logWork.setDisable(false);
            else
                logWork.setDisable(true);
        });

        checkInDatePicker.setOnKeyTyped(event -> {
            if(Helper.isCorrectTaskId(taskName.getText()))
                logWork.setDisable(false);
            else
                logWork.setDisable(true);
        });
    }

}
