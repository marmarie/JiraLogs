package sample.utils;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONException;
import org.json.JSONObject;
import structure.JiraIssue;
import structure.model.Entries;
import structure.model.Result;
import structure.model.UserPreferences;
import structure.model.Worklog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static sample.utils.Helper.*;

/**
 * Created by ali.naffaa and mariya.azoyan 18.08.2016.
 */

public class TestHttp  {

    static HashMap<String, String> headersMap = new HashMap<>();

    static {
    headersMap.put("Content-Type", "application/json");
   }

    public static void putCredentials(String credentials) {
        headersMap.put("Authorization", "Basic " + credentials);
    }

    public static void putHeadersInRequest(HttpRequest request, HashMap<String, String> headers) {
        for (String name : headers.keySet()) {
            request.addHeader(name, headers.get(name));
        }
    }

    public static JSONObject makeJSON(String date, String time) throws JSONException {
        JSONObject f = new JSONObject();
        f.put("started", date);
        if(time.contains("s"))
            f.put("timeSpentSeconds", time.replace("s",""));
        else
            f.put("timeSpent", time);
        return f;
    }

    private static boolean makePost(HashMap<String, String> headers, String url, JSONObject json) throws IOException, JSONException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(url);
            putHeadersInRequest(request, headers);
            request.setEntity(new StringEntity(json.toString()));
            try (CloseableHttpResponse response = client.execute(request)) {
                  if (response.getStatusLine().getStatusCode() == 201)
                        return true;
                }
            }
        return false;
    }

    public static void logWork(String credentials, JiraIssue jiraIssue) throws JSONException, IOException {
        putCredentials(credentials);
        for(String dateIssue : jiraIssue.getWorkLogs().keySet()) {
            String time = jiraIssue.getWorkLogs().get(dateIssue);
            System.out.println("log to "+jiraIssue.getId()+" date = " +dateIssue +" time to log="+ time);
            makePost(headersMap, "https://jira.ringcentral.com/rest/api/latest/issue/" + jiraIssue.getId() + "/worklog", makeJSON(dateIssue+"T15:01:00.000+0000", time+"s"));
        }
    }



    public static HashMap<String, String> getLogWork(String credentials, int days) throws IOException {
        HashMap<String, String> hashMap = new HashMap<>();
        String json = getJson(credentials,days);

        Worklog[] w = new ObjectMapper().readValue(json, Result.class).getWorklog();
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

    public static HashMap<String, String> getLogWork(String credentials, LocalDate startDate, LocalDate endDate) throws IOException {
        HashMap<String, String> hashMap = new HashMap<>();
        int days = (int) ChronoUnit.DAYS.between(startDate, LocalDate.now());
        String json = getJson(credentials, days);

        Worklog[] w = new ObjectMapper().readValue(json, Result.class).getWorklog();
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

    private static String getJson(String credentials,int days) throws IOException {
        putCredentials(credentials);
        String json = post(headersMap, "https://jira.ringcentral.com/rest/timesheet-gadget/1.0/raw-timesheet.json?targetUser=" + getUserName(credentials) + "&startDate=" + getDate(days));
        return json;
    }


    public static int basicAuthorization(UserPreferences userPreferences) throws IOException {
        putCredentials(userPreferences.getCredentials());
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("http://jira.ringcentral.com");
            putHeadersInRequest(request, headersMap);
            try (CloseableHttpResponse response = client.execute(request)) {
                return response.getStatusLine().getStatusCode();
            }
        }
        catch (Exception ex) {
            Logger.getAnonymousLogger().log(Level.INFO, ex.getMessage());
        }
        return 0;
    }


    private static String post(HashMap<String, String> headers, String url) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            putHeadersInRequest(request, headers);
            try (CloseableHttpResponse response = client.execute(request)) {
                try {
                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 65728);
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                } catch (IOException e) {
                    Logger.getAnonymousLogger().log(Level.INFO, e.getMessage());
                } catch (Exception e) {
                    Logger.getAnonymousLogger().log(Level.INFO, e.getMessage());
                }
            }
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








}

