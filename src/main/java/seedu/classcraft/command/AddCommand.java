package seedu.classcraft.command;

import seedu.classcraft.storage.Storage;
import seedu.classcraft.studyplan.StudyPlan;
import seedu.classcraft.ui.Ui;
import seedu.classcraft.exceptions.StudyPlanException;

/**
 * AddCommand class representing the command to add a module to the study plan.
 * Extends the Command abstract class and implements the executeCommand method.
 */
public class AddCommand extends Command {

    public String[] moduleAddInfo;

    /**
     * AddCommand constructor to create an AddCommand object.
     *
     * @param moduleAddInfo An array containing module code and semester information.
     */
    public AddCommand(String[] moduleAddInfo) {
        super();
        this.moduleAddInfo = moduleAddInfo;
    }

    /**
     * Method from Command parent class.
     * Uses the studyPlan's addModule method to add a module to the study plan,
     * which takes in module code, semester information from moduleAddInfo, storage handler
     * and a boolean indicating if it's from a saved file.
     * Error is thrown if any exception occurs during the addition of the module.
     *
     * @param studyPlan The current study plan ,including data restored from storage
     * @param ui        The user interface to interact with the user
     * @param storage   The storage handler to read/write data
     */
    @Override
    public void executeCommand(StudyPlan studyPlan, Ui ui, Storage storage) {
        try {

            studyPlan.addModule(moduleAddInfo[0], Integer.parseInt(moduleAddInfo[1]), storage, false);

            ui.showMessage("Successfully added " + moduleAddInfo[0]
                    + " to semester " + moduleAddInfo[1]);
        } catch (StudyPlanException e) {
            // Handle prerequisite validation errors
            ui.printMessage("Prerequisite Error:\n" + e.getMessage());
        } catch (IllegalArgumentException e) {
            // Handle invalid semester errors
            ui.printMessage("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            // Handle other errors (API failures, etc.)
            ui.printMessage("Error adding module: " + e.getMessage());
        }
    }
}
