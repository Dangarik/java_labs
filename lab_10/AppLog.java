import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.*;

public class AppLog {
    public static final Logger LOG = Logger.getLogger("Lab10");
    public static void setup(Path file) throws IOException {
        Path parent = file.toAbsolutePath().getParent();
        Files.createDirectories(parent);
        LOG.setUseParentHandlers(false);
        LOG.setLevel(Level.FINE);
        Formatter format = new Formatter() {
            public String format(LogRecord record) {
                String error = record.getThrown() == null ? "" :
                    " | " + record.getThrown().toString();
                return record.getLevel() + " " + formatMessage(record) + error + "\n";
            }
        };
        ConsoleHandler console = new ConsoleHandler();
        console.setLevel(Level.INFO);
        console.setEncoding("UTF-8");
        console.setFormatter(format);
        FileHandler output = new FileHandler(file.toString(), true);
        output.setEncoding("UTF-8");
        output.setLevel(Level.FINE);
        output.setFormatter(format);
        LOG.addHandler(console);
        LOG.addHandler(output);
    }
    public static void demo(I18n ui) {
        System.out.println(ui.text("levels.first"));
        LOG.setLevel(Level.FINE);
        LOG.fine("D1_FINE: file only");
        LOG.info("D1_INFO: console and file");
        LOG.warning("D1_WARNING: console and file");
        System.out.println(ui.text("levels.second"));
        LOG.setLevel(Level.WARNING);
        LOG.info("D2_INFO: rejected by logger");
        LOG.warning("D2_WARNING: console and file");
        LOG.setLevel(Level.FINE);
        System.out.println(ui.text("levels.finish"));
    }
    public static void close() {
        for (Handler handler : LOG.getHandlers()) {
            handler.close();
            LOG.removeHandler(handler);
        }
    }
}
