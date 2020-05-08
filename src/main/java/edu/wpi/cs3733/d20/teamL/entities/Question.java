package edu.wpi.cs3733.d20.teamL.entities;

public class Question {
    String question;
    int order;
    int weight;
    int recs;

    public Question(String question, int order, int weight, int recs) {
        this.question = question;
        this.order = order;
        this.weight = weight;
        this.recs = recs;
    }

    public String getQuestion() {
        return question;
    }

    public int getOrder() {
        return order;
    }

    public int getWeight() {
        return weight;
    }

    public int getRecs() {
        return recs;
    }
}
