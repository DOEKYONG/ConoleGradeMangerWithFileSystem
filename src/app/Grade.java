package app;

import java.util.List;


public class Grade {
    private String name;
    private int number;
    private String major;
    private List<Subject> subjects;

    public Grade(String name, int number, String major, List<Subject> subjects) {
        this.name = name;
        this.number = number;
        this.major = major;
        this.subjects = subjects;
    }



    public Grade(String name, String major, List<Subject> subjects) {
        this.name = name;
        this.major = major;
        this.subjects = subjects;
    }

    public String getName() {
        return name;
    }

    public int getNumber() {
        return number;
    }

    public String getMajor() {
        return major;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

}
