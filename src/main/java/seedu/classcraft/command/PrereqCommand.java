package seedu.classcraft.command;

import com.fasterxml.jackson.databind.JsonNode;
import seedu.classcraft.nusmodsfetcher.NUSmodsFetcher;
import seedu.classcraft.studyplan.StudyPlan;
import seedu.classcraft.ui.Ui;
import seedu.classcraft.storage.Storage;

import java.util.logging.Logger;
import java.util.logging.Level;

public class PrereqCommand extends Command {

    private static final Logger logger = Logger.getLogger(PrereqCommand.class.getName());

    private String moduleCode;

    public PrereqCommand(String moduleCode) {
        super();
        this.moduleCode = moduleCode;
        logger.log(Level.FINE, "PrereqCommand created for module: {0}", moduleCode);
    }

    @Override
    public void executeCommand(StudyPlan studyPlan, Ui ui, Storage storage) {
        assert studyPlan != null : "StudyPlan cannot be null";
        assert ui != null : "Ui cannot be null";

        logger.log(Level.INFO, "Executing prereq command for module: {0}", moduleCode);

        try {
            String moduleTitle = NUSmodsFetcher.getModuleTitle(moduleCode);
            logger.log(Level.FINE, "Module title fetched: {0}", moduleTitle);

            JsonNode rootJson = NUSmodsFetcher.fetchModuleJson(moduleCode);
            JsonNode prereqTree = rootJson.get("prereqTree");

            if (prereqTree == null || prereqTree.isNull()) {
                logger.log(Level.INFO, "No prerequisites found for module: {0}", moduleCode);
            }

            ui.displayPrerequisites(moduleCode, moduleTitle, prereqTree);
            logger.log(Level.INFO, "Prerequisites displayed successfully for: {0}", moduleCode);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching prerequisites for module: " + moduleCode, e);
            ui.displayPrereqError(moduleCode);
        }
    }
}
