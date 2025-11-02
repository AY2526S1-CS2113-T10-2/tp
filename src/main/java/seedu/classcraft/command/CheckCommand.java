package seedu.classcraft.command;

import seedu.classcraft.storage.Storage;
import seedu.classcraft.studyplan.StudyPlan;
import seedu.classcraft.ui.Ui;

public class CheckCommand extends Command {
    public CheckCommand() {
        super();
    }

    @Override
    public void executeCommand(StudyPlan studyPlan, Ui ui, Storage storage) {
        ui.printMessage("Checking Study plan...");
        studyPlan.checkStudyPlan();
    }
}
