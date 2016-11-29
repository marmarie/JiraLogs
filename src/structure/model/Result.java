package structure.model;

public class Result
{
    private String startDate;

    private String endDate;

    private Worklog[] worklog;

    public String getStartDate ()
    {
        return startDate;
    }

    public void setStartDate (String startDate)
    {
        this.startDate = startDate;
    }

    public String getEndDate ()
    {
        return endDate;
    }

    public void setEndDate (String endDate)
    {
        this.endDate = endDate;
    }

    public Worklog[] getWorklog ()
    {
        return worklog;
    }

    public void setWorklog (Worklog[] worklog)
    {
        this.worklog = worklog;
    }

    @Override
    public String toString() {
        return "ClassPojo [startDate = "+startDate+", endDate = "+endDate+", worklog = "+worklog+"]";
    }
}