package seedu.classcraft.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import seedu.classcraft.studyplan.StudyPlan;
import seedu.classcraft.ui.Ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Fetches data from SpecData.json and prints data when a specialisation is queried.
 */
public class SpecCommand extends Command {
    private static final String SPECDATA = "src/main/java/seedu/classcraft/data/SpecData.json";
    public String specToQuery;
    private final ObjectMapper mapper = new ObjectMapper();

    public SpecCommand(String specName) {
        super();
        this.specToQuery = specName;
    }

    @Override
    public void executeCommand(StudyPlan studyPlan, Ui ui) throws IOException {
        try {
            File specFile = new File(SPECDATA);
            JsonNode specNode = mapper.readTree(specFile);

            System.out.println("These are all the mods that count towards your specialisation");
            printJsonArray(specNode.get(specToQuery).get("prereq"));

            System.out.println("And these are some common CEG mods that unlock the spec mods");
            Iterator<String> mods = specNode.get(specToQuery).fieldNames();
            while (mods.hasNext()) {
                String key = mods.next();
                if (!key.equals("prereq")) {
                    System.out.print(key + " unlocks ");
                    printJsonArray(specNode.get(specToQuery).get(key));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading specialisations file");
        }
    }

    private void printJsonArray(JsonNode jsonArray) {
        List<String> listOfMods = new ArrayList<>();
        for (JsonNode module : jsonArray) {
            listOfMods.add(module.asText());
        }
        System.out.println(String.join(", ", listOfMods));
    }
}
