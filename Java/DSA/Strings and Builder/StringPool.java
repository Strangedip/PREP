import java.util.Scanner;

public class StringPool {

    // all Strings will be stored inside String pool in heap area
    // no Duplicate value will be present inside String loop
    //*in String loops even for 2 variable only 1 String object will be present if bot have same value
    //*Strings are immutable because of String pool will cause issue if changing any value
    public static void main(String[] args) {
        String a="str";
        Scanner in=new Scanner(System.in);
        // String b="str1";
        String b=in.next();
        System.out.println(a.equals(b));
    }
}
