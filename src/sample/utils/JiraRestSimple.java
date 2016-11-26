package sample.utils;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.input.WorklogInputBuilder;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import org.joda.time.DateTime;

import java.net.URI;
import java.util.ArrayList;

/**
 * Created by ali.naffaa on 23.11.2016.
 */
public class JiraRestSimple {
    private static final String JIRA_URL = "https://jira.ringcentral.com";
    private static final String JIRA_ADMIN_USERNAME = "";
    private static final String JIRA_ADMIN_PASSWORD = "";

    public static void main(String[] args) throws Exception {
        System.out.println(String.format("Logging in to %s with username '%s' and password '%s'", JIRA_URL, JIRA_ADMIN_USERNAME, JIRA_ADMIN_PASSWORD));
        JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
        final URI jiraServerUri = new URI(JIRA_URL);
        JiraRestClient client = factory.createWithBasicHttpAuthentication(jiraServerUri, JIRA_ADMIN_USERNAME, JIRA_ADMIN_PASSWORD);

        ArrayList<Issue> result = (ArrayList<Issue>) client.getSearchClient().searchJql("project in (CDR, AUT) AND created >= -4w AND \"Participants of an issue\" = currentUser() and worklogAuthor=currentUser()").claim().getIssues();
        for(Issue issue:result){
            client.getIssueClient().addWorklog(issue.getWorklogUri(),new WorklogInputBuilder(issue.getSelf()).setStartDate(new DateTime()).setComment("updated").setMinutesSpent(1).build()).claim();
        }
    }
}


