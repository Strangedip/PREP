import java.util.Scanner;

public class PalindromeString {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String s = in.next(); // we can convert it into charArray but lengthy
        checkPalindrome(s);
        checkPalindrome2(s);
        checkPalindrome3(s);

    }

    // checking end and start char without toCharArray
    static void checkPalindrome(String s) {
        int start = 0;
        int end = s.length() - 1;
        boolean palindrom = true;

        // check start char with last corresponding char
        while (start < end) {
            // if both are equal increase start and reduce end
            if (s.charAt(start) == s.charAt(end)) {
                end--;
                start++;
            }
            // if not equal break and mark not palindrom
            else {
                palindrom = false;
                break;
            }
        }
        System.out.println(palindrom ? "yes" : "no");
    }

    // checking end and start char with toCharArray
    static void checkPalindrome2(String str) {
        char[] arr = str.toCharArray();
        int start = 0;
        int end = arr.length - 1;
        boolean palindrom = true;

        // check start char with last corresponding char in array
        while (start < end) {
            // if both are equal increase start and reduce end
            if (arr[start] == arr[end]) {
                end--;
                start++;
            }
            // if not equal break and mark not palindrom
            else {
                palindrom = false;
                break;
            }
        }
        System.out.println(palindrom ? "yes" : "no");
    }

    // concatenating with empty string in reverse order
    static void checkPalindrome3(String s) {
        String s2 = "";
        for (int i = s.length() - 1; i >= 0; i--) {
            s2 = s2 + s.charAt(i);
        }
        System.out.println(s2.equals(s) ? "yes" : "no");
    }
}