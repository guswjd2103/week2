package com.example.week1_contact;

public class Problem {
    private int problem_id;
    private String answer;

    public Problem(int problem_id, String answer) {
        this.problem_id = problem_id;
        this.answer = answer;
    }

    public int getProblem_id() {
        return problem_id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setProblem_id(int problem_id) {
        this.problem_id = problem_id;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
