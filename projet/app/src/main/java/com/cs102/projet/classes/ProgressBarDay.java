package com.cs102.projet.classes;

public class ProgressBarDay {

    private String projet_due_date;
    private String projet_created_date;

    public ProgressBarDay() {
    }

    public ProgressBarDay(String projet_due_date, String projet_creation_date)
    {
        this.projet_due_date = projet_due_date;
        this.projet_created_date = projet_created_date;
    }

    public String getProjet_due_date()
    {
        return projet_due_date;
    }

    public String getProjet_created_date()
    {
        return projet_created_date;
    }
}
