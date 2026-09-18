import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.Scanner;

public class Main {
    private static Path path(Scanner s, String prompt) {
        System.out.print(prompt);
        String value = s.nextLine().trim();
        if (value.isEmpty()) throw new IllegalArgumentException("Шлях порожній");
        return Paths.get(value);
    }
    private static char key(Scanner s) {
        System.out.print("Ключ — один символ: ");
        String value = s.nextLine();
        if (value.length() != 1 || Character.isSurrogate(value.charAt(0)))
            throw new IllegalArgumentException("Введіть один символ, наприклад K");
        return value.charAt(0);
    }
    static void demo() throws IOException, ClassNotFoundException {
        Path folder = Paths.get("demo"); Files.createDirectories(folder);
        Path input = folder.resolve("input.txt");
        FileStorage.writeText(input, "Java streams\nЦей рядок містить найбільшу кількість слів\nКороткий рядок\n");
        System.out.println("ЗАВДАННЯ 1: максимум слів");
        System.out.println(FileStorage.maxWords(input));
        System.out.println("\nЗАВДАННЯ 2: серіалізація реєстру");
        Tram.CardSystem system = new Tram.CardSystem();
        system.issue(Tram.CardType.STUDENT, Tram.Plan.FIVE_RIDES, LocalDate.of(2026, 9, 16));
        Tram.Card card = system.issue(Tram.CardType.REGULAR, Tram.Plan.BALANCE, LocalDate.of(2026, 9, 16));
        system.topUp(card.id, 3000);
        FileStorage.save(folder.resolve("cards.dat"), system);
        Tram.CardSystem loaded = FileStorage.load(folder.resolve("cards.dat"), Tram.CardSystem.class);
        System.out.println("Збережено і прочитано demo/cards.dat"); loaded.showCards();
        System.out.println("Наступний ID після читання: "
                + loaded.issue(Tram.CardType.PUPIL, Tram.Plan.TEN_DAYS, LocalDate.of(2026, 9, 16)).id);
        System.out.println("\nЗАВДАННЯ 3: шифрування, ключ K");
        StreamCipher.encrypt(input, folder.resolve("encrypted.bin"), 'K');
        StreamCipher.decrypt(folder.resolve("encrypted.bin"), folder.resolve("restored.txt"), 'K');
        System.out.println("Створено encrypted.bin і restored.txt");
        System.out.println("Тексти однакові: " + FileStorage.readText(input).equals(FileStorage.readText(folder.resolve("restored.txt"))));
        System.out.println("\nЗАВДАННЯ 4: теги за HTTP URL контрольної сторінки");
        com.sun.net.httpserver.HttpServer server = DemoPage.start(0);
        try {
            String url = "http://127.0.0.1:" + server.getAddress().getPort() + "/page.html";
            System.out.println("URL: " + url);
            TagCounter.show(TagCounter.fromUrl(url));
        } finally { server.stop(0); }
        System.out.println("Для зовнішньої сторінки: java TagCounter https://example.com");
    }
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--demo")) {
            try { demo(); } catch (IOException | ClassNotFoundException e) { System.out.println("Помилка: " + e.getMessage()); }
            return;
        }
        Scanner s = new Scanner(System.in);
        while (true) {
            System.out.println("\n1 — Рядок із максимумом слів\n2 — Картки та файли"
                    + "\n3 — Шифрування\n4 — Дешифрування\n5 — Теги за URL\n0 — Вихід");
            int choice = Tram.readInt(s, "Ваш вибір: ", 0, 5);
            if (choice == 0) break;
            try {
                if (choice == 1) System.out.println(FileStorage.maxWords(path(s, "Шлях до текстового файлу: ")));
                else if (choice == 2) {
                    Tram.run(s);
                } else if (choice == 3 || choice == 4) {
                    Path input = path(s, "Вхідний файл: ");
                    Path output = path(s, "Вихідний файл: ");
                    char k = key(s);
                    if (choice == 3) StreamCipher.encrypt(input, output, k);
                    else StreamCipher.decrypt(input, output, k);
                    System.out.println("Готово: " + output.toAbsolutePath());
                } else {
                    System.out.print("URL (Enter — https://example.com): ");
                    String url = s.nextLine().trim();
                    TagCounter.show(TagCounter.fromUrl(url.isEmpty() ? "https://example.com" : url));
                }
            } catch (IOException | IllegalArgumentException e) {
                System.out.println("Помилка: " + e.getMessage());
            }
        }
        s.close();
    }
}
