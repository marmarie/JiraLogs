package com.ringcentral.qa.structure.model;

public class Entries
{
    private String id;
    private String startDate;
    private String author;
    private String updated;
    private String created;
    private String updateAuthorFullName;
    private String updateAuthor;
    private String timeSpent;
    private String comment;
    private String authorFullName;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getStartDate ()
    {
        return startDate;
    }

    public void setStartDate (String startDate)
    {
        this.startDate = startDate;
    }

    public String getAuthor ()
    {
        return author;
    }

    public void setAuthor (String author)
    {
        this.author = author;
    }

    public String getUpdated ()
    {
        return updated;
    }

    public void setUpdated (String updated)
    {
        this.updated = updated;
    }

    public String getCreated ()
    {
        return created;
    }

    public void setCreated (String created)
    {
        this.created = created;
    }

    public String getUpdateAuthorFullName ()
    {
        return updateAuthorFullName;
    }

    public void setUpdateAuthorFullName (String updateAuthorFullName) {
        this.updateAuthorFullName = updateAuthorFullName;
    }

    public String getUpdateAuthor ()
    {
        return updateAuthor;
    }

    public void setUpdateAuthor (String updateAuthor)
    {
        this.updateAuthor = updateAuthor;
    }

    public String getTimeSpent ()
    {
        return timeSpent;
    }

    public void setTimeSpent (String timeSpent)
    {
        this.timeSpent = timeSpent;
    }

    public String getComment ()
    {
        return comment;
    }

    public void setComment (String comment)
    {
        this.comment = comment;
    }

    public String getAuthorFullName ()
    {
        return authorFullName;
    }

    public void setAuthorFullName (String authorFullName)
    {
        this.authorFullName = authorFullName;
    }

    @Override
    public String toString() {
        return "ClassPojo [id = "+id+", startDate = "+startDate+", author = "+author+", updated = "+updated+", created = "+created+", updateAuthorFullName = "+updateAuthorFullName+", updateAuthor = "+updateAuthor+", timeSpent = "+timeSpent+", comment = "+comment+", authorFullName = "+authorFullName+"]";
    }
}