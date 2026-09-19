package workshop.task_2_1;
import java.util.LinkedHashSet;
import java.util.Set;

public class Developer {
    private final String name;
    private final Set<String> languages = new LinkedHashSet<>();

    public Developer(String name) { this.name = name; }
    public String getName() { return name; }
    public void add(String language) { languages.add(language); }
    public Set<String> getLanguages() { return languages; }
}
