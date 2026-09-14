package com.emine.kelimeoyunu;

public class Question {
    private int id;
    private String question;
    private String answer;

    // Constructor (Yapıcı Metot)
    public Question(int id, String question, String answer) {
        this.id = id;
        this.question = question;
        this.answer = answer;
    }

    // Getter Metotları (Verileri okumak için)
    public int getId() { return id; }
    public String getQuestion() { return question; }
    public String getAnswer() { return answer; }
}