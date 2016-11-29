package sample.utils;

import structure.JiraIssue;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Formatter;
import java.util.HashMap;

/**
 * Created by marie on 26.11.16.
 */
public class JiraBasicRest {

    public static final String JIRA_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final String DATE = "yyyy-MM-dd";
    public static void main(String args []) throws IOException {
        String basicIssue  = "AUT-10223";
        String cred = "YWxpLm5hZmZhYTo2e0swdSQyeXpa";
        int days = 5;
//        ArrayList<JiraIssue> issues = TestHttp.getLogWorkWithCredAndDays(cred,days);
        HashMap<String,String> dateAndLogTime = TestHttp.getLogWork(cred,days);
        JiraIssue issueToLog = TestHttp.getIssueListForLog(basicIssue,dateAndLogTime,days);
        TestHttp.logWork(issueToLog,cred);

        System.out.println(issueToLog);

    }
}
