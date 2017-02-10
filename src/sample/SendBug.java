package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.StringConverter;
import sample.utils.TestHttp;
import structure.model.UserPreferences;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by marie on 10.02.17.
 */
public class SendBug {
    GridPane grid= new GridPane();

    Button loadBugs = new Button("Load bugs");
    Button sendBugs = new Button("Send Bugs");
    TextField email = new TextField(LoginPage3.userPreferences.getUserName() + "@ab-soft.net");
    public static final String Column1MapKey = "A";
    public static final String Column2MapKey = "B";

    TableColumn<Map, String> firstDataColumn = new TableColumn<>("Bug");
    TableColumn<Map, String> secondDataColumn = new TableColumn<>("Summary");
    TableView tableView = new TableView<>(generateDataInMap());

    public void setGrid(){
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));
    }


    public void setElements(){
      //  email.setPrefColumnCount(15);

        loadBugs.setOnAction((ActionEvent e) -> {
            addTableWithBugs();
            grid.add(tableView,1,2,20,10);
                });
    }


    public GridPane getContent(){
        setGrid();
        setElements();
        addElementsToGrid();
        return grid;
    }

    public void addElementsToGrid(){
    grid.add(new Label("Your E-Mail:"), 0,0,5,1);
        grid.add(email, 6,0,6,1);
        grid.add(loadBugs, 13,0,7,1);


    }

    public void addTableWithBugs(){
        firstDataColumn.setCellValueFactory(new MapValueFactory(Column1MapKey));
        firstDataColumn.setMinWidth(100);
        secondDataColumn.setCellValueFactory(new MapValueFactory(Column2MapKey));
        secondDataColumn.setMinWidth(570);
        tableView.setPrefHeight(800);

        tableView.setEditable(true);
        tableView.getSelectionModel().setCellSelectionEnabled(true);
        tableView.getColumns().setAll(firstDataColumn, secondDataColumn);
        Callback<TableColumn<Map, String>, TableCell<Map, String>>
                cellFactoryForMap = (TableColumn<Map, String> p) ->
                new TextFieldTableCell(new StringConverter() {
                    @Override
                    public String toString(Object t) {
                        return t.toString();
                    }
                    @Override
                    public Object fromString(String string) {
                        return string;
                    }
                });
        firstDataColumn.setCellFactory(cellFactoryForMap);
        secondDataColumn.setCellFactory(cellFactoryForMap);
    }

    private ObservableList<Map> generateDataInMap() {
        HashMap<String, String> bugList = TestHttp.getList();

        ObservableList<Map> allData = FXCollections.observableArrayList();
        for (Map.Entry<String, String> entry : bugList.entrySet()) {
            Map<String, String> dataRow = new HashMap<>();

            dataRow.put(Column1MapKey, entry.getKey());
            dataRow.put(Column2MapKey, entry.getValue());

            allData.add(dataRow);
        }
        return allData;
    }


}
