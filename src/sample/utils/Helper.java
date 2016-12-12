package sample.utils;

import org.apache.commons.codec.binary.Base64;
import structure.JiraIssue;

import java.text.SimpleDateFormat;
import java.util.*;

import static sample.utils.JiraBasicRest.DATE;

/**
 * Created by marie on 05.12.16.
 */
public class Helper {


    static ArrayList<String> daysOff = new ArrayList<>();

    static  {
        daysOff.add("Sunday");
        daysOff.add("Saturday");


    }

    static SimpleDateFormat format = new SimpleDateFormat(DATE);


    public static String getUserName(String cred) {
        byte[] decodedBytes = Base64.decodeBase64(cred);
        return new String(decodedBytes).split(":")[0];
    }

    public static String getPassword(String cred) {
        byte[] decodedBytes = Base64.decodeBase64(cred);
        return new String(decodedBytes).split(":")[1];
    }

    public static String encodeCredentials(String cred){
        return Base64.encodeBase64String(cred.getBytes());
    }

    public static JiraIssue getIssue(String id, String time ){
        LinkedHashMap<String, String> workLogs = new LinkedHashMap<>();
        workLogs.put(getDate(String.valueOf(new Date().getTime())), getTimeInSeconds(time));
        return new JiraIssue(id, workLogs);
    }

    private static String getTimeInSeconds(String time){
        if(time.contains("d"))
            return Integer.parseInt(time.replace("d", "")) * 8*3600 + "";
        if (time.contains("h"))
            return Integer.parseInt(time.replace("h", "")) * 3600 + "";
        if(time.contains("m"))
            return Integer.parseInt(time.replace("m", "")) * 60 + "";
        return time;

    }


    public static String getDate(String date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(date));
        return format.format(calendar.getTime());
    }


    public static String getDate(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -days);
        return format.format(calendar.getTime());
    }

    public static HashMap<String, String> getCalendarDays(int days) {
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


}
