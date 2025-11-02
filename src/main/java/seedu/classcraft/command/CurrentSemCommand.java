package seedu.classcraft.command;


import seedu.classcraft.storage.Storage;
import seedu.classcraft.studyplan.StudyPlan;
import seedu.classcraft.ui.Ui;

public class CurrentSemCommand extends Command {
    private String currentSem;

    public CurrentSemCommand(String currentSem) {
        super();
        this.currentSem = currentSem;
    }

    @Override
    public void executeCommand(StudyPlan studyPlan, Ui ui, Storage storage) {
        try {
            int totalSemesters = StudyPlan.getTotalSemesters();
            int currentSemester = Integer.parseInt(currentSem);

            if (currentSemester < 1 || currentSemester > totalSemesters) {
                throw new IllegalArgumentException();
            }

            StudyPlan.setCurrentSemester(Integer.parseInt(currentSem));

            ui.showMessage("Successfully set current semester to " + currentSem);
        } catch (IllegalArgumentException e) {
            // Handle invalid semester errors
            ui.printMessage("Invalid semester");
        } catch (Exception e) {
            // Handle other errors (API failures, etc.)
            ui.printMessage("Error adding module: " + e.getMessage());
        }
    }
}
