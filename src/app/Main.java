package app;

import java.util.List;
import java.util.Scanner;
import utils.Utils;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("접근할 파일 명을 입력해주세요 : ");
        String fileName = scanner.next();
        FileConnector fileConnector = FileConnector.fileConnector(fileName);
        while (true) {
            System.out.println("1.개인성적조회 2.전체조회 3.성적입력 4.성적수정 5.삭제 6.종료");
            int menu = scanner.nextInt();
            if(menu == 1 ) {
                System.out.println("학번을 입력하세요 :");
                int studentNum = scanner.nextInt();
                fileConnector.printOne(studentNum);
            }
            if(menu == 2 ) {
                fileConnector.printAll();
            }
            if(menu == 3) {
                System.out.print("이름을 입력하세요 : ");
                String name = scanner.next();
                System.out.print("전공을 입력하세요 : ");
                String major = scanner.next();
                System.out.println("과목과 점수를 입력하세요 ex)국어:77, 수학:27 ");
                scanner.nextLine();
                String input = scanner.nextLine();
                List<Subject> subjects =  Utils.createSubjectList(input);
                Grade grade = new Grade(name,major,subjects);
                fileConnector.write(grade);
            }
            if(menu == 4 ) {
                System.out.print("학번을 입력하세요 : ");
                int inNum = scanner.nextInt();
                System.out.print("수정할 과목을 입력하세요 : ");
                String inSubject = scanner.next();
                System.out.print("수정할 점수를 입력하세요 : ");
                int score = scanner.nextInt();
                List<Grade> grades = fileConnector.read();
                Subject updatedSubject  = new Subject(inSubject,score);
                Grade grade = Subject.subjectToGrade(grades,updatedSubject,inNum);
                fileConnector.update(grade,inNum);
            }
            if(menu == 5 ) {
                System.out.print("학번을 입력하세요 : ");
                int inNum = scanner.nextInt();
                fileConnector.delete(inNum);
            }
            if(menu == 6) {
                System.out.println("종료 합니다.");
                break;
            }
        }
    }
}