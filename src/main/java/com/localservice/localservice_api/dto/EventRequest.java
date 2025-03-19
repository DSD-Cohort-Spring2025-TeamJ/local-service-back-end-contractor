package com.localservice.localservice_api.dto;

import com.google.api.client.util.DateTime;

public class EventRequest {
    private String summary;
    private String start;
    private String end;

    // Convert string start time to EventDateTime format
    public DateTime getParsedStartTime() {
        return new DateTime(start);
    }

    // Convert string end time to EventDateTime format
    public DateTime getParsedEndTime() {
        return new DateTime(end);
    }

    // Getters and Setters
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getStart() { return start; }
    public void setStart(String start) { this.start = start; }

    public String getEnd() { return end; }
    public void setEnd(String end) { this.end = end; }
}
