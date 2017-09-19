package com.ringcentral.qa.utils;

import com.ringcentral.qa.structure.model.JiraIssue;
import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by marie on 26.11.16.
 */
public class JiraBasicRest {

    public static final String DATE = "yyyy-MM-dd";
    public static void main(String args []) throws IOException, JSONException {
        String basicIssue = "AUT-10223";
        int days = 5;
        HashMap<String,String> dateAndLogTime = TestHttp.getLogWork(days);
        JiraIssue issueToLog = TestHttp.getIssueListForLog(basicIssue,dateAndLogTime,days);
        TestHttp.logWork(issueToLog);


    }
}
