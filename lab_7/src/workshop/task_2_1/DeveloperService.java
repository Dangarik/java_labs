package workshop.task_2_1;

import java.util.List;
import java.util.stream.Collectors;

public class DeveloperService {

    public static List<String> getLanguages(List<Developer> team) {
        return team.stream()
                .flatMap(developer -> developer.getLanguages().stream())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }


}
