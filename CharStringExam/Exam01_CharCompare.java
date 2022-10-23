package CharStringExam;

public class Exam01_CharCompare {
    static char c1 = 'f';
    static char c2 = 'g';
    
    public static void main(String[] args) {
        if (c1 > c2)
            System.out.println("c1 > c2");
        else if (c1 == c2)
            System.out.println("c1 == c2");
        else
            System.out.println("c1 < c2");

        System.out.printf("c1 is (%d) < c2 is (%d)", Character.getNumericValue(c1), Character.getNumericValue(c2));
    }

}