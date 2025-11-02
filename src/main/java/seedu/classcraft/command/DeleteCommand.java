package seedu.classcraft.command;

import seedu.classcraft.exceptions.StudyPlanException;
import seedu.classcraft.storage.Storage;
import seedu.classcraft.studyplan.StudyPlan;
import seedu.classcraft.ui.Ui;

/**
 * DeleteCommand class representing a command to delete a module from the study plan.
 * Extends the Command abstract class.
 */
public class DeleteCommand extends Command {
    public String moduleToDelete;

    /**
     * DeleteCommand constructor to create an DeleteCommand object.
     *
     * @param moduleCode An string containing module code.
     */
    public DeleteCommand(String moduleCode) {
        super();
        this.moduleToDelete = moduleCode;
    }

    /**
     * Method from Command parent class.
     * Uses the studyPlan's removeModule method to remove a module from the study plan,
     * which takes in module code and storage handler
     *
     * @param studyPlan The current study plan ,including data restored from storage
     * @param ui        The user interface to interact with the user
     * @param storage   The storage handler to read/write data
     */
    @Override
    public void executeCommand(StudyPlan studyPlan, Ui ui, Storage storage) {
        try {
            studyPlan.removeModule(moduleToDelete, storage);
            ui.showMessage("Successfully deleted " + moduleToDelete + " from the study plan.");
        } catch (StudyPlanException e) {
            ui.printMessage("Error deleting module: " + e.getMessage());
        }
    }
}
