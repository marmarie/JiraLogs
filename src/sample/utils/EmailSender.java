package sample.utils;

import sample.LoginPage3;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by marie on 08.03.17.
 */
public class EmailSender {

    private String username ;
    private String password = "";
    private String email;
    private String signature;

    public EmailSender(String username, String passWord){
        this.username = username + "@od.anything3d.com";
        this.password = passWord;
    }

    public EmailSender(){
        this.username = LoginPage3.getUserPreferences().getUserName() + "@od.anything3d.com";
        this.email = LoginPage3.getUserPreferences().getEmail();
        try {
            this.signature = FileReader.getEmailSignature();
        } catch (IOException e) {
            Logger.getAnonymousLogger().log(Level.WARNING, "Problems with email signature");
        }
    }

    private Session getSession(){
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "secure.emailsrvr.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        return session;
    }

    public boolean sendEMmail(String subject, String summary) {
        String bug = "<a href=\"http://jira.ringcentral.com/browse/" + subject + "\">" + summary + "</a>";
        try {
            Message message = new MimeMessage(getSession());
            message.setFrom(new InternetAddress(email));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("TO"));
            message.setSubject("New bug " + subject + ": " + summary);
            message.setContent("Hello Team!<br>" + "\t\t" + bug +"<br>" + signature , "text/html");

            Transport.send(message);
            return true;
        } catch (MessagingException e) {
            return false;
        }
    }
}
