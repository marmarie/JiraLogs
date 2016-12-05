package sample.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import structure.model.UserPreferences;

import java.io.*;

/**
 * Created by marie on 23.11.16.
 */
public class FileReader {


    public static boolean isFileExists(){
        return getFile().exists();
    }

    private static File getFile(){
        return new File("login.txt");
    }


    public static String[] getCredentials() throws IOException {
        java.io.FileReader fileReader = new java.io.FileReader(getFile());
        BufferedReader br = new BufferedReader(fileReader);
        String line;
        String[] ar = new String[2];
        int i = 0;
        while ((line = br.readLine()) != null) {
            ar[i++] = line;
        }
        br.close();
        fileReader.close();
        return ar;
    }

    public static void saveCredentials(String name, String pass) throws IOException{
        File file = getFile();
        if(file.createNewFile()) {
            System.out.println("New file was created");
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(name + "\n" + pass);
            fileWriter.flush();
        }
        else
            System.out.println("File already exists");
    }

    public static void saveUserPreferences(UserPreferences userPreferences) throws IOException{
        File file = getFile();
        if(file.createNewFile()) {
            System.out.println("New file was created");
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(file, userPreferences);
        }
        else
            System.out.println("File already exists");
    }

    public static UserPreferences getCredentialsFromFile() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(getFile(), UserPreferences.class);
    }

}
