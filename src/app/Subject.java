package app;

import java.util.List;
import utils.Utils;

public class Subject {
    private String subjectName;
    private double score;

    public Subject(String subjectName, double score) {
        this.subjectName = subjectName;
        this.score = score;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public double getScore() {
        return score;
    }

    static Grade subjectToGrade(List<Grade> grades, Subject updatedSubject, int inNum) {
        Boolean check = false;
        for(Grade temp : grades) {
            int index = 0;
            for (Subject subject : temp.getSubjects()) {
                if(temp.getNumber() == inNum) {
                    check = true;
                    if (!subject.getSubjectName().equals(updatedSubject.getSubjectName())) {
                        index++;
                    } else {
                        temp.getSubjects().set(index,updatedSubject);
                        return temp;
                    }
                }
            }
        }
        Utils.validateNumber(check);
        throw new RuntimeException("존재하지 않는 과목 입니다.");
    }
}
