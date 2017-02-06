package sample.test;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

/**
 * Created by marie on 27.01.17.
 */
public class SendEmail {

    String jqlEmail = "https://jira.ringcentral.com/rest/api/2/search?jql=issuetype%20%3D%20Bug%20and%20reporter%3D"+ "USERNAME"+"%20and%20created%20%3E%20startOfDay(-0d)%20&fields=key,summary";
    String request = "rest/api/2/search?jql=issuetype%20%3D%20Bug%20and%20created%20>%20startOfDay(-0d)%20&fields=key,summary";
    String bugURL, bugName;
    String email = "<a href=" + bugURL + ">"+ bugName +"</a>";


    public static void main(String[] args) {

        final String username = " ";
        final String password = " ";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.me.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(" "));
            message.setSubject("New Bug");
            message.setContent("<a href=\"https://www.tutorialspoint.com/java/java_sending_email.htm\">Текст ссылки</a>", "text/html");
//            message.setText("Dear Mail Crawler,"
//                    + "\n\n No spam to my email, please!");
            Transport.send(message);
            System.out.println("Done");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }


}
