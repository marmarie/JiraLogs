package sample.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import structure.model.UserPreferences;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.logging.Level.WARNING;

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

    private static boolean isSignatureExist(){return getSignature().exists();}
    private static File getSignature() {return new File("signature.txt");}

    public static String getEmailSignature() {
        if(!isSignatureExist())
            saveEmailSignature();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(getSignature(), String.class);
        } catch (IOException e) {
            Logger.getAnonymousLogger().log(WARNING, e.getMessage());
        }
        return null;
    }

    public static void saveEmailSignature()  {
        File signatureFile = getSignature();
        try {
            if(signatureFile.createNewFile()) {
                Logger.getAnonymousLogger().log(Level.FINE, "New Signature File was created!");
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.writeValue(signatureFile, TestHttp.getEmailSignature());
            }
            else Logger.getAnonymousLogger().log(Level.FINE, "Signature File already exists!");
        } catch (IOException e) {
            Logger.getAnonymousLogger().log(WARNING, e.getMessage());
        }
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

//    public static void saveCredentials(String name, String pass) {
//        File file = getFile();
//        try {
//            if(file.createNewFile()) {
//                System.out.println("New file was created");
//                FileWriter fileWriter = new FileWriter(file);
//                fileWriter.write(name + "\n" + pass);
//                fileWriter.flush();
//            }
//            else
//                System.out.println("File already exists");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public static void saveUserPreferences(UserPreferences userPreferences){
        File file = getFile();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if(file.createNewFile()) {
                System.out.println("New file was created");
                objectMapper.writeValue(file, userPreferences);
            }
            else {
            if(!getCredentialsFromFile().getCredentials().equals(userPreferences.getCredentials()))
                objectMapper.writeValue(file, userPreferences);
            }

        } catch (IOException e) {
            Logger.getAnonymousLogger().log(WARNING, e.getMessage());
        }
    }





    public static UserPreferences getCredentialsFromFile() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(getFile(), UserPreferences.class);
        } catch (IOException e) {
            Logger.getAnonymousLogger().log(WARNING, e.getMessage());
        }
        return null;
    }

}
