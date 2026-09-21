import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Scanner;
import java.util.logging.Level;

public class Main {
    private static final I18n UI = new I18n();
    private static final Path LOG_FILE = Paths.get("logs", "app.log");
    private static String input(Scanner scanner, String key) {
        System.out.println(UI.text(key));
        return scanner.nextLine();
    }
    private static void cipher(Scanner scanner, boolean encrypt) throws IOException {
        Path source = Paths.get(input(scanner, "input.file"));
        Path result = Paths.get(input(scanner, "output.file"));
        String key = input(scanner, "key");
        if (key.length() != 1) throw new IllegalArgumentException("Key must be one char");
        if (encrypt) StreamCipher.encrypt(source, result, key.charAt(0));
        else StreamCipher.decrypt(source, result, key.charAt(0));
        System.out.println(UI.text("saved", result));
    }
    private static void language(Scanner scanner) {
        String choice = input(scanner, "language.options");
        if (choice.equals("1")) UI.setLanguage("uk");
        else if (choice.equals("2")) UI.setLanguage("en");
        else throw new IllegalArgumentException("Unknown language");
        AppLog.LOG.info("Interface language changed");
        System.out.println(UI.text("language.changed"));
    }
    private static void demo(Scanner scanner) throws Exception {
        ReflectionDemo.run(scanner, UI);
        Path dir = Paths.get("demo");
        Files.createDirectories(dir);
        Path original = dir.resolve("input.txt");
        Path encrypted = dir.resolve("encrypted.bin");
        Path restored = dir.resolve("restored.txt");
        String text = "Java та український текст.\nПеревірка шифрування!\n";
        Files.write(original, text.getBytes(StandardCharsets.UTF_8));
        StreamCipher.encrypt(original, encrypted, 'K');
        StreamCipher.decrypt(encrypted, restored, 'K');
        boolean same = text.equals(new String(Files.readAllBytes(restored), StandardCharsets.UTF_8));
        System.out.println(UI.text("roundtrip", same));
        try {
            StreamCipher.encrypt(dir.resolve("missing.txt"), dir.resolve("unused.bin"), 'K');
        } catch (IOException e) {
            AppLog.LOG.log(Level.SEVERE, "Expected missing-file error", e);
            System.out.println(UI.text("demo.error"));
        }
        AppLog.demo(UI);
        System.out.println(UI.text("log.path", LOG_FILE));
    }
    public static void main(String[] args) {
        try {
            AppLog.setup(LOG_FILE);
        } catch (IOException e) {
            System.err.println(UI.text("log.failed"));
            AppLog.close();
            return;
        }
        try (Scanner scanner = new Scanner(System.in, "UTF-8")) {
            AppLog.LOG.info("Application started");
            if (args.length > 0 && args[0].equals("--demo")) {
                demo(scanner);
                return;
            }
            while (true) {
                System.out.println(UI.text("menu"));
                if (!scanner.hasNextLine()) break;
                String choice = scanner.nextLine().trim();
                try {
                    switch (choice) {
                        case "1": ReflectionDemo.run(scanner, UI); break;
                        case "2": cipher(scanner, true); break;
                        case "3": cipher(scanner, false); break;
                        case "4": AppLog.demo(UI); break;
                        case "5": language(scanner); break;
                        case "6":
                            System.out.println(UI.text("log.path", LOG_FILE));
                            System.out.println(new String(Files.readAllBytes(LOG_FILE), StandardCharsets.UTF_8));
                            break;
                        case "0": return;
                        default:
                            System.out.println(UI.text("invalid"));
                            AppLog.LOG.warning("Invalid menu choice: " + choice);
                    }
                } catch (Exception e) {
                    AppLog.LOG.log(Level.SEVERE, "Operation failed", e);
                    if (e.getClass().getSimpleName().equals("InaccessibleObjectException"))
                        System.out.println(UI.text("reflection.failed"));
                    else System.out.println(UI.text("operation.failed"));
                }
            }
        } catch (Exception e) {
            AppLog.LOG.log(Level.SEVERE, "Application error", e);
            System.out.println(UI.text("operation.failed"));
        } finally {
            AppLog.LOG.info("Application finished");
            AppLog.close();
        }
    }
}
