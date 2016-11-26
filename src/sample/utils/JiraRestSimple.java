package sample.utils;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.input.WorklogInputBuilder;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import org.joda.time.DateTime;
import structure.JiraIssue;

import java.io.FileNotFoundException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by ali.naffaa on 23.11.2016.
 */
public class JiraRestSimple {
    private static final String JIRA_URL = "";
    private static final String JIRA_ADMIN_USERNAME = "";
    private static final String JIRA_ADMIN_PASSWORD = "";

    private static HashMap<String,ArrayList<JiraIssue>> dateAndTaskId = new HashMap<>();

    public static void main(String[] args) throws Exception {
        JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
        final URI jiraServerUri = new URI(JIRA_URL);


        JiraRestClient client  = factory.createWithBasicHttpAuthentication(jiraServerUri, JIRA_ADMIN_USERNAME, JIRA_ADMIN_PASSWORD);
        fillMaps(client, 4);
//        ArrayList<Issue> result = (ArrayList<Issue>) client.getSearchClient().searchJql("project in (CDR, AUT) AND created >= -4w AND \"Participants of an issue\" = currentUser() and worklogAuthor=currentUser()").claim().getIssues();
//        for(Issue issue:result){
//            client.getIssueClient().addWorklog(issue.getWorklogUri(),new WorklogInputBuilder(issue.getSelf()).setStartDate(new DateTime()).setComment("updated").setMinutesSpent(1).build()).claim();
//        }
//        System.exit(0);
    }

    private static void fillMaps(JiraRestClient client,int weeks) throws FileNotFoundException {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.add(Calendar.WEEK_OF_YEAR,-weeks);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(calendar.getTime());
        ArrayList<Issue> result = (ArrayList<Issue>) client.getSearchClient().searchJql("project in (CDR, AUT) and worklogDate > "+ date +" and worklogAuthor = "+JIRA_ADMIN_USERNAME).claim().getIssues();
        for(Issue is : result){
            is.getId();
            client.getIssueClient().getIssue("AUT-10223").claim().getWorklogs();
        }


    }

}


