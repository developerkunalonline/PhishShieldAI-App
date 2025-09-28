package com.backdoorz.phishshieldai;

public class VisitedUrl {
    private String url;
    private String status;
    private String date;

    public VisitedUrl(String url, String status, String date) {
        this.url = url;
        this.status = status;
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public String getStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }
}
