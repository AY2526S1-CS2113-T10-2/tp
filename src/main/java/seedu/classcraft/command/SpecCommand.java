package seedu.classcraft.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import seedu.classcraft.storage.Storage;
import seedu.classcraft.studyplan.StudyPlan;
import seedu.classcraft.ui.Ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Fetches data from SpecData.json and prints data when a specialisation is queried.
 */
public class SpecCommand extends Command {
    private static final String SPEC_DATA =
        "{\"ae\": {\"prereq\": [\"EE3408C\",\"EE3431C\",\"EE4218\",\"EE4407\",\"EE4415\",\"EE5507\","
                + "\"CG3207\",\"EE4409\",\"EE4435\",\"EE4436\",\"EE4437\",\"EE4438\"],\"EE2026\": [\"EE4415\"],"
                + "\"CG2027\": [\"EE3408C\",\"EE4407\",\"EE4409\",\"EE4435\",\"EE4436\",\"EE4437\",\"EE4438\"],"
                + "\"CG2028\": [\"EE4218\",\"CG3207\"],\"EE3408C\": [\"EE5507\"]},\"4.0\": {\"prereq\": [\"EE3331C\","
                + "\"ME2142\",\"EE3306\",\"EE4211\",\"EE4212\",\"EE4302\",\"EE4307\",\"EE4311\",\"EE4312\","
                + "\"EE4314\",\"EE4315\",\"ME3242\",\"ME4262\",\"ME4248\",\"ME4246\",\"ME5405\",\"CN4227R\","
                + "\"CN4221R\",\"RB4301\"],\"CG2023\": [\"EE3331C\"],\"EE3331C\": [\"EE4302\",\"EE4307\",\"ME5405\"],"
                + "\"EE2211\": [\"EE4211\"],\"EE2012/ST2334\": [\"EE4211\"]},\"iot\": {\"prereq\": [\"CS3237\","
                + "\"EE4211\",\"EE4409\",\"CG4002\",\"CS4222\",\"EE4204\",\"EE4216\",\"EE4218\",\"CS3244\","
                + "\"EE4002D\",\"EE4002R\",\"CP4106\"],\"CG2028\": [\"CS3237\",\"EE4218\"],\"CG2027\": [\"EE4409\"],"
                + "\"EE2211\": [\"EE4211\"],\"EE2012/ST2334\": [\"EE4211\",\"CS4222\",\"EE4204\",\"CS3244\"],"
                + "\"CS2113\": [\"CG4002\"],\"EE4204\": [\"CS4222\"],\"EE2026\": [\"EE4216\"]},\"robotics\": {"
                + "\"prereq\": [\"BN4203\",\"BN4601\",\"EE3305\",\"ME3243\",\"EE4305\",\"EE4308\",\"EE4309\","
                + "\"EE4705\",\"EE4311\",\"EE4312\",\"EE4314\",\"ME4242\",\"ME4245\",\"ME5406\",\"MLE4228\","
                + "\"RB4301\"],\"EE3331C\": [\"EE4308\",\"EE3331C\",\"ME4245\"],\"EE2211\": [\"EE4314\",\"EE4705\"]},"
                + "\"st\": {\"prereq\": [\"EE3105\",\"EE4002D\",\"EE4002R\",\"EE3131C\",\"EE3104C\",\"EE3331C\","
                + "\"EE4115\",\"EE4218\",\"EE4314\",\"EE4503\",\"EE4101\"],\"CG2023\": [\"EE3131C\",\"EE3331C\","
                + "\"EE4115\"],\"PC2020\": [\"EE3104C\",\"EE4101\"],\"EE2211\": [\"EE4115\",\"EE4314\"],"
                + "\"CG2028\": [\"EE4218\"]}}";

    public String specToQuery;
    private final ObjectMapper mapper = new ObjectMapper();

    public SpecCommand(String specName) {
        super();
        this.specToQuery = specName;
        assert specName != null;
        List<String> allowedValues = Arrays.asList("ae", "4.0", "iot", "robotics", "st");
        assert allowedValues.contains(specToQuery);
    }

    @Override
    public void executeCommand(StudyPlan studyPlan, Ui ui, Storage storage) throws IOException {
        try {
            JsonNode specNode = mapper.readTree(SPEC_DATA);

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
