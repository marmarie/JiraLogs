package sample.utils;

import org.apache.commons.codec.binary.Base64;

/**
 * Created by marie on 05.12.16.
 */
public class Helper {


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


}
