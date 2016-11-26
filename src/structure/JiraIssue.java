package structure;

import org.joda.time.DateTime;

import java.util.LinkedHashMap;

/**
 * Created by ali.naffaa on 26.11.2016.
 */
public class JiraIssue {

    String id;




    public void setWorkLogs(LinkedHashMap<DateTime, String> workLogs) {
        this.workLogs = workLogs;
    }

    LinkedHashMap<DateTime, String> workLogs;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JiraIssue() {
        id ="";
        workLogs = new LinkedHashMap<>();
    }

    public JiraIssue(String id, LinkedHashMap<DateTime, String> workLogs) {
        this.id = id;
        this.workLogs = workLogs;
    }

    public LinkedHashMap<DateTime, String> getWorkLogs() {
        return workLogs;
    }

    public void addWorkLog(DateTime dateTime, String text){
        workLogs.put(dateTime,text);
    }

}
