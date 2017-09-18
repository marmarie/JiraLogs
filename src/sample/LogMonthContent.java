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

    private Button logWork = new Button("Log Work");
    private GridPane grid= new GridPane();
    private DatePicker checkInDatePicker = new DatePicker();
    private DatePicker checkInEndDatePicker = new DatePicker();

    private TextField taskName = new TextField(){
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

        grid.add(new Label("End date: "), 0, 2);
        grid.add(checkInEndDatePicker, 1, 2);
        grid.add(logWork, 1, 3);

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

    private void logDays(LocalDate localDate,LocalDate localEndDate) throws IOException, JSONException {
        String basicIssue = taskName.getText();
        long days = ChronoUnit.DAYS.between(localDate, localEndDate);
        System.out.println(days);


        HashMap<String,String> dateAndLogTime = TestHttp.getLogWork(localDate,localEndDate);
        System.out.println(dateAndLogTime.toString());
        JiraIssue issueToLog = TestHttp.getIssueListForLog(basicIssue,dateAndLogTime,localDate,localEndDate );
        System.out.println(issueToLog.getWorkLogs());
        //TestHttp.logWork(cred,issueToLog);
    }

    public void logTime(){
        logWork.setOnAction((ActionEvent event) -> {
            setAllDisable(true);
            CompletableFuture.supplyAsync(() -> {
                        try {
                            logDays(checkInDatePicker.getValue(),checkInEndDatePicker.getValue());
                        } catch (Exception e) {
                            grid.add(new Label(e.toString()), 1, 3);
                        }
                        return 0;
                    });
           setAllDisable(false);
        });
    }

    private void setAllDisable(boolean disable){
        taskName.setDisable(disable);
        logWork.setDisable(disable);
        checkInDatePicker.setDisable(disable);
        checkInEndDatePicker.setDisable(disable);
    }

    private void enableLogWork(){
        taskName.setOnKeyTyped(event -> {
            String newText = event.getCharacter();
            boolean disable = !(Helper.isCorrectTaskId(taskName.getText()+newText));
            logWork.setDisable(disable);
        });

        checkInDatePicker.setOnKeyTyped(event -> {
            boolean disable = !(Helper.isCorrectTaskId(taskName.getText()));
            logWork.setDisable(disable);
        });

        checkInEndDatePicker.setOnKeyTyped(event -> {
            boolean disable = !(Helper.isCorrectTaskId(taskName.getText()));
            logWork.setDisable(disable);
        });

    }

}
