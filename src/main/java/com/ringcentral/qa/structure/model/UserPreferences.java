package com.ringcentral.qa.structure.model;

/**
 * Created by ali.naffaa on 30.11.2016.
 */
public class UserPreferences {
    private String userName;
    private String credentials; //base 64
    private String email;
    private String emailSignature;

    public String getEmailSignature() {return emailSignature;}

    public void setEmailSignature(String emailSignature) {this.emailSignature = emailSignature;}

    public String getUserName() {return userName;}

    public String getEmail(){return email; }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEmail(String email) { this.email = email;}

    public String getCredentials() {return credentials;}

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }

}
