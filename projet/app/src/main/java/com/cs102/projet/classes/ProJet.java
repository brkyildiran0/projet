package com.cs102.projet.classes;

public class ProJet
{
    //Properties
    private String projet_name;
    private String projet_due_date;

    //Constructors
    public ProJet() { }

    public ProJet(String projet_name, String projet_due_date)
    {
        this.projet_name = projet_name;
        this.projet_due_date = projet_due_date;
    }

    //Methods
    public String getProjet_name() {
        return projet_name;
    }

    public String getProjet_due_date() {
        return projet_due_date;
    }
}
