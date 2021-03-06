package com.ringcentral.qa.utils;

import com.ringcentral.qa.structure.model.JiraIssue;
import org.apache.commons.codec.binary.Base64;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.ringcentral.qa.utils.JiraBasicRest.DATE;


/**
 * Created by marie on 05.12.16.
 */
public class Helper {

    static ArrayList<String> daysOff = new ArrayList<>();

    static  {
        daysOff.add("sunday");
        daysOff.add("saturday");
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
        workLogs.put(LocalDateTime.now().toString(), getTimeInSeconds(time));
        return new JiraIssue(id, workLogs);
    }

    public static String getTimeInSeconds(String time){
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

    public static HashMap<String, String> getCalendarDays(LocalDate startDate, LocalDate localEndDate) {
        LinkedHashMap<String, String> data = new LinkedHashMap<>();
        long days = ChronoUnit.DAYS.between(startDate, localEndDate);
        for (int counter = 0; counter <= days; counter++) {
            LocalDate temp =  localEndDate.minusDays(counter);
            if (!isDateOFF(temp))
                data.put(temp.toString(), "28800");
        }
        return data;
    }

    private static boolean isDateOFF(LocalDate localEndDate) {
        return daysOff.contains(localEndDate.getDayOfWeek().toString().toLowerCase());
    }

    public static boolean isDateOFF(Calendar calendar) {
        SimpleDateFormat format = new SimpleDateFormat("EEEE", Locale.ENGLISH);
        String day = format.format(calendar.getTime());
        return daysOff.contains(day.toLowerCase());
    }

    public static boolean isCorrectTaskId(String allText){
        return allText.matches("^[a-zA-Z]{3}-\\d{1,}");
    }

    public static boolean isCorrectInputForTaskId1(String value, String t){
        String allText = value+t;
        if(allText.matches("^[a-zA-Z]{1,3}"))
            return true;
        if(value.matches("^[a-zA-Z]{3}")&&(t.equals("-")||t.isEmpty()))
            return true;
        if(value.matches("^[a-zA-Z]{3}-|^[a-zA-Z]{3}-\\d{1,}")&&(t.matches("^\\d{1,}")||t.isEmpty()))
            return true;
        return isCorrectTaskId(t);
    }
    public static boolean isCorrectInputForTaskId(String oldValue, String newValue){
        if(newValue.isEmpty())
            return true;
        String t = String.valueOf(newValue.charAt(newValue.length()-1));
        if(newValue.matches("^[a-zA-Z]{1,3}"))
            return true;
        if(oldValue.matches("^[a-zA-Z]{3}")&&(t.equals("-")||t.isEmpty()))
            return true;
        if(oldValue.matches("^[a-zA-Z]{3}-|^[a-zA-Z]{3}-\\d{1,}")&&(t.matches("^\\d{1,}")||t.isEmpty()))
            return true;
        return isCorrectTaskId(t);
    }


}
