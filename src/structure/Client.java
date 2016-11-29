package structure;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by mariya.azoyan on 26.11.2016.
 */
public class Client {

    public static JSONObject makeJSON(String date, String time) throws JSONException {
        JSONObject f = new JSONObject();
        f.put("started", date);
        f.put("timeSpent", time);
        return f;
    }

    private static boolean getPost(HashMap<String, String> headers, String url, JSONObject json) throws IOException, JSONException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(url);
            for (String name : headers.keySet()) {
                request.addHeader(name, headers.get(name));
            }
            request.setEntity(new StringEntity(json.toString()));
            try (CloseableHttpResponse response = client.execute(request)) {
                try {
                    if (response.getStatusLine().getStatusCode() == 201)
                        return true;
                } catch (Exception e) {
                    System.out.println(response.getStatusLine());
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public static boolean logWork(String issueToLog, String date, String time) throws JSONException, IOException {

        String credentials = "";
        HashMap<String,String> headersMap = new HashMap<>();
        headersMap.put("Content-Type","application/json");
        headersMap.put("Authorization", "Basic " + credentials);
        return getPost(headersMap,"https://jira.ringcentral.com/rest/api/latest/issue/"+issueToLog+"/worklog", makeJSON(date, time));

    }

    public static void main(String... args) throws IOException, JSONException {
        System.out.println(logWork("AUT-10223","2016-11-29T10:46:37.884+0000", "30 m" ));
    }

}
