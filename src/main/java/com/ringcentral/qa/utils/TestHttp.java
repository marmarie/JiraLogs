package com.ringcentral.qa.utils;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ringcentral.qa.LoginPage3;
import com.ringcentral.qa.structure.model.Entries;
import com.ringcentral.qa.structure.model.JiraIssue;
import com.ringcentral.qa.structure.model.Result;
import com.ringcentral.qa.structure.model.Worklog;
import javafx.util.Pair;
import org.apache.http.HttpRequest;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Logger;

import static com.jayway.jsonpath.JsonPath.read;
import static com.ringcentral.qa.utils.Helper.getCalendarDays;
import static com.ringcentral.qa.utils.Helper.getDate;
import static com.ringcentral.qa.utils.Helper.getUserName;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.WARNING;


/**
 * Created by ali.naffaa and mariya.azoyan 18.12.2016.
 */

public class TestHttp  {

    private static HashMap<String, String> headersMap = new HashMap<>();

    static {
    headersMap.put("Content-Type", "application/json");
   }

    public static void putCredentials() {
       // headersMap.put("Authorization", "Basic " + LoginPage3.getUserPreferences().getCredentials());
        headersMap.put("Authorization", "Basic " + FileReader.getCredentialsFromFile().getCredentials());
    }

    public static void putHeadersInRequest(HttpRequest request, HashMap<String, String> headers) {
        for (String name : headers.keySet()) {
            request.addHeader(name, headers.get(name));
        }
    }

    public static JSONObject makeJSON(String date, String time){
        JSONObject f = new JSONObject();
            try {
                if(!date.equals(""))
                f.put("started", date);
                if(time.contains("s"))
                    f.put("timeSpentSeconds", time.replace("s",""));
                else
                    f.put("timeSpent", time);
            } catch (JSONException e) {
                Logger.getAnonymousLogger().log(WARNING, e.getMessage());
            }
        return f;
    }

