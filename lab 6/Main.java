import java.util.Arrays;
import java.util.Scanner;

public class Main {

    public static String[] findWords(String input) {
        String[] words = input.trim().split("\\s+");
        String[] result = new String[words.length];
        int resultSize = 0;

        for (String word : words) {

            if (!word.matches("[A-Za-z]+")) {
                continue;
            }

            int vowels = 0;
            int consonants = 0;

            for (int i = 0; i < word.length(); i++) {
                char symbol = Character.toLowerCase(word.charAt(i));

                if ("aeiou".indexOf(symbol) != -1) {
                    vowels++;
                } else {
                    consonants++;
                }
            }

            if (vowels == consonants) {
                result[resultSize] = word;
                resultSize++;
            }
        }

        return Arrays.copyOf(result, resultSize);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введіть рядок зі словами:");
        String input = scanner.nextLine();

        String[] result = findWords(input);

        System.out.println("Результат:");
        System.out.println(Arrays.toString(result));

        scanner.close();
    }
}