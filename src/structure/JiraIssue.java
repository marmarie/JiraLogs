package structure;

import java.util.LinkedHashMap;

/**
 * Created by ali.naffaa on 26.11.2016.
 */
public class JiraIssue {

    String id;
    LinkedHashMap<String, String> workLogs;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LinkedHashMap<String, String> getWorkLogs() {
        return workLogs;
    }

    public void setWorkLogs(LinkedHashMap<String, String> workLogs) {
        this.workLogs = workLogs;
    }

    public JiraIssue(String id, LinkedHashMap<String, String> workLogs) {
        this.id = id;
        this.workLogs = workLogs;
    }
    public JiraIssue() {
        id ="";
        workLogs = new LinkedHashMap<>();
    }

}
