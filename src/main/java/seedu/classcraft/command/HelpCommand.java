package seedu.classcraft.command;

import seedu.classcraft.storage.Storage;
import seedu.classcraft.studyplan.StudyPlan;
import seedu.classcraft.ui.Ui;

/**
 * HelpCommand class representing the command to display help information.
 */
public class HelpCommand extends Command {

    /**
     * Method from Command parent class.
     * Prints a help message to the user, indicating available commands
     * and their required formats.
     *
     * @param studyPlan The current study plan ,including data restored from storage
     * @param ui        The user interface to interact with the user
     * @param storage   The storage handler to read/write data
     */
    @Override
    public void executeCommand(StudyPlan studyPlan, Ui ui, Storage storage) {
        String userHelp = "Hi there, do you require help?\n"
                + "Here are the list of commands you can use:\n\n"
                + "Basic Commands:\n\n"
                + "1. add - Adds a Module to your plan.\n " +
                " Format: add n/{MODULE_CODE} s/{SEMESTER} (SEMESTER: 1 to 8)\n\n"
                + "2. delete - Deletes a Module from your plan.\n   Format: delete {MODULE_CODE}\n\n"
                + "3. view - View Current Plan, Sample Plan or Graduation Requirements.\n   Format: view {INFORMATION} "
                + "(INFORMATION: plan,grad,sample)\n\n"
                + "4. exit - Exit the program\n\n"
                + "5. help - View this message again\n\n"
                + "6. prereq - View Pre-Requisites for the given module.\n   Format: prereq {MODULE_CODE}\n\n"
                + "Advanced Commands:\n\n"
                + "7. mc - Calculate MCs for a semester or total.\n " +
                "Format: mc {SEMESTER} (SEMESTER: 1 to 8 or total)\n\n"
                + "8. add-completed - Adds a module as already completed.\n   Format: add-completed {MODULE_CODE}\n\n"
                + "9. add-exempted - Adds a module as exempted.\n   Format: add-exempted {MODULE_CODE}\n\n"
                + "10. progress - View your current degree progress.\n   Format: progress\n\n"
                + "11. spec - View modules required for a CEG specialisation.\n   Format: spec {SPECIALISATION} "
                + "(SPECIALISATION: ae,4.0,iot,robotics,st)\n\n"
                + "12. check - Checks workload distribution of study plan.\n   Format: check\n"
                + "13. current_semester - Indicates the current semester, so that modules from previous semesters are" +
                " marked as completed.\n   Format: current_semester s/{SEMESTER} (SEMESTER: 1 to 8)\n";

        ui.printMessage(userHelp);
    }
}
