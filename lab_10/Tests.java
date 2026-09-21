import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;

public class Tests {
    private static int passed;
    private static void check(boolean ok, String name) {
        if (!ok) throw new AssertionError(name);
        System.out.println("PASS: " + name);
        passed++;
    }
    public static void main(String[] args) throws Exception {
        Path dir = Files.createTempDirectory("lab10-test-");
        Path log = dir.resolve("app.log");
        AppLog.setup(log);
        try {
            String target = new String("old value");
            String alias = target;
            target.hashCode();
            ReflectionDemo.change(target, "Україна");
            check(alias == target && alias.equals("Україна") &&
                target.hashCode() == new String("Україна").hashCode(), "same String, Unicode and hash cache");
            ReflectionDemo.change(target, "A");
            check(target.equals("A"), "Unicode to Latin storage");
            String zero = new String("");
            zero.hashCode();
            ReflectionDemo.change(zero, "abc");
            check(zero.hashCode() == "abc".hashCode(), "zero hash cache cleared");
            Path input = dir.resolve("input.txt");
            Path encrypted = dir.resolve("encrypted.bin");
            Path restored = dir.resolve("restored.txt");
            String text = "Java Україна 😀\n";
            Files.write(input, text.getBytes(StandardCharsets.UTF_8));
            StreamCipher.encrypt(input, encrypted, '\uffff');
            StreamCipher.decrypt(encrypted, restored, '\uffff');
            check(Arrays.equals(Files.readAllBytes(input), Files.readAllBytes(restored)), "cipher Unicode round-trip");
            boolean rejected = false;
            try { StreamCipher.encrypt(input, input, 'K'); }
            catch (java.io.IOException e) { rejected = true; }
            check(rejected && text.equals(new String(Files.readAllBytes(input), StandardCharsets.UTF_8)),
                "same input/output rejected without truncation");
            I18n ui = new I18n();
            check(ui.text("menu").contains("Вибір мови"), "Ukrainian interface");
            ui.setLanguage("en");
            check(ui.text("menu").contains("Select language"), "English interface");
            ResourceBundle uk = ResourceBundle.getBundle("location.messages", new Locale("uk"));
            ResourceBundle en = ResourceBundle.getBundle("location.messages", new Locale("en"));
            check(uk.keySet().equals(en.keySet()), "matching resource keys");
            AppLog.demo(ui);
            for (Handler handler : AppLog.LOG.getHandlers()) handler.flush();
            String contents = new String(Files.readAllBytes(log), StandardCharsets.UTF_8);
            check(contents.contains("D1_FINE") && contents.contains("D1_INFO") &&
                !contents.contains("D2_INFO") && contents.contains("D2_WARNING"), "file logging filters");
            for (Handler handler : AppLog.LOG.getHandlers()) {
                if (handler instanceof ConsoleHandler) {
                    check(!handler.isLoggable(new LogRecord(Level.FINE, "fine")) &&
                        handler.isLoggable(new LogRecord(Level.INFO, "info")), "console logging filters");
                }
            }
            System.out.println("All " + passed + " checks passed.");
        } finally {
            AppLog.close();
            try (java.util.stream.Stream<Path> paths = Files.walk(dir)) {
                paths.sorted(Comparator.reverseOrder()).forEach(path -> {
                    try { Files.delete(path); } catch (java.io.IOException e) { throw new RuntimeException(e); }
                });
            }
        }
    }
}
