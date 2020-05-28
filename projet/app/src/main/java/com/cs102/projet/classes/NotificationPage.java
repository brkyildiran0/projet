package com.cs102.projet.classes;

public class NotificationPage
{
    //Properties
    private String title;
    private String message;
    private String time;

    //Constructors
    public NotificationPage() { }

    public NotificationPage(String title, String message, String time)
    {
        this.title = title;
        this.message = message;
        this.time = time;
    }

    //Methods
    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getTime(){
        return time;
    }
}
