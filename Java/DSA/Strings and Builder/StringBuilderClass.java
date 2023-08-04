public class StringBuilderClass {

    // StringBuilder is a class to create mutable String
    // used to concatenate objects(toStrings) without creating extra String object
    // it will modify string without creating new String object (less space consumed)
    public static void main(String[] args) {

        // StringBuilder sb= new StringBuilder();
        StringBuilder sb= new StringBuilder("a");
        StringBuilderClass r=new StringBuilderClass();
        sb.append('b');
        sb.append("c");
        sb.append(r); //appends class's toString value (address here)
        System.out.println(sb.append(false));
    }
}
