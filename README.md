## 싱글톤 패턴이란?
> 싱글톤 패턴은 애플리케이션이 처음 실행될 때 메모리에 인스턴스를 단 한 번만 할당하여 이후에는 해당 인스턴스를 계속 사용하는 디자인 패턴

## 싱글톤 패턴의 장점과 단점
### 장점 
 * 싱글톤 패턴은 메모리에 인스턴스를 할당 해 두기 때문에 추후에 해당 인스턴스에 접근 할 때 메모리 낭비를 방지 할 수 있습니다.
* 인스턴스의 유일성을 보장 받을 수 있습니다.
* 전역으로 사용하는 인스턴스로서, 데이터 공유가 편리합니다.

### 단점
* 하나의 인스턴스를 통해 일들을 수행하는데 해당 인스턴스의 생성자가 private 이기 때문에 Mocking 이 어려워 테스트 코드 작성이 어려워집니다.
* 하나의 인스턴스가 여러 일을 수행 하기 때문에 단일 책임원칙을 위반하게 되고, 객체지향적이지 못합니다.

### 싱글톤 선택 이유

싱글톤을 사용할 때는 보통 스레드 안전성을 고려해야 하지만, 본 프로그램은 서버가 아닌 단일 사용자 프로그램이므로 synchronized, volatile, Lazy Holder와 같은 스레드 동기화 방식은 고려하지 않았습니다.

프로그램의 목적은 하나의 인스턴스로 여러 작업을 처리하여 간단하게 구현하는 것이며, Java 기초를 복습하면서 싱글톤 패턴을 학습할 기회가 되리라 판단했습니다.

또한, 메모장을 데이터베이스처럼 가정하여 학교나 학급별로 독립된 데이터베이스에 연결하는 것처럼 파일별 커넥션을 설정하여 성적 데이터를 효율적으로 관리하고자 했습니다.

실제로는 멀티스레드 환경이 아니기 때문에 여러 커넥션이 필요하지 않지만, 메모장 파일에 접근하지 않으면 기능을 수행할 수 없으므로, 싱글톤 패턴을 통해 하나의 커넥션 인스턴스를 공유하는 방식으로 구현했습니다.



이러한 특징을 가진 싱글톤 패턴을 이용하여 자바파일시스템을 활용한 간단한? 성적 관리 프로그램을 만들어 봤습니다.
   
## 싱글톤 인스턴스 생성

- 싱글톤 인스턴스 생성 과정

~~~ java
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

    ...

}

~~~

~~~ java
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("접근할 파일 명을 입력해주세요 : ");
        String fileName = scanner.next();
        TextFileDataAccess fileConnector = FileConnector.fileConnector(fileName);
     ...
}

~~~
FileConnector 생성자는 private 으로 외부에서 생성하지 못하게 막아두고,
Main에서 싱글톤 인스턴스 fileConnector 를 생성 했을 때 FileConnector의 instance의 초기값은 null 이기 때문에 접근할 파일명과 함께 새로운 인스턴스가 만들어지게 됩니다.





## 인터페이스

~~~ java
public interface TextFileDataAccess<T> {
    void update(T o1, int pk);
    List<?> read();
    void write(T o1);
    void delete(int inNum);
}
~~~
지금은 성적에 관한 데이터만 관리하지만 학생 정보나 과목 정보와 같이 다양한 유형의 데이터에 대해서도 확장 가능성을 생각해 다음과 같이 인터페이스를 사용하였습니다.

- read 메서드 (R)
~~~ java
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

~~~
CRUD 에서 R을 담당합니다.
먼저 메모장을 데이터베이스 처럼 사용하기 위해서 이름,학번,전공,과목 및 점수 순으로 "/"를 단위로 구분지었습니다.
read() 메서드는 메모장에 정보들을 읽어와 싱글톤 인스턴스(FileConnector)의 필드에 있는 성적 객체 리스트로 옮기는 역할을 합니다.

- write 메서드 (C)

~~~ java
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
    
    
    --- Utils.appendData
    public static void appendData(Grade temp, StringBuilder sb) {
        for(Subject subject : temp.getSubjects()){
            sb.append("/").
                    append(subject.getSubjectName())
                    .append(" ")
                    .append(subject.getScore());
        }
    }
~~~
CRUD에서 C를 담당합니다.
Main에서 입력받은 데이터들을 통해 Grade 객체를 생성하고 그 객체를 메모장에 "/"로 구분하여 정보들을 등록하는 메서드 입니다.
학번은 RDBMS의 autoincrement 처럼 처음 등록하는 경우이면 학번은 0으로 생성되고, 앞에 등록된 학번이 있으면 1증가하여 자동으로 등록시켜주도록 구현했습니다.

- delete 메서드 (D)
~~~ java
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
    
    
    --- overwrite 메서드
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
~~~
CRUD에서 D를 담당합니다.
현재 FileConnector 가 가지고 있는 성적객체 리스트에서 입력받은 학번을 찾아 삭제하도록 하였습니다.
overwrite 메서드가 추가된 이유는 자바 파일 시스템에서 메모장을 조작할 때 특정 줄이나 단어만 수정할 수 없기 때문입니다. 따라서 특정 라인을 삭제하는 대신에 삭제할 정보를 리스트에서 삭제한 다음, 해당 정보를 새로운 정보로 덮어쓰는 작업이 필요합니다. 이를 위해 overwrite 메서드를 추가하여 기존 정보를 덮어쓰는 기능을 구현하였습니다.

- update 메서드 (U)
~~~ java
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
~~~
과목 점수 수정 기능의 메서드로 CRUD에서 U를 담당합니다.
Delete 와 로직이 비슷하지만 수정을 위한 Grade를 생성하는 과정에서 과목을 수정하여야 하기 때문에 Subject 클래스에 아래와 같은 static 메서드를 사용하게 되었습니다.
~~~ java
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
~~~


## 실행결과
![](https://velog.velcdn.com/images/one_more_light/post/c1b22a6d-438d-4ae2-9f1e-3320be104c8a/image.png)
![](https://velog.velcdn.com/images/one_more_light/post/1860eefc-17c6-4edf-95a3-d247cca8baa3/image.png)
![](https://velog.velcdn.com/images/one_more_light/post/37a2463a-2a57-42fe-aadf-0d4baee0e136/image.png)
![](https://velog.velcdn.com/images/one_more_light/post/b4665102-9d87-4f37-8c4c-16d65f3b2781/image.png)
![](https://velog.velcdn.com/images/one_more_light/post/0395a54a-cc6b-4303-95b9-7718b6d6bc09/image.png)
![](https://velog.velcdn.com/images/one_more_light/post/91c80342-3136-4263-a1df-273743cf9b3c/image.png)
