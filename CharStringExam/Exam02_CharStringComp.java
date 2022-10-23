package CharStringExam;

public class Exam02_CharStringComp {
    
    static char c = 'a';

    static String inputC = "strings";

    public static void main(String[] args) {
        if(c == 'a') {   // char형 비교 방법
            System.out.println("char형 'a' is equal");
        }

        if ("strings".equals(inputC)) {  //stirng형 비교 방법
            System.out.println("strings형 'strings' is equal");
        }
    }
    
}