    private static boolean makePost(HashMap<String, String> headers, String url, JSONObject json) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(url);
            putHeadersInRequest(request, headers);
            request.setEntity(new StringEntity(json.toString()));
            try (CloseableHttpResponse response = client.execute(request)) {
                  if (response.getStatusLine().getStatusCode() == 201)
                        return true;
                }
            } catch (IOException e) {
            Logger.getAnonymousLogger().log(WARNING, e.getMessage());
        }
        return false;
    }

    public static void logWork(JiraIssue jiraIssue) {
        putCredentials();
        for(String dateIssue : jiraIssue.getWorkLogs().keySet()) {
            String time = jiraIssue.getWorkLogs().get(dateIssue);
            System.out.println("log to "+jiraIssue.getId()+" date = " +dateIssue +" time to log="+ time);
            makePost(headersMap, "https://jira.ringcentral.com/rest/api/latest/issue/" + jiraIssue.getId() + "/worklog", makeJSON(dateIssue+"T15:01:00.000+0000", time+"s"));
        }
    }

    public static List<String> log8hToTodayTasks(){
        putCredentials();
        List<String> taskIds =  read(getTasksJSON(), "$.issues[*].key");
        String timeInSeconds = String.valueOf(28800/taskIds.size());
        String timeInHours = String.valueOf(8/taskIds.size());
        for(String id : taskIds ) {
        Logger.getAnonymousLogger().log(FINE, "log to " + id + " time to log = " + timeInHours + " hours");
        makePost(headersMap, "https://jira.ringcentral.com/rest/api/latest/issue/" + id + "/worklog", makeJSON(LocalDateTime.now().toString() + "+0000", timeInSeconds+"s"));
     }
      //  makePost(headersMap, "https://jira.ringcentral.com/rest/api/latest/issue/AUT-10223/worklog", makeJSON(LocalDateTime.now().toString() + "+0000", "102s"));
        return taskIds;
    }

    public static List<String> getIssuesForToday(){
        putCredentials();
        return read(getTasksJSON(), "$.issues[*].key");
    }


    public static HashMap<String, String> getLogWork(int days) {
        HashMap<String, String> hashMap = new HashMap<>();
        String json = getJson(days);

        Worklog[] w = new Worklog[0];
        try {
            w = new ObjectMapper().readValue(json, Result.class).getWorklog();
        } catch (IOException e) {
            Logger.getAnonymousLogger().log(WARNING, e.getMessage());
        }
        for (Worklog worklog : w) {
            Entries[] entries = worklog.getEntries();
            for (Entries entry : entries) {
                String date = getDate(entry.getStartDate());
                if (hashMap.containsKey(date)) {
                    String fullDate = String.valueOf(Long.valueOf(hashMap.get(date)) + Long.valueOf(entry.getTimeSpent()));
                    hashMap.put(date, fullDate);
                } else
                    hashMap.put(date, entry.getTimeSpent());
            }
        }
        return hashMap;
    }

    public static HashMap<String, String> getLogWork(LocalDate startDate, LocalDate endDate) {
        HashMap<String, String> hashMap = new HashMap<>();
        int days = (int) ChronoUnit.DAYS.between(startDate, LocalDate.now());
        String json = getJson(days);

        Worklog[] w = new Worklog[0];
        try {
            w = new ObjectMapper().readValue(json, Result.class).getWorklog();
        } catch (IOException e) {
            Logger.getAnonymousLogger().log(WARNING, e.getMessage());
        }
        for (Worklog worklog : w) {
            Entries[] entries = worklog.getEntries();
            for (Entries entry : entries) {
                LocalDate date = Instant.ofEpochMilli(Long.valueOf(entry.getStartDate())).atZone(ZoneId.systemDefault()).toLocalDate();
                if (ChronoUnit.DAYS.between(date, endDate)>0) {
                    if (hashMap.containsKey(date.toString())) {
                        String fullDate = String.valueOf(Long.valueOf(hashMap.get(date.toString())) + Long.valueOf(entry.getTimeSpent()));
                        hashMap.put(date.toString(), fullDate);
                    } else
                        hashMap.put(date.toString(), entry.getTimeSpent());
                }
            }
        }
        return hashMap;
    }

    private static String getJson(int days) {
        putCredentials();
        return post(headersMap, "https://jira.ringcentral.com/rest/timesheet-gadget/1.0/raw-timesheet.json?targetUser=" + getUserName(LoginPage3.getUserPreferences().getCredentials()) + "&startDate=" + getDate(days));
    }

    private static String getTasksJSON(){
        putCredentials();
        //https://jira.ringcentral.com/rest/api/2/search?jql=issuetype%20in(%27Dashboard%20report%20message%27,%20%27QA%20Auto%20Sub-Task%27,%20%27QA%20Task%27)%20and%20assignee%20in(%27mariya.azoyan%27)%20AND%20%20updatedDate%20%3E%20startOfDay(-0d)
        return post(headersMap, "https://jira.ringcentral.com/rest/api/2/search?jql=issuetype%20in%20('Dashboard%20report%20message',%20'QA%20Auto%20Sub-Task',%20'QA%20Task')%20AND%20assignee%20in%20("+FileReader.getCredentialsFromFile().getUserName()+")%20AND%20%20updatedDate%20%3E%20startOfDay(-0d)");
    }

    private static String getBugsJson() {
        putCredentials();
        return post(headersMap, "https://jira.ringcentral.com/rest/api/2/search?jql=issuetype%20%3D%20Bug%20and%20created%20%3E%20startOfDay(-0d)%20&fields=key,summary");
    }

    public static HashMap<String,String> getList(){
        String result = getBugsJson();
        List<String> bugKeys =  read(result, "$.issues[*].key");
        List<String> bugSummaries = read(result, "$.issues[*].fields.summary");
        HashMap<String, String> allList = new LinkedHashMap<>();
        int i=0;
        for (String key : bugKeys){
            allList.put(key, bugSummaries.get(i++));
        }
        return allList;
    }

    static String getEmailSignature() {
        HashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        ArrayList<BasicNameValuePair> userName = new ArrayList<>();
        userName.add(new BasicNameValuePair("ln", LoginPage3.getUserPreferences().getUserName()));
        StringBuilder sb = new StringBuilder();
        try( CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost("http://signature.od.ab-soft.net");
            putHeadersInRequest(request, headers);
            request.setEntity(new UrlEncodedFormEntity(userName));
            try (CloseableHttpResponse response = client.execute(request)){
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 65728);
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                } catch (Exception e) {
                    Logger.getAnonymousLogger().log(WARNING, e.getMessage());
                }
            }
        } catch (IOException e) {
            Logger.getAnonymousLogger().log(WARNING, e.getMessage());
        }
        return sb.toString().split("Save this code as your mail signature...\" readonly>")[1].split("</textarea><br>")[0];
    }


    public static Pair<String,String> basicAuthorization() {
        putCredentials();
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("http://jira.ringcentral.com");
            putHeadersInRequest(request, headersMap);
            try (CloseableHttpResponse response = client.execute(request)) {
                return new Pair<>(String.valueOf(response.getStatusLine().getStatusCode()), response.getStatusLine().getReasonPhrase());
            }
        }
        catch (Exception ex) {
            Logger.getAnonymousLogger().log(WARNING, ex.getMessage());
        }
        return new Pair<>("0", "");
    }


    private static String post(HashMap<String, String> headers, String url) {
        StringBuilder sb = new StringBuilder();
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            putHeadersInRequest(request, headers);
            try (CloseableHttpResponse response = client.execute(request)) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 65728);
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                } catch (Exception e) {
                    Logger.getAnonymousLogger().log(WARNING, e.getMessage());
                }
            }
        } catch (IOException e) {
            Logger.getAnonymousLogger().log(WARNING, e.getMessage());
        }
        return sb.toString();
    }

    public static JiraIssue getIssueListForLog(String basicIssueId, HashMap<String, String> dateAndLogTime, int days) {
        JiraIssue needLogJira = new JiraIssue();
        needLogJira.setId(basicIssueId);
        HashMap<String, String> data = getCalendarDays(days);
        for (String id : data.keySet()) {
            if (dateAndLogTime.keySet().contains(id) && !dateAndLogTime.get(id).equals("28800")) {
                String leftToLog = String.valueOf(28800L - Long.parseLong(dateAndLogTime.get(id)));
                needLogJira.addWorkLog(id, leftToLog);
            } else if (!dateAndLogTime.containsKey(id)) {
                needLogJira.addWorkLog(id, data.get(id));
            }
        }
        return needLogJira;
    }

    public static JiraIssue getIssueListForLog(String basicIssueId, HashMap<String, String> dateAndLogTime, LocalDate startDate, LocalDate localEndDate) {
        JiraIssue needLogJira = new JiraIssue();
        needLogJira.setId(basicIssueId);
        HashMap<String, String> data = getCalendarDays(startDate,localEndDate);
        for (String id : data.keySet()) {
            if (dateAndLogTime.keySet().contains(id) && !dateAndLogTime.get(id).equals("28800")) {
                String leftToLog = String.valueOf(28800L - Long.parseLong(dateAndLogTime.get(id)));
                needLogJira.addWorkLog(id, leftToLog);
            } else if (!dateAndLogTime.containsKey(id)) {
                needLogJira.addWorkLog(id, data.get(id));
            }
        }
        return needLogJira;
    }


 public static void main(String...args){
         getIssuesForToday();
 }





}

