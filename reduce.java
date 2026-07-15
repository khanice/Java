import java.util.List;

public class reduce {
    public static void main(String[] args) {
        List<Integer> numbers = List.of(3, 7, 2, 9, 4, 1, 8, 5);
        List<String> words = List.of("apple", "banana", "kiwi", "mango", "fig", "grape");

        // R1: Sum all numbers in the list
        int sum = numbers.stream().reduce(0, Integer::sum);
        System.out.println(sum);


        // R2: Multiply all numbers together (product of all)
        int product = numbers.stream().reduce(1, (a, b) -> a * b);
        System.out.println(product);

        // R3: Find the maximum number in the list
        int max = numbers.stream().reduce(Integer.MIN_VALUE, Integer::max);
        System.out.println(max);

        // R4: Find the minimum number in the list
        int min = numbers.stream().reduce(Integer.MAX_VALUE, Integer::min);
        System.out.println(min);

        // R5: Concatenate all words into a single string, separated by commas
        String concatenated = words.stream().reduce((a, b) -> a + ", " + b).orElse("");
        System.out.println(concatenated);

        // R6: Find the longest word using reduce
        String LongestWord = words.stream().reduce((a,b)->a.length()>b.length() ? a : b).orElse("");
        System.out.println(LongestWord);

        // R6: Find the alphabetically first word using reduce
        String firstWord = words.stream().reduce((a,b)->a.compareTo(b)>0? b : a).orElse("");
        System.out.println(firstWord);

        // R7: Count the total number of characters in all words combined
        int totalChars = words.stream().map(String::length).reduce(0, Integer::sum);
        System.out.println(totalChars);

    }
}
