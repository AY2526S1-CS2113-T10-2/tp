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
     * @param ui The user interface to interact with the user
     * @param storage The storage handler to read/write data
     */
    @Override
    public void executeCommand(StudyPlan studyPlan, Ui ui, Storage storage) {
        String userHelp = "Hi there, do you require help?\n"
                + "Here are the list of commands you can use:\n"
                + "1. add - Adds a task. Format: add n/{MODULE_CODE} s/{SEMESTER} (SEMESTER: 1 to 8)\n"
                + "2. delete - Deletes a task. Format: delete {MODULE_CODE} \n"
                + "3. view - View all tasks. Format: view {INFORMATION} (INFORMATION: plan,grad,sample)\n"
                + "4. spec - View modules required for a specialisation. Format: spec {SPECIALISATION}\n"
                + "(SPECIALISATION: ae,4.0,iot,robotics,st)\n"
                + "5. exit - Exit the program\n"
                + "6. help - View this message again";
        ui.printMessage(userHelp);
    }
}
