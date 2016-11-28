package sample.utils;

import structure.JiraIssue;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by marie on 26.11.16.
 */
public class JiraBasicRest {
    public static void main(String args []) throws IOException {
        String basicIssue  = "AUT-10223";
        int days = 50;
        ArrayList<JiraIssue> issues = TestHttp.getLogWorkWithCredAndDays("",days);
        JiraIssue issueToLog = TestHttp.getIssueListForLog(basicIssue,issues);
        System.out.println(issueToLog);

    }
}
