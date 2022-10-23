package CharStringExam;

public class Exam03_AllCharCompare {

    public static void main(String[] args) {

        CharacterCompare();
        EqualCompare();
        StringMatches();

    }

    private static void CharacterCompare() {
        char char1 = 'z';
        char char2 = 'z';

         if (Character.compare(char1, char2) > 0) {
            System.out.println(char1 + " is greater");
        } else if (Character.compare(char1, char2) < 0) {
            System.out.println(char1 + " is less than " + char2);
        } else if (Character.compare(char1, char2) == 0) {
            System.out.println(char1 + " and " + char2 + " are equal");
        } else {
            System.out.println(char1 + " and " + char2 + " are invalid characters");
        }
    }

    private static void EqualCompare() {
        char char1 = 'a';
        char char2 = 'b';
        char char3 = 'a';


        if (char1 == char2) {
            System.out.println("Char1 and Char2 are equal");
        } else {
            System.out.println("Char1 and Char2 are not equal");
        }

        if(char1 == char3){
            System.out.println("Char1 and Char3 are equal");
        }else{
            System.out.println("Char1 and Char3 are not equal");
        }
    }

    private static void StringMatches() {
        char char1 = 'a';

        if (Character.toString(char1).matches("[a-z?]")) {
            System.out.println("The character matches");
        } else {
            System.out.println("The character does not match");
        }
    }

}
