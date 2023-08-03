
public class PrettyPrinting {
    // use System.out.printf(); for Pretty Printing
    // %s = String (any String)
    // %d = numbers (any integer)
    // %.Nf decimals (N digits after .) (rounds off for decimals)
    public static void main(String[] args) {
        System.out.printf("hi my name is %s and my marks are %d adn %.3f", "sandip", 1211111111, 89.99999999);
        System.out.println(); // printing next line
        System.out.printf("%s,%s,%s are my friends", "ashish", "diggi", "me");
    }
    // %s String of text
    // %f floating point value (float or double)
    // %e Exponential, scientific notation of a float or double
    // %b boolean true or false value
    // %c Single character char
    // %d Base 10 integer, such as a Java int, long, short or byte
    // %o Octal number
    // %x Hexadecimal number
    // %% Percentage sign
    // %n New line, aka carriage-return
    // %tY Year to four digits
    // %tT Time in format of HH:MM:SS ( ie 21:46:30)
}
