package com.student.bikeshare.models;

public class MailModel {
    private String to;
    private String from;
    private String subject;
    private String body;

    public MailModel(String to, String from, String subject, String body) {
        this.to = to;
        this.from = from;
        this.subject = subject;
        this.body = body;
    }
}
