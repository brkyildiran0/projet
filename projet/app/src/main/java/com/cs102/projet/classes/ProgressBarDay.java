package com.cs102.projet.classes;

public class ProgressBarDay
{
    //Properties
    private String projet_due_date;
    private String projet_created_date;

    //Constructors
    public ProgressBarDay() { }

    public ProgressBarDay(String projet_due_date, String projet_creation_date)
    {
        this.projet_due_date = projet_due_date;
        this.projet_created_date = projet_created_date;
    }

    //Methods
    public String getProjet_due_date()
    {
        return projet_due_date;
    }

    public String getProjet_created_date()
    {
        return projet_created_date;
    }
}
