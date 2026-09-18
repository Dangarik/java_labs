import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class StreamCipher {
    private static class CharWriter extends Writer {
        private final DataOutputStream out;
        CharWriter(OutputStream out) { this.out = new DataOutputStream(out); }
        public void write(char[] c, int offset, int length) throws IOException {
            for (int i = offset; i < offset + length; i++) out.writeChar(c[i]);
        }
        public void flush() throws IOException { out.flush(); }
        public void close() throws IOException { out.close(); }
    }
    private static class CharReader extends Reader {
        private final DataInputStream in;
        CharReader(InputStream in) { this.in = new DataInputStream(in); }
        public int read(char[] c, int offset, int length) throws IOException {
            if (length == 0) return 0;
            int count = 0;
            while (count < length) {
                int high = in.read();
                if (high == -1) break;
                int low = in.read();
                if (low == -1) throw new IOException("Пошкоджено зашифрований файл");
                c[offset + count++] = (char) ((high << 8) | low);
            }
            return count == 0 ? -1 : count;
        }
        public void close() throws IOException { in.close(); }
    }
    public static class EncryptWriter extends FilterWriter {
        private final char key;
        public EncryptWriter(Writer out, char key) { super(out); this.key = key; }
        public void write(int c) throws IOException { out.write((c + key) & 0xffff); }
        public void write(char[] c, int offset, int length) throws IOException {
            for (int i = offset; i < offset + length; i++) write(c[i]);
        }
        public void write(String s, int offset, int length) throws IOException {
            for (int i = offset; i < offset + length; i++) write(s.charAt(i));
        }
    }
    public static class DecryptReader extends FilterReader {
        private final char key;
        public DecryptReader(Reader in, char key) { super(in); this.key = key; }
        public int read() throws IOException {
            int c = in.read();
            return c == -1 ? -1 : (c - key) & 0xffff;
        }
        public int read(char[] c, int offset, int length) throws IOException {
            int count = in.read(c, offset, length);
            for (int i = 0; i < count; i++) c[offset + i] = (char) ((c[offset + i] - key) & 0xffff);
            return count;
        }
    }
    private static void transfer(Reader in, Writer out) throws IOException {
        char[] buffer = new char[1024]; int count;
        while ((count = in.read(buffer)) != -1) out.write(buffer, 0, count);
    }
    private static void different(Path input, Path output) throws IOException {
        if (input.toAbsolutePath().normalize().equals(output.toAbsolutePath().normalize())
                || (Files.exists(output) && Files.isSameFile(input, output)))
            throw new IOException("Вхідний і вихідний файли мають бути різними");
    }
    public static void encrypt(Path input, Path output, char key) throws IOException {
        different(input, output);
        try (Reader in = Files.newBufferedReader(input, StandardCharsets.UTF_8);
             Writer out = new EncryptWriter(new CharWriter(Files.newOutputStream(output)), key)) {
            transfer(in, out);
        }
    }
    public static void decrypt(Path input, Path output, char key) throws IOException {
        different(input, output);
        try (Reader in = new DecryptReader(new CharReader(Files.newInputStream(input)), key);
             Writer out = Files.newBufferedWriter(output, StandardCharsets.UTF_8)) {
            transfer(in, out);
        }
    }
}
