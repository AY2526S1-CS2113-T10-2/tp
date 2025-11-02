package seedu.classcraft.studyplan;

import com.fasterxml.jackson.databind.JsonNode;
import seedu.classcraft.exceptions.StudyPlanException;

import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Validates module prerequisites by evaluating the prereqTree structure
 * Handles complex AND/OR logic properly
 */
public class PrerequisiteChecker {

    private static final Logger logger = Logger.getLogger(PrerequisiteChecker.class.getName());

    /**
     * Validates if prerequisites are satisfied before adding module.
     *
     * @param module         The module to be added.
     * @param targetSemester The semester number (1-based index) to which the module is being added.
     * @param studyPlan      The current study plan instance.
     */
    public static void validatePrerequisites(Module module, int targetSemester, StudyPlan studyPlan)
            throws StudyPlanException {
        assert module != null : "Module cannot be null";
        assert studyPlan != null : "StudyPlan cannot be null";
        assert targetSemester > 0 : "Target semester must be positive";
        setLoggerLevel(logger);

        logger.log(Level.INFO, "Validating prerequisites for module {0} in semester {1}",
                new Object[]{module.getModCode(), targetSemester});

        JsonNode prereqTree = module.getPrereqTree();

        if (prereqTree == null || prereqTree.isNull() || prereqTree.isMissingNode()) {
            logger.log(Level.FINE, "Module {0} has no prerequisites", module.getModCode());
            return;
        }

        Set<String> completedModules = getCompletedModules(targetSemester, studyPlan);
        logger.log(Level.FINE, "Completed modules before semester {0}: {1}",
                new Object[]{targetSemester, completedModules});

        boolean satisfied = evaluatePrereqTree(prereqTree, completedModules);

        if (!satisfied) {
            String errorMessage = buildErrorMessage(module.getModCode(), targetSemester, prereqTree, completedModules);
            logger.log(Level.WARNING, "Prerequisites not satisfied for module {0}: {1}",
                    new Object[]{module.getModCode(), errorMessage});
            throw new StudyPlanException(errorMessage);
        }

        logger.log(Level.INFO, "Prerequisites satisfied for module {0}", module.getModCode());
    }

    /**
     * Recursively evaluates the prerequisite tree
     * Returns true if prerequisites are satisfied
     */
    private static boolean evaluatePrereqTree(JsonNode node, Set<String> completedModules) {
        assert node != null : "JsonNode cannot be null";
        assert completedModules != null : "Completed modules set cannot be null";

        if (node == null || node.isNull() || node.isMissingNode()) {
            return true;
        }

        if (node.isObject() && node.has("or")) {
            boolean result = evaluateOrNode(node.get("or"), completedModules);
            logger.log(Level.FINER, "OR node evaluation result: {0}", result);
            return result;
        }

        if (node.isObject() && node.has("and")) {
            boolean result = evaluateAndNode(node.get("and"), completedModules);
            logger.log(Level.FINER, "AND node evaluation result: {0}", result);
            return result;
        }

        if (node.isTextual()) {
            return isModuleCompleted(node.asText(), completedModules);
        }

        if (node.isObject() && node.has("moduleCode")) {
            return isModuleCompleted(node.get("moduleCode").asText(), completedModules);
        }

        return true;
    }

    private static boolean evaluateOrNode(JsonNode orNode, Set<String> completedModules) {
        assert orNode != null : "OR node cannot be null";
        assert completedModules != null : "Completed modules set cannot be null";

        if (!orNode.isArray()) {
            return false;
        }

        for (JsonNode child : orNode) {
            if (evaluatePrereqTree(child, completedModules)) {
                logger.log(Level.FINEST, "OR condition satisfied by child node");
                return true;
            }
        }
        logger.log(Level.FINEST, "OR condition not satisfied");
        return false;
    }

    private static boolean evaluateAndNode(JsonNode andNode, Set<String> completedModules) {
        assert andNode != null : "AND node cannot be null";
        assert completedModules != null : "Completed modules set cannot be null";

        if (!andNode.isArray()) {
            return true;
        }

        for (JsonNode child : andNode) {
            if (!evaluatePrereqTree(child, completedModules)) {
                logger.log(Level.FINEST, "AND condition failed on child node");
                return false;
            }
        }
        logger.log(Level.FINEST, "AND condition satisfied");
        return true;
    }

    private static boolean isModuleCompleted(String moduleText, Set<String> completedModules) {
        assert moduleText != null : "Module text cannot be null";
        assert completedModules != null : "Completed modules set cannot be null";

        String moduleCode = stripGradeRequirement(moduleText);

        if (isBridgingModule(moduleCode)) {
            logger.log(Level.FINEST, "Ignoring bridging module: {0}", moduleCode);
            return true;
        }

        if (!isValidModuleCode(moduleCode)) {
            logger.log(Level.WARNING, "Invalid module code format: {0}", moduleCode);
            return true;
        }

        boolean completed = completedModules.contains(moduleCode);
        logger.log(Level.FINEST, "Module {0} completion status: {1}", new Object[]{moduleCode, completed});
        return completed;
    }

    private static boolean isValidModuleCode(String moduleCode) {
        return moduleCode.matches("^[A-Z]{2,3}\\d{4}[A-Z]?$");
    }

    /**
     * Gets all completed modules from previous semesters
     */
    private static Set<String> getCompletedModules(int targetSemester, StudyPlan studyPlan) {
        assert studyPlan != null : "StudyPlan cannot be null";
        assert targetSemester > 0 : "Target semester must be positive";

        Set<String> completedModules = new HashSet<>();
        ArrayList<ArrayList<Module>> plan = studyPlan.getStudyPlan();

        for (int sem = 0; sem < targetSemester - 1 && sem < plan.size(); sem++) {
            for (Module mod : plan.get(sem)) {
                completedModules.add(mod.getModCode());
            }
        }

        completedModules.addAll(studyPlan.getCompletedModulesMap().keySet());

        return completedModules;
    }

    /**
     * Strips grade requirements from module codes (e.g., "CS1010:B" -> "CS1010")
     */
    private static String stripGradeRequirement(String moduleCode) {
        int colonIndex = moduleCode.indexOf(':');
        if (colonIndex != -1) {
            return moduleCode.substring(0, colonIndex);
        }

        return moduleCode;
    }

    /**
     * Checks if module is a bridging course that should be ignored
     */
    private static boolean isBridgingModule(String moduleCode) {
        return moduleCode.equals("MA1301")
                || moduleCode.equals("MA1301X")
                || moduleCode.equals("MA1301FC")
                || moduleCode.equals("PC1201");
    }

    /**
     * Builds user-friendly error message
     */
    private static String buildErrorMessage(String moduleCode, int targetSemester, JsonNode prereqTree,
                                            Set<String> completedModules) {
        assert moduleCode != null : "Module code cannot be null";
        assert prereqTree != null : "Prerequisite tree cannot be null";

        StringBuilder message = new StringBuilder();
        message.append("Cannot add ").append(moduleCode)
                .append(" to semester ").append(targetSemester)
                .append(".\n\n");

        message.append("Required prerequisites: ")
                .append(prettifyPrereqTree(prereqTree))
                .append("\n\n");

        if (completedModules.isEmpty()) {
            message.append("You have not completed any prerequisite modules in earlier semesters.\n");
        } else {
            message.append("Completed modules: ")
                    .append(String.join(", ", completedModules))
                    .append("\n");
        }

        message.append("\nPlease add the required prerequisite modules to an earlier semester first.");

        return message.toString();
    }

    /**
     * Converts prereqTree to human-readable format
     */
    private static String prettifyPrereqTree(JsonNode node) {
        if (node == null || node.isNull()) {
            return "";
        }

        if (node.has("or")) {
            return prettifyLogicNode(node.get("or"), " OR ");
        }

        if (node.has("and")) {
            return prettifyLogicNode(node.get("and"), " AND ");
        }

        if (node.isTextual()) {
            return prettifyModuleCode(node.asText());
        }

        if (node.has("moduleCode")) {
            return prettifyModuleCode(node.get("moduleCode").asText());
        }

        return "";
    }

    private static String prettifyLogicNode(JsonNode logicNode, String separator) {
        if (!logicNode.isArray()) {
            return "";
        }

        List<String> parts = new ArrayList<>();
        for (JsonNode child : logicNode) {
            String part = prettifyPrereqTree(child);
            if (!part.isEmpty()) {
                parts.add(part);
            }
        }

        return parts.isEmpty() ? "" : "(" + String.join(separator, parts) + ")";
    }

    private static String prettifyModuleCode(String moduleText) {
        String code = stripGradeRequirement(moduleText);

        if (!isBridgingModule(code) && isValidModuleCode(code)) {
            return code;
        }
        return "";
    }

    /**
     * Sets logger level depending on how the program is run.
     * When running from a jar file, it disables logging.
     * Otherwise, when running from an IDE, it displays all logging messages.
     */
    public static void setLoggerLevel(Logger logger) {
        String className = "/" + logger.getClass().getName().replace('.', '/') + ".class";
        URL resource = logger.getClass().getResource(className);

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
