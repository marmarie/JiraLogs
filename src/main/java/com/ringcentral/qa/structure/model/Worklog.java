package com.ringcentral.qa.structure.model;

public class Worklog {

    private String summary;
    private Entries[] entries;
    private String key;
    private String[] fields;


    public String getSummary ()
    {
        return summary;
    }

    public void setSummary (String summary)
    {
        this.summary = summary;
    }

    public Entries[] getEntries ()
    {
        return entries;
    }

    public void setEntries (Entries[] entries)
    {
        this.entries = entries;
    }

    public String getKey ()
    {
        return key;
    }

    public void setKey (String key)
    {
        this.key = key;
    }

    public String[] getFields ()
    {
        return fields;
    }

    public void setFields (String[] fields)
    {
        this.fields = fields;
    }

    @Override
    public String toString() {
        return " [summary = "+summary+", entries = "+entries+", key = "+key+", fields = "+fields+"]";
    }
}