import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class FileStorage {
    public static void save(Path path, Serializable data) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(path))) {
            out.writeObject(data); 
        }
    }

    public static <T> T load(Path path, Class<T> type) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(path))) {
            Object data = in.readObject();
            if (!type.isInstance(data)) throw new IOException("У файлі інший тип об'єкта");
            return type.cast(data);
        }
    }

    public static class LineResult {
        final String line;
        final int words, number;
        LineResult(String line, int words, int number) {
            this.line = line; this.words = words; this.number = number;
        }
        public String toString() {
            return number == 0 ? "Файл порожній" : "Рядок " + number + ", слів: " + words + "\n" + line;
        }
    }

    public static LineResult maxWords(Path path) throws IOException {
        String best = ""; int max = -1, number = 0, bestNumber = 0;
        try (BufferedReader in = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = in.readLine()) != null) {
                number++;
                String clean = line.trim();
                int count = clean.isEmpty() ? 0 : clean.split("\\s+").length;
                if (count > max) { best = line; max = count; bestNumber = number; }
            }
        }
        return new LineResult(best, Math.max(0, max), bestNumber);
    }

    public static void writeText(Path path, String text) throws IOException {
        Files.write(path, text.getBytes(StandardCharsets.UTF_8));
    }

    public static String readText(Path path) throws IOException {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }
}
