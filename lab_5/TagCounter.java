import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.*;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.*;
import javax.swing.text.html.parser.ParserDelegator;

public class TagCounter {
    public static Map<String, Integer> count(String html) throws IOException {
        Map<String, Integer> counts = new TreeMap<>();
        HTMLEditorKit.ParserCallback callback = new HTMLEditorKit.ParserCallback() {
            private void add(HTML.Tag tag, MutableAttributeSet attributes, int pos) {
                if (attributes.isDefined(HTML.Attribute.ENDTAG)) return;
                String name = tag.toString().toLowerCase(Locale.ROOT);
                if (pos < 0 || pos >= html.length()) return;
                String tail = html.substring(pos);
                if (Pattern.compile("^<\\s*" + Pattern.quote(name) + "(?=\\s|/?>)",
                        Pattern.CASE_INSENSITIVE).matcher(tail).find())
                    counts.merge(name, 1, Integer::sum);
            }
            public void handleStartTag(HTML.Tag tag, MutableAttributeSet a, int pos) { add(tag, a, pos); }
            public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet a, int pos) { add(tag, a, pos); }
        };
        new ParserDelegator().parse(new StringReader(html), callback, true);
        return counts;
    }
    public static Map<String, Integer> fromUrl(String address) throws IOException {
        URL url = new URL(address);
        if (!url.getProtocol().equals("https") && !url.getProtocol().equals("http"))
            throw new IOException("Введіть HTTP або HTTPS URL");
        URLConnection connection = url.openConnection();
        connection.setConnectTimeout(8000); connection.setReadTimeout(8000);
        connection.setRequestProperty("User-Agent", "JavaLab5/1.0");
        Charset charset = StandardCharsets.UTF_8;
        String contentType = connection.getContentType();
        if (contentType != null) {
            Matcher m = Pattern.compile("charset=\\s*[\"']?([^;\\s\"']+)", Pattern.CASE_INSENSITIVE).matcher(contentType);
            if (m.find()) charset = Charset.forName(m.group(1));
        }
        StringBuilder html = new StringBuilder();
        try (Reader in = new InputStreamReader(connection.getInputStream(), charset)) {
            char[] buffer = new char[4096]; int n;
            while ((n = in.read(buffer)) != -1) html.append(buffer, 0, n);
        }
        return count(html.toString());
    }
    public static void show(Map<String, Integer> counts) {
        System.out.println("За назвою тегу:");
        new TreeMap<>(counts).forEach((tag, n) -> System.out.println(tag + " — " + n));
        System.out.println("За частотою (при рівності — за назвою):");
        ArrayList<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());
        entries.sort(Comparator.comparingInt((Map.Entry<String, Integer> e) -> e.getValue())
                .thenComparing(Map.Entry::getKey));
        for (Map.Entry<String, Integer> e : entries) System.out.println(e.getKey() + " — " + e.getValue());
    }
    public static void main(String[] args) throws IOException {
        String address = args.length == 0 ? "https://example.com" : args[0];
        try { System.out.println("URL: " + address); show(fromUrl(address)); }
        catch (IOException | IllegalArgumentException e) { System.out.println("Помилка: " + e.getMessage()); }
    }
}
