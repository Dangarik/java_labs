import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Main {
    static int readInt(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min && value <= max) return value;
            } catch (NumberFormatException ignored) { }
            System.out.println("Введіть ціле число від " + min + " до " + max);
        }
    }
    static int[] readArray(Scanner scanner) {
        while (true) {
            System.out.print("Введіть від 1 до 50 цілих чисел через пробіл: ");
            try {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) throw new IllegalArgumentException("Рядок порожній");
                String[] parts = line.split("\\s+");
                if (parts.length > 50) throw new IllegalArgumentException("Забагато чисел");
                int[] values = new int[parts.length];
                for (int i = 0; i < parts.length; i++) values[i] = Integer.parseInt(parts[i]);
                return values;
            } catch (IllegalArgumentException e) {
                System.out.println("Помилка введення: " + e.getMessage());
            }
        }
    }
    static int[] randomArray(int count, Random random) {
        int[] values = new int[count];
        for (int i = 0; i < count; i++) values[i] = random.nextInt(100);
        return values;
    }
    static RedBlackTree build(int[] values) {
        RedBlackTree tree = new RedBlackTree();
        for (int value : values) tree.add(value);
        return tree;
    }
    static void compare(int[] values) {
        System.out.println("ДОВІЛЬНИЙ ПОРЯДОК");
        System.out.println("Порядок додавання: " + Arrays.toString(values));
        build(values).show();
        int[] sorted = values.clone();
        Arrays.sort(sorted);
        System.out.println("\nВПОРЯДКОВАНИЙ ПОРЯДОК");
        System.out.println("Порядок додавання: " + Arrays.toString(sorted));
        build(sorted).show();
    }
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--demo")) {
            compare(randomArray(12, new Random(9))); return;
        }
        try (Scanner scanner = new Scanner(System.in)) {
            RedBlackTree tree = new RedBlackTree();
            while (true) {
                System.out.println("\n1 — Додати числа з клавіатури\n2 — Нове дерево з випадкових чисел"
                        + "\n3 — Нове дерево з впорядкованих випадкових чисел"
                        + "\n4 — Показати дерево та обхід\n5 — Пошук числа"
                        + "\n6 — Порівняти два порядки введення\n0 — Вихід");
                int choice = readInt(scanner, "Ваш вибір: ", 0, 6);
                if (choice == 0) break;
                if (choice == 1) {
                    int[] values = readArray(scanner);
                    System.out.println("Порядок додавання: " + Arrays.toString(values));
                    for (int value : values)
                        if (!tree.add(value)) System.out.println("Дублікат пропущено: " + value);
                    tree.show();
                } else if (choice == 2 || choice == 3) {
                    int count = readInt(scanner, "Кількість чисел (1–50): ", 1, 50);
                    int[] values = randomArray(count, new Random());
                    System.out.println("Початковий масив: " + Arrays.toString(values));
                    if (choice == 3) Arrays.sort(values);
                    System.out.println("Порядок додавання: " + Arrays.toString(values));
                    tree = build(values); tree.show();
                } else if (choice == 4) tree.show();
                else if (choice == 5) {
                    int value = readInt(scanner, "Число для пошуку: ", Integer.MIN_VALUE, Integer.MAX_VALUE);
                    System.out.println(tree.contains(value) ? "Число знайдено" : "Числа немає");
                } else compare(readArray(scanner));
            }
        }
    }
}
