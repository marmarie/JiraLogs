package structure.model;

/**
 * Created by ali.naffaa on 30.11.2016.
 */
public class UserPreferences {
    String userName;
    String credentials; //base 64
    String email;

    public String getUserName() {
        return userName;
    }

    public String getEmail(){return email; }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEmail(String email) { this.email = email;}

    public String getCredentials() {
        return credentials;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }




}
