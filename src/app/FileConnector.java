package app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import utils.Utils;

public class FileConnector implements TextFileDataAccess<Grade> {
    private static FileConnector instance;
    private static String fileName;
    private List<Grade> grades;

    private FileConnector(String fileName){
        this.fileName = fileName;
        this.grades = read();
    }

    public  static FileConnector fileConnector(String fileName){
        if(instance == null) {
            instance = new FileConnector(fileName);
        }
        return instance;
    }

    public List<Grade> refreshGrades() {
        return read();
    }

    public void printOne(int inNum) {
        refreshGrades();
        Boolean check = false;
        for(Grade temp : grades) {
            if (temp.getNumber() == inNum) {
                check = true;
                System.out.println(temp.getName() + " 학생은 " + temp.getSubjects().size()  + "과목을 수강했고, "+"평균은" + Utils.getCalculateAverage(temp.getSubjects())+ "점입니다.");
            }
        }
        Utils.validateNumber(check);
    }
    public void printAll(){
        refreshGrades();
        for(Grade temp : grades) {
            StringBuilder sb = new StringBuilder();
            StringBuilder subjectBuilder = new StringBuilder();
            for(Subject subject : temp.getSubjects()) {
                subjectBuilder.append(subject.getSubjectName())
                        .append(":")
                        .append(subject.getScore())
                        .append("  ");
            }
            sb.append("이름 : ").append(temp.getName())
                    .append("   학번 : ").append(temp.getNumber())
                    .append("   전공 : ").append(temp.getMajor())
                    .append("   성적 -> ").append(subjectBuilder);
            System.out.println(sb);
        }
    }
    @Override
    public void write(Grade grade) {
        try {
            Path path = FileSystems.getDefault().getPath(fileName+".txt");
            File file = path.toFile();
            FileWriter fw = new FileWriter(file, true);
            StringBuilder sb = new StringBuilder();
            BufferedWriter writer = new BufferedWriter(fw);
            Utils.appendData(grade, sb);
            if(grades.isEmpty()) {
                writer.write(grade.getName()+"/"+0+"/"+grade.getMajor()+ sb);
            }else {
                writer.write(grade.getName()+"/"+(grades.get(grades.size()-1).getNumber()+1)+"/"+grade.getMajor()+sb);
            }
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Grade grade, int inNum){
        Boolean check = false;
        for(int i = 0 ; i<grades.size(); i++){
            if(grades.get(i).getNumber() == inNum) {
                grades.set(i,grade);
                check = true;
                break;
            }
        }
        Utils.validateNumber(check);
        overwrite(fileName);
    }

    @Override
    public List<Grade> read() {
        grades = new ArrayList<>();
        try {
            File file = new File(fileName+".txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedReader in = new BufferedReader(new FileReader(fileName+".txt"));
            String s;
            while ((s = in.readLine()) != null) {
                String[] splitString = s.split("/");
                String name = splitString[0];
                int number = Integer.valueOf(splitString[1]);
                String major = splitString[2];

                List<Subject> subjects = new ArrayList<>();
                for(int i = 3 ; i<splitString.length; i++) {
                    subjects.add(new Subject(splitString[i].split(" ")[0],Double.parseDouble(splitString[i].split(" ")[1])));
                }
                Grade grade = new Grade(name, number, major,subjects);
                grades.add(grade);
            }
            in.close();
            return grades;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return grades;
    }
    @Override
    public void delete(int inNum){
        boolean check = false;
        for(Grade temp : grades) {
            if(temp.getNumber() == inNum) {
                check = true;
                grades.remove(temp);
                break;
            }
        }
        Utils.validateNumber(check);
        overwrite(fileName);
    }

    private void overwrite(String fileName)  {
        try {
            Path path = FileSystems.getDefault().getPath(fileName+".txt");
            File file = path.toFile();
            FileWriter fw = new FileWriter(file);
            BufferedWriter writer = new BufferedWriter(fw);
            for(Grade temp : grades){
                StringBuilder sb = new StringBuilder();
                Utils.appendData(temp, sb);
                writer.write(temp.getName()+"/"+temp.getNumber()+"/"+temp.getMajor()+sb);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

