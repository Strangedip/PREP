import java.util.*;

/**
 * Problem: Integer to Roman
 * 
 * Roman numerals are represented by seven different symbols: I, V, X, L, C, D and M.
 * 
 * Symbol       Value
 * I             1
 * V             5
 * X             10
 * L             50
 * C             100
 * D             500
 * M             1000
 * 
 * For example, 2 is written as II in Roman numeral, just two one's added together. 
 * 12 is written as XII, which is simply X + II. The number 27 is written as XXVII, which is XX + V + II.
 * 
 * Roman numerals are usually written largest to smallest from left to right. However, the numeral 
 * for four is not IIII. Instead, the number four is written as IV. Because the one is before the 
 * five we subtract it making four. The same principle applies to the number nine, which is written as IX.
 * 
 * There are six instances where subtraction is used:
 * - I can be placed before V (5) and X (10) to make 4 and 9.
 * - X can be placed before L (50) and C (100) to make 40 and 90.
 * - C can be placed before D (500) and M (1000) to make 400 and 900.
 * 
 * Given an integer, convert it to a roman numeral.
 * 
 * Example:
 * Input: num = 3
 * Output: "III"
 * 
 * Example 2:
 * Input: num = 58
 * Output: "LVIII"
 * Explanation: L = 50, V = 5, III = 3.
 * 
 * Example 3:
 * Input: num = 1994
 * Output: "MCMXCIV"
 * Explanation: M = 1000, CM = 900, XC = 90 and IV = 4.
 */
public class IntegerToRoman {
    
