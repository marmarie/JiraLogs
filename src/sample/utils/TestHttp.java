package sample.utils;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import structure.JiraIssue;
import structure.model.Entries;
import structure.model.Result;
import structure.model.Worklog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static sample.utils.JiraBasicRest.DATE;
import static sample.utils.JiraBasicRest.JIRA_DATE_TIME_PATTERN;

/**
 * Created by ali.naffaa and mariya.azoyan 18.08.2016.
 */
public class TestHttp {

    public static ArrayList<JiraIssue>  getLogWorkWithCredAndDays(String credentials,int days) throws IOException {
        ArrayList<JiraIssue> jiraIssueArrayList = new ArrayList<>();
        HashMap<String,String> headersMap = new HashMap<>();
        headersMap.put("Content-Type","application/json");
        headersMap.put("Authorization", "Basic " + credentials);
        String json = getPost(headersMap,"https://jira.ringcentral.com/rest/timesheet-gadget/1.0/raw-timesheet.json?targetUser="+getUserName(credentials)+"&startDate="+getDate(days));

        ObjectMapper mapper = new ObjectMapper();
        Worklog[] w =  mapper.readValue(json, Result.class ).getWorklog();
        for(Worklog worklog:w){
            Entries [] entries = worklog.getEntries();
            LinkedHashMap<String,String> date= new LinkedHashMap<>();
            for(Entries entry: entries){
                if(date.containsKey(entry.getStartDate())){
                    String fullDate = String.valueOf(Long.valueOf(date.get(entry.getStartDate()))+Long.valueOf(entry.getTimeSpent()));
                    date.put(entry.getStartDate(), fullDate);
                }
                else {
                    date.put(entry.getStartDate(), entry.getTimeSpent());
                }
            }
            jiraIssueArrayList.add(new JiraIssue(worklog.getKey(),date));
        }
        return jiraIssueArrayList;
    }


    public static void logWork(JiraIssue issueToLog,String credentials) throws IOException {
        ArrayList<JiraIssue> jiraIssueArrayList = new ArrayList<>();
        HashMap<String,String> headersMap = new HashMap<>();
        headersMap.put("Content-Type","application/json");
        headersMap.put("Authorization", "Basic " + credentials);
        String json = getPost(headersMap,"https://jira.ringcentral.com/rest/api/2/issue/"+issueToLog+"/worklog");

        ObjectMapper mapper = new ObjectMapper();
        Worklog[] w =  mapper.readValue(json, Result.class ).getWorklog();
        for(Worklog worklog:w){
            Entries [] entries = worklog.getEntries();
            LinkedHashMap<String,String> date= new LinkedHashMap<>();
            for(Entries entry: entries){
                if(date.containsKey(entry.getStartDate())){
                    String fullDate = String.valueOf(Long.valueOf(date.get(entry.getStartDate()))+Long.valueOf(entry.getTimeSpent()));
                    date.put(entry.getStartDate(), fullDate);
                }
                else {
                    date.put(entry.getStartDate(), entry.getTimeSpent());
                }
            }
            jiraIssueArrayList.add(new JiraIssue(worklog.getKey(),date));
        }
    }


    public static HashMap<String,String>  getLogWork(String credentials,int days) throws IOException {
        HashMap<String,String> hashMap = new HashMap<>();
        HashMap<String,String> headersMap = new HashMap<>();
        headersMap.put("Content-Type","application/json");
        headersMap.put("Authorization", "Basic " + credentials);
        String json = getPost(headersMap,"https://jira.ringcentral.com/rest/timesheet-gadget/1.0/raw-timesheet.json?targetUser="+getUserName(credentials)+"&startDate="+getDate(days));

        ObjectMapper mapper = new ObjectMapper();
        Worklog[] w =  mapper.readValue(json, Result.class ).getWorklog();
        for(Worklog worklog:w){
            Entries [] entries = worklog.getEntries();
            for(Entries entry: entries){
                String date = getDate(entry.getStartDate());
                if(hashMap.containsKey(date)){
                    String fullDate = String.valueOf(Long.valueOf(hashMap.get(date))+Long.valueOf(entry.getTimeSpent()));
                    hashMap.put(date, fullDate);
                }
                else {
                    hashMap.put(date, entry.getTimeSpent());
                }
            }
        }
        return hashMap;
    }




    private static String getPost(HashMap<String,String> headers,String url) throws IOException {
        StringBuilder sb = new StringBuilder();
        try(CloseableHttpClient client = HttpClients.createDefault()){
            HttpGet request = new HttpGet(url);
            for(String name:headers.keySet()){
                request.setHeader(name,headers.get(name));
            }
            try(CloseableHttpResponse response = client.execute(request)){
                try {
                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 65728);
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                }
                catch (IOException e) { e.printStackTrace(); }
                catch (Exception e) { e.printStackTrace(); }
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
            } else {
                needLogJira.addWorkLog(id, data.get(id));
            }
        }
        return needLogJira;
    }

    private static HashMap<String, String> getCalendarDays(int days) {
        HashMap<String, String> data = new HashMap<>();
        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(DATE);
        for (int counter = 0; counter < days; counter++) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR,-counter);
            data.put(dateTimeFormatter.format(calendar.getTime()),"28800");
        }
        return data;
    }

    private static String getDate(String date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(date));
        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(DATE);
        return dateTimeFormatter.format(calendar.getTime());
    }


    private static String getDate(int days){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR,-days);
        SimpleDateFormat format = new SimpleDateFormat(DATE);
        return format.format(calendar.getTime());
    }

    private static String getUserName(String cred){
        byte[] decodedBytes = Base64.decodeBase64(cred);
        return new String(decodedBytes).split(":")[0];
    }

}

