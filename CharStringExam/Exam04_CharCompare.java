package CharStringExam;

public class Exam04_CharCompare {
    

    public static void main(String[] args) {

        String c1 = "1";
        String c2 = "k";

        if (c1.charAt(0) == '1') {
            System.out.println("char '1' is equal");
        }

        if (c2.charAt(0) == 'k') {
            System.out.println("char 'k' is equal");
        }

        countString(c1);
        countString(c2);

    }
    
    /**
     * @param str
     */
    private static void countString(String str) {
        
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '1') {
                System.out.println("ch1 char '1' is equal");
            }
            if (str.charAt(i) == 'k') {
                System.out.println("ch2 char 'k' is equal");
            }
        }
    }
}
