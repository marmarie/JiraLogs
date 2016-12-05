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
import structure.model.Worklog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;

import static sample.utils.Helper.getUserName;
import static sample.utils.JiraBasicRest.DATE;

/**
 * Created by ali.naffaa and mariya.azoyan 18.08.2016.
 */

public class TestHttp {

    static ArrayList<String> daysOff = new ArrayList<>();
    static HashMap<String, String> headersMap = new HashMap<>();
    static SimpleDateFormat format = new SimpleDateFormat(DATE);

    static  {
        daysOff.add("Sunday");
        daysOff.add("Saturday");
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        return false;
    }

    public static void logWork(String credentials, JiraIssue jiraIssue) throws JSONException, IOException {
        putCredentials(credentials);
        for(String dateIssue : jiraIssue.getWorkLogs().keySet()) {
            String time = jiraIssue.getWorkLogs().get(dateIssue);
            System.out.println("log to "+jiraIssue.getId()+" date = " +dateIssue +" time to log="+time);
            makePost(headersMap, "https://jira.ringcentral.com/rest/api/latest/issue/" + jiraIssue.getId() + "/worklog", makeJSON(dateIssue+"T15:01:00.000+0000", time+"s"));
        }
    }



    public static HashMap<String, String> getLogWork(String credentials, int days) throws IOException {
        HashMap<String, String> hashMap = new HashMap<>();
        putCredentials(credentials);
        String json = post(headersMap, "https://jira.ringcentral.com/rest/timesheet-gadget/1.0/raw-timesheet.json?targetUser=" + getUserName(credentials) + "&startDate=" + getDate(days));

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
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
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

    private static HashMap<String, String> getCalendarDays(int days) {
        LinkedHashMap<String, String> data = new LinkedHashMap<>();
        for (int counter = 0; counter < days; counter++) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -counter);
            if (!isDateOFF(calendar))
                data.put(format.format(calendar.getTime()), "28800");
        }
        return data;
    }

    private static boolean isDateOFF(Calendar calendar) {
        SimpleDateFormat format = new SimpleDateFormat("EEEE", Locale.ENGLISH);
        String day = format.format(calendar.getTime());
        return daysOff.contains(day);
    }

    private static String getDate(String date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(date));
        return format.format(calendar.getTime());
    }


    private static String getDate(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -days);
        return format.format(calendar.getTime());
    }




}