    /**
     * APPROACH 1: GREEDY WITH ARRAYS (Most Intuitive)
     * Time Complexity: O(1) - At most 13 symbols to process
     * Space Complexity: O(1) - Fixed size arrays
     * 
     * Use arrays to store all possible Roman numeral mappings in descending order.
     * Greedily subtract the largest possible value at each step.
     */
    public String intToRomanGreedy(int num) {
        // All possible values in descending order
        int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] symbols = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < values.length; i++) {
            while (num >= values[i]) {
                num -= values[i];
                result.append(symbols[i]);
            }
        }
        
        return result.toString();
    }
    
    /**
     * APPROACH 2: HARDCODED MAPPINGS
     * Time Complexity: O(1)
     * Space Complexity: O(1)
     * 
     * Use hardcoded arrays for thousands, hundreds, tens, and ones.
     * Directly map each digit to its Roman representation.
     */
    public String intToRomanHardcoded(int num) {
        String[] thousands = {"", "M", "MM", "MMM"};
        String[] hundreds = {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
        String[] tens = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
        String[] ones = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};
        
        return thousands[num / 1000] + 
               hundreds[(num % 1000) / 100] + 
               tens[(num % 100) / 10] + 
               ones[num % 10];
    }
    
    /**
     * APPROACH 3: RECURSIVE APPROACH
     * Time Complexity: O(log num) - Based on number of recursive calls
     * Space Complexity: O(log num) - Recursion stack
     * 
     * Recursively break down the number by finding the largest Roman numeral
     * that fits and recursively convert the remainder.
     */
    public String intToRomanRecursive(int num) {
        if (num == 0) return "";
        
        if (num >= 1000) return "M" + intToRomanRecursive(num - 1000);
        if (num >= 900) return "CM" + intToRomanRecursive(num - 900);
        if (num >= 500) return "D" + intToRomanRecursive(num - 500);
        if (num >= 400) return "CD" + intToRomanRecursive(num - 400);
        if (num >= 100) return "C" + intToRomanRecursive(num - 100);
        if (num >= 90) return "XC" + intToRomanRecursive(num - 90);
        if (num >= 50) return "L" + intToRomanRecursive(num - 50);
        if (num >= 40) return "XL" + intToRomanRecursive(num - 40);
        if (num >= 10) return "X" + intToRomanRecursive(num - 10);
        if (num >= 9) return "IX" + intToRomanRecursive(num - 9);
        if (num >= 5) return "V" + intToRomanRecursive(num - 5);
        if (num >= 4) return "IV" + intToRomanRecursive(num - 4);
        if (num >= 1) return "I" + intToRomanRecursive(num - 1);
        
        return "";
    }
    
    /**
     * APPROACH 4: USING TREEMAP
     * Time Complexity: O(log 13) = O(1) - TreeMap operations on fixed size
     * Space Complexity: O(1) - Fixed size TreeMap
     * 
     * Use TreeMap to maintain sorted order of Roman numerals.
     * Automatically handles descending order traversal.
     */
    public String intToRomanTreeMap(int num) {
        TreeMap<Integer, String> map = new TreeMap<>(Collections.reverseOrder());
        map.put(1000, "M");
        map.put(900, "CM");
        map.put(500, "D");
        map.put(400, "CD");
        map.put(100, "C");
        map.put(90, "XC");
        map.put(50, "L");
        map.put(40, "XL");
        map.put(10, "X");
        map.put(9, "IX");
        map.put(5, "V");
        map.put(4, "IV");
        map.put(1, "I");
        
        StringBuilder result = new StringBuilder();
        
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            int value = entry.getKey();
            String symbol = entry.getValue();
            
            while (num >= value) {
                num -= value;
                result.append(symbol);
            }
        }
        
        return result.toString();
    }
    
    /**
     * APPROACH 5: DIGIT-BY-DIGIT CONVERSION
     * Time Complexity: O(1) - At most 4 digits to process
     * Space Complexity: O(1)
     * 
     * Process each digit (thousands, hundreds, tens, ones) separately
     * and convert to Roman representation.
     */
    public String intToRomanDigitByDigit(int num) {
        StringBuilder result = new StringBuilder();
        
        // Thousands place
        int thousands = num / 1000;
        for (int i = 0; i < thousands; i++) {
            result.append("M");
        }
        num %= 1000;
        
        // Hundreds place
        int hundreds = num / 100;
        result.append(convertDigitToRoman(hundreds, "C", "D", "M"));
        num %= 100;
        
        // Tens place
        int tens = num / 10;
        result.append(convertDigitToRoman(tens, "X", "L", "C"));
        num %= 10;
        
        // Ones place
        result.append(convertDigitToRoman(num, "I", "V", "X"));
        
        return result.toString();
    }
    
    /**
     * Helper method to convert a single digit to Roman numeral
     */
    private String convertDigitToRoman(int digit, String one, String five, String ten) {
        if (digit == 0) return "";
        if (digit <= 3) return one.repeat(digit);
        if (digit == 4) return one + five;
        if (digit <= 8) return five + one.repeat(digit - 5);
        if (digit == 9) return one + ten;
        return "";
    }
    
    /**
     * APPROACH 6: OPTIMIZED LINEAR SCAN
     * Time Complexity: O(1)
     * Space Complexity: O(1)
     * 
     * Similar to approach 1 but with optimized loop structure.
     */
    public String intToRomanOptimized(int num) {
        int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] symbols = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        
        StringBuilder result = new StringBuilder();
        int i = 0;
        
        while (num > 0 && i < values.length) {
            if (num >= values[i]) {
                int count = num / values[i];
                num %= values[i];
                
                for (int j = 0; j < count; j++) {
                    result.append(symbols[i]);
                }
            }
            i++;
        }
        
        return result.toString();
    }
    
    /**
     * APPROACH 7: SWITCH-CASE APPROACH
     * Time Complexity: O(1)
     * Space Complexity: O(1)
     * 
     * Use switch statements to handle different ranges of numbers.
     */
    public String intToRomanSwitch(int num) {
        StringBuilder result = new StringBuilder();
        
        while (num > 0) {
            if (num >= 1000) {
                result.append("M");
                num -= 1000;
            } else if (num >= 900) {
                result.append("CM");
                num -= 900;
            } else if (num >= 500) {
                result.append("D");
                num -= 500;
            } else if (num >= 400) {
                result.append("CD");
                num -= 400;
            } else if (num >= 100) {
                result.append("C");
                num -= 100;
            } else if (num >= 90) {
                result.append("XC");
                num -= 90;
            } else if (num >= 50) {
                result.append("L");
                num -= 50;
            } else if (num >= 40) {
                result.append("XL");
                num -= 40;
            } else if (num >= 10) {
                result.append("X");
                num -= 10;
            } else if (num >= 9) {
                result.append("IX");
                num -= 9;
            } else if (num >= 5) {
                result.append("V");
                num -= 5;
            } else if (num >= 4) {
                result.append("IV");
                num -= 4;
            } else {
                result.append("I");
                num -= 1;
            }
        }
        
        return result.toString();
    }
    
    // Test method to demonstrate all approaches
    public static void main(String[] args) {
        IntegerToRoman solution = new IntegerToRoman();
        
        // Test case 1: Simple number
        int num1 = 3;
        System.out.println("Test Case 1: " + num1);
        System.out.println("Greedy: " + solution.intToRomanGreedy(num1));
        System.out.println("Hardcoded: " + solution.intToRomanHardcoded(num1));
        System.out.println("Recursive: " + solution.intToRomanRecursive(num1));
        System.out.println("TreeMap: " + solution.intToRomanTreeMap(num1));
        System.out.println("Digit by Digit: " + solution.intToRomanDigitByDigit(num1));
        System.out.println("Optimized: " + solution.intToRomanOptimized(num1));
        System.out.println("Switch: " + solution.intToRomanSwitch(num1));
        System.out.println();
        
        // Test case 2: Medium complexity
        int num2 = 58;
        System.out.println("Test Case 2: " + num2);
        System.out.println("Greedy: " + solution.intToRomanGreedy(num2));
        System.out.println("Hardcoded: " + solution.intToRomanHardcoded(num2));
        System.out.println();
        
        // Test case 3: Complex with subtractive cases
        int num3 = 1994;
        System.out.println("Test Case 3: " + num3);
        System.out.println("Greedy: " + solution.intToRomanGreedy(num3));
        System.out.println("Hardcoded: " + solution.intToRomanHardcoded(num3));
        System.out.println("Recursive: " + solution.intToRomanRecursive(num3));
        System.out.println();
        
        // Test case 4: Edge cases
        int[] testCases = {1, 4, 5, 9, 10, 40, 50, 90, 100, 400, 500, 900, 1000, 3999};
        System.out.println("Edge Cases:");
        for (int num : testCases) {
            System.out.println(num + " -> " + solution.intToRomanGreedy(num));
        }
        
        // Performance test
        performanceTest(solution);
    }
    
    private static void performanceTest(IntegerToRoman solution) {
        System.out.println("\n=== Performance Test ===");
        
        long start, end;
        int iterations = 100000;
        
        // Test Greedy approach
        start = System.nanoTime();
        for (int i = 1; i <= iterations; i++) {
            solution.intToRomanGreedy(i % 3999 + 1);
        }
        end = System.nanoTime();
        System.out.println("Greedy: " + (end - start) / 1000000.0 + " ms");
        
        // Test Hardcoded approach
        start = System.nanoTime();
        for (int i = 1; i <= iterations; i++) {
            solution.intToRomanHardcoded(i % 3999 + 1);
        }
        end = System.nanoTime();
        System.out.println("Hardcoded: " + (end - start) / 1000000.0 + " ms");
        
        // Test TreeMap approach
        start = System.nanoTime();
        for (int i = 1; i <= iterations; i++) {
            solution.intToRomanTreeMap(i % 3999 + 1);
        }
        end = System.nanoTime();
        System.out.println("TreeMap: " + (end - start) / 1000000.0 + " ms");
        
        // Test Optimized approach
        start = System.nanoTime();
        for (int i = 1; i <= iterations; i++) {
            solution.intToRomanOptimized(i % 3999 + 1);
        }
        end = System.nanoTime();
        System.out.println("Optimized: " + (end - start) / 1000000.0 + " ms");
    }
} 