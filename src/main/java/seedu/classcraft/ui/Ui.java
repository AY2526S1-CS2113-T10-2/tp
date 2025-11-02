package seedu.classcraft.ui;

import com.fasterxml.jackson.databind.JsonNode;
import seedu.classcraft.studyplan.StudyPlan;
import seedu.classcraft.studyplan.Module;

import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.logging.Level;

public class Ui {
    private static final Logger logger = Logger.getLogger(Ui.class.getName());
    private String line = "_____________________________________________________" + System.lineSeparator();

    public Ui() {
        setLoggerLevel();
    }

    /**
     * Prints a generic message to the console, framed by lines for clarity.
     *
     * @param message The message to be printed.
     */
    public void printMessage(String message) {
        assert message != null : "Message cannot be null";
        logger.log(Level.FINE, "Printing message: {0}", message);

        System.out.print(line);
        System.out.println(message);
        System.out.print(line);
    }

    /**
     * Prints the contents of a study plan, showing modules by semester.
     * Now also displays Completed/Exempted modules first.
     *
     * @param plan  The study plan data (either current or sample).
     * @param title The title to display (e.g., "CEG Sample Study Plan").
     */
    private void displayStudyPlan(StudyPlan plan, String title, boolean sample) {
        System.out.print(line);
        System.out.println(title);
        System.out.print(line);

        ArrayList<Module> completedMods = plan.getCompletedModulesList();
        if (completedMods != null && !completedMods.isEmpty()) {
            System.out.println("Completed / Exempted Modules:");
            for (Module mod : completedMods) {
                System.out.println("  - " + mod.getModCode() + " (" + mod.getModName() + ") - "
                        + mod.getStatus().toString());
            }
            System.out.print(line);
        }

        int current_semester = StudyPlan.getCurrentSemester();

        ArrayList<ArrayList<Module>> planData = plan.getStudyPlan();

        for (int i = 0; i < planData.size(); i++) {
            if (!sample && (i + 1 < current_semester)) {
                System.out.println("Semester " + (i + 1) + " (Completed):");
            } else {
                System.out.println("Semester " + (i + 1) + ":");
            }
            ArrayList<Module> semesterMods = planData.get(i);

            if (semesterMods.isEmpty()) {
                System.out.println("  (Empty)");
                continue;
            }

            for (Module mod : semesterMods) {
                String prereqsInfo = mod.getPrerequisitesDisplay();
                System.out.println("  - " + mod.getModCode() + " (" + mod.getModName() + ")" + prereqsInfo);
            }
        }
        System.out.print(line);
    }

    public void displaySamplePlan(StudyPlan samplePlan) {
        displayStudyPlan(samplePlan, "CEG Sample Study Plan", true);
    }

    public void displayCurrentPlan(StudyPlan currentPlan) {
        displayStudyPlan(currentPlan, "Current Study Plan", false);
    }

    // @@author ashpasa

    /**
     * Displays the total module credits for a given semester or overall.
     *
     * @param semesterIndex The index of the semester in the ArrayList, with -1 representing overall total.
     * @param totalCredits  The number of module credits for the corresponding semester, or overall.
     */
    public void displayTotalCredits(int semesterIndex, int totalCredits) {
        String semesterString = "";
        try {
            if (semesterIndex == -1) {
                semesterString = "Total";
            } else {
                semesterString = "Semester " + Integer.toString(semesterIndex + 1);
            }
        } catch (Exception e) {
            semesterString = "Invalid Semester";
        }

        System.out.print(line);
        System.out.println(semesterString + " Module Credits: " + Integer.toString(totalCredits));
        System.out.print(line);
    }
    // @@author

    /**
     * Displays an error message to the user.
     *
     * @param errorMessage The error message to be displayed.
     */
    public void showError(String errorMessage) {
        assert errorMessage != null : "Error message cannot be null";
        logger.log(Level.WARNING, "Displaying error: {0}", errorMessage);

        System.out.println("============================================================");
        System.out.println("ERROR: " + errorMessage);
        System.out.println("============================================================");
    }

    /**
     * Displays a general message to the user.
     *
     * @param message The message to be displayed.
     */
    public void showMessage(String message) {
        System.out.println("============================================================");
        System.out.println(message);
        System.out.println("============================================================");
    }

