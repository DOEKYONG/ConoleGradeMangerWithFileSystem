package utils;

import app.Grade;
import app.Subject;
import java.util.ArrayList;
import java.util.List;


public class Utils {
    public static List<Subject> createSubjectList(String input) {
        validateInput(input);
        List<Subject> subjects = new ArrayList<>();
        if(!input.contains(", ")) {
            subjects.add(new Subject(input.split(":")[0],Double.parseDouble(input.split(":")[1])));
            return subjects;
        }
        String[] subjectInput = input.split(", ");
        for(String temp : subjectInput) {
            subjects.add(new Subject(temp.split(":")[0],Double.parseDouble(temp.split(":")[1])));
        }
        return subjects;
    }

    private static void validateInput(String input) {
        if (!input.matches(".+:\\s*\\d+$")) {
            throw new RuntimeException("잘못된 입력 형식입니다.");
        }
        if (input.contains(",") && !input.matches(".*,\\s+.*")) {
            throw new RuntimeException("쉼표 앞에 공백이 한 개를 초과할 수 없습니다.");
        }
    }

    public static double getCalculateAverage(List<Subject> subjects) {
        double sum =0;
        for(Subject subject : subjects){
            sum += subject.getScore();
        }
        return sum/subjects.size();
    }

    public static void appendData(Grade temp, StringBuilder sb) {
        for(Subject subject : temp.getSubjects()){
            sb.append("/").
                    append(subject.getSubjectName())
                    .append(" ")
                    .append(subject.getScore());
        }
    }

    public static void validateNumber(Boolean check) {
        if(!check) {
            throw new RuntimeException("존재하지 않는 학번입니다.");
        }
    }
}