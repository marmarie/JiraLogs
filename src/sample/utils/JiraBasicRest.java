package sample.utils;

import org.json.JSONException;
import structure.Client;
import structure.JiraIssue;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by marie on 26.11.16.
 */
public class JiraBasicRest {

    public static final String JIRA_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final String DATE = "yyyy-MM-dd";
    public static void main(String args []) throws IOException, JSONException {
        String basicIssue  = "AUT-10223";
        String cred = "";
        int days = 31;
//        ArrayList<JiraIssue> issues = TestHttp.getLogWorkWithCredAndDays(cred,days);
        HashMap<String,String> dateAndLogTime = TestHttp.getLogWork(cred,days);
        JiraIssue issueToLog = TestHttp.getIssueListForLog(basicIssue,dateAndLogTime,days);
        Client.logWork(cred,issueToLog);

        System.out.println(issueToLog);

    }
}