    /**
     * Displays the prerequisites for a given module.
     *
     * @param moduleCode  The module code.
     * @param moduleTitle The module title.
     * @param prereqTree  The prerequisite tree in JSON format.
     */
    public void displayPrerequisites(String moduleCode, String moduleTitle, JsonNode prereqTree) {
        assert moduleCode != null : "Module code cannot be null";
        logger.log(Level.INFO, "Displaying prerequisites for: {0}", moduleCode);

        System.out.print(line);
        System.out.println("Module: " + moduleCode + " - " + moduleTitle);
        System.out.print(line);

        if (prereqTree == null || prereqTree.isNull() || prereqTree.isMissingNode()) {
            System.out.println("Prerequisites: None");
            System.out.println();
            System.out.println("This module has no prerequisites. You can take it in any semester!");
            logger.log(Level.FINE, "No prerequisites for module: {0}", moduleCode);
        } else {
            String prereqString = prettifyPrereqTree(prereqTree);
            System.out.println("Prerequisites: " + prereqString);
            System.out.println();
            System.out.println("Note: You need to satisfy these prerequisites before taking this module.");
        }

        System.out.print(line);
    }

    /**
     * Displays an error message for prerequisite lookup.
     *
     * @param moduleCode The module code.
     */
    public void displayPrereqError(String moduleCode) {
        assert moduleCode != null : "Module code cannot be null";
        logger.log(Level.WARNING, "Could not fetch prerequisites for module: {0}", moduleCode);

        System.out.print(line);
        System.out.println("Error: Could not fetch prerequisites for " + moduleCode);
        System.out.println("Please check that the module code is valid.");
        System.out.print(line);
    }

    /**
     * Converts prereqTree JSON to human-readable format
     */
    private String prettifyPrereqTree(JsonNode node) {
        if (node == null || node.isNull()) {
            return "None";
        }

        if (node.has("or")) {
            return prettifyArrayNode(node.get("or"), "OR");
        }

        if (node.has("and")) {
            return prettifyArrayNode(node.get("and"), "AND");
        }

        if (node.isTextual()) {
            return prettifyModuleNode(node.asText());
        }

        if (node.has("moduleCode")) {
            return prettifyModuleNode(node.get("moduleCode").asText());
        }

        return "";
    }

    private String prettifyArrayNode(JsonNode arrayNode, String joinWord) {
        if (arrayNode == null || !arrayNode.isArray()) {
            return "";
        }
        StringBuilder result = new StringBuilder("(");
        boolean first = true;
        for (JsonNode child : arrayNode) {
            String childStr = prettifyPrereqTree(child);
            if (childStr.isEmpty() || isBridgingModule(childStr)) {
                continue;
            }
            if (!first) {
                result.append(" ").append(joinWord).append(" ");
            }
            result.append(childStr);
            first = false;
        }
        result.append(")");
        return result.toString();
    }

    private String prettifyModuleNode(String codeRaw) {
        String code = stripGradeRequirement(codeRaw);
        if (isValidModuleCode(code) && !isBridgingModule(code)) {
            return code;
        }
        return "";
    }

    private String stripGradeRequirement(String moduleCode) {
        int colonIndex = moduleCode.indexOf(':');
        if (colonIndex != -1) {
            return moduleCode.substring(0, colonIndex);
        }
        return moduleCode;
    }

    private boolean isValidModuleCode(String code) {
        return code != null && code.matches("^[A-Z]{2,3}\\d{4}[A-Z]?$");
    }

    private boolean isBridgingModule(String code) {
        return code.equals("MA1301") || code.equals("MA1301X")
                || code.equals("MA1301FC") || code.equals("PC1201");
    }

    /**
     * Sets logger level depending on how the program is run.
     * When running from a jar file, it disables logging.
     * Otherwise, when running from an IDE, it displays all logging messages.
     */
    public void setLoggerLevel() {
        String className = "/" + this.getClass().getName().replace('.', '/') + ".class";
        URL resource = this.getClass().getResource(className);

        if (resource == null) {
            return;
        }

        String protocol = resource.getProtocol();
        if (Objects.equals(protocol, "jrt")) {
            logger.setLevel(Level.OFF);
        } else if (Objects.equals(protocol, "file")) {
            logger.setLevel(Level.ALL);
        }
    }
}