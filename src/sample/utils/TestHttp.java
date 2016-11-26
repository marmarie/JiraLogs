package sample.utils;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import structure.model.Result;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Created by ali.naffaa on 18.08.2016.
 */
public class TestHttp {

    public static Result getPhones(HashMap<String,String> headers) throws IOException {
//        ArrayList <Result> resultList = new ArrayList<>();
//        ObjectMapper mapper = new ObjectMapper();
//        String url = "http://service-odsautams.lab.nordigy.ru/mobile/api/proxy.html?cmd=digitalLine.getPhones";
//        Result [] result = mapper.readValue(getPost(headers,"type=UserPhones",url), Entries.class).getResult();
//        Collections.addAll(resultList,result);
//        return resultList;
        return new Result();
    }


    public static void LoginToJira(String credentials) throws IOException {
        HashMap<String,String> headersMap = new HashMap<>();
        headersMap.put("Content-Type","application/json");
        headersMap.put("Authorization","Basic "+credentials);
        getPost(headersMap,"");
    }


    private static String getPost(HashMap<String,String> headers,String url) throws IOException {
        StringBuilder sb = new StringBuilder();
        try(CloseableHttpClient client = HttpClients.createDefault()){

            HttpPost request = new HttpPost(url);
            request.setHeader("","" );


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


}

