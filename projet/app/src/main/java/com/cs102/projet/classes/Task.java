package com.cs102.projet.classes;

public class Task
{
    //Properties
    private String task_description;
    private String task_due_date;
    private String task_name;
    private String task_owner;
    private String task_due_hour;
    private String task_priority;

    //Constructors
    public Task() { }

    public Task(String task_description, String task_due_date, String task_name, String task_owner, String task_due_hour, String task_priority)
    {
        this.task_description = task_description;
        this.task_due_date = task_due_date;
        this.task_name = task_name;
        this.task_owner = task_owner;
        this.task_due_hour = task_due_hour;
        this.task_priority = task_priority;
    }

    //Methods
    public String getTask_description() {
        return task_description;
    }

    public String getTask_due_date() {
        return task_due_date;
    }

    public String getTask_name() {
        return task_name;
    }

    public String getTask_owner() {
        return task_owner;
    }

    public String getTask_due_hour() {
        return task_due_hour;
    }

    public String getTask_priority() {
        return task_priority;
    }
}