import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class I18n {
    private ResourceBundle bundle;
    public I18n() { setLanguage("uk"); }
    public void setLanguage(String language) {
        bundle = ResourceBundle.getBundle("location.messages", new Locale(language));
    }
    public String text(String key, Object... args) {
        return MessageFormat.format(bundle.getString(key), args);
    }
}
