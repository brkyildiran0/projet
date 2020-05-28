package com.cs102.projet.classes;

public class ProgressBarTask
{
    //Properties
    private int total_completed_tasks;
    private int total_uncompleted_tasks;

    //Constructors
    public ProgressBarTask (){}

    public ProgressBarTask(int total_completed_tasks, int total_uncompleted_tasks)
    {
        this.total_completed_tasks = total_completed_tasks;
        this.total_uncompleted_tasks = total_uncompleted_tasks;
    }

    //Methods
    public int getTotal_completed_tasks() {
        return total_completed_tasks;
    }

    public int getTotal_uncompleted_tasks() {
        return total_uncompleted_tasks;
    }
}