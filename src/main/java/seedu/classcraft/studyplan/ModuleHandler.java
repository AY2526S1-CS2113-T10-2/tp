package seedu.classcraft.studyplan;

import java.net.URL; 
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects; 
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;


import seedu.classcraft.exceptions.NUSmodsFetcherException;
import seedu.classcraft.nusmodsfetcher.NUSmodsFetcher;

import java.util.Iterator;
import java.util.Map;

/**
 * Handles the storage and creation of Module classes
 * Stores a HashMap with modCode: Module as key value pair
 */
public class ModuleHandler {
    private static final Logger LOGGER = Logger.getLogger(ModuleHandler.class.getName());
    private HashMap<String, Module> modules;


    public ModuleHandler() {
        setLoggerLevel();
        this.modules = new HashMap<>();
    }

    /**
     * Creates a Module object by fetching details from NUSMods API.
     *
     * @param moduleCode The module code of the module to be created.
     * @return The created Module object.
     */
    public Module createModule(String moduleCode) throws Exception {
        String modName = "placeholder";
        int modCreds = 0;
        String modDescription = "placeholder";
        JsonNode prereqTreeNode = null;

        try {
            modName = NUSmodsFetcher.getModuleTitle(moduleCode);
            modCreds = NUSmodsFetcher.getModuleCredits(moduleCode);
            modDescription = NUSmodsFetcher.getModuleDescription(moduleCode);
        } catch (NUSmodsFetcherException e) {
            LOGGER.warning("Could not fetch details for " + moduleCode + ". Error: " + e.getMessage());
            throw new Exception("Module code " + moduleCode + " is invalid or could not be fetched from NUSMods.");
        }

        List<String> prerequisites = new ArrayList<>();

        try {
            JsonNode rootJson = NUSmodsFetcher.fetchModuleJson(moduleCode);

            JsonNode prerequisiteNode = rootJson.get("prereqTree");

            if (prerequisiteNode != null && !prerequisiteNode.isNull()) {
                prereqTreeNode = prerequisiteNode;
                extractModuleCodes(prerequisiteNode, prerequisites);

                prerequisites = prerequisites.stream()
                        .map(code -> stripGradeRequirement(code))
                        .filter(code -> isValidModuleCode(code))
                        .filter(code -> !isBridgingModule(code))
                        .distinct()
                        .collect(Collectors.toList());

                LOGGER.info("Module " + moduleCode + " has " + prerequisites.size()
                        + " prerequisites: " + prerequisites);
            }

        } catch (Exception e) {
            LOGGER.warning("Could not fetch details for " + moduleCode
                    + ". Using default values. Error: " + e.getMessage());
        }

        Module newModule = new Module(modName, moduleCode, modCreds, modDescription, prerequisites, -1, -1);

        newModule.setPrereqTree(prereqTreeNode);
        addModule(newModule);

        assert modules.containsKey(moduleCode) : "New module must be added to modules map.";
        return newModule;
    }

    public HashMap<String, Module> getModules() {
        return modules;
    }

    /**
     * Adds a module to the modules map if it does not already exist.
     *
     * @param module The module to be added.
     */
    public void addModule(Module module) {
        modules.putIfAbsent(module.getModCode(), module);
    }

    private boolean isValidModuleCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return false;
        }
        return code.matches("^[A-Z]{2,3}\\d{4}[A-Z]?$");
    }

    private void extractModuleCodes(JsonNode node, List<String> result) {
        if (node == null || node.isNull()) {
            return;
        }

        if (node.isObject() && node.has("moduleCode")) {
            String modCode = node.get("moduleCode").asText().trim();
            if (!modCode.isEmpty()) {
                modCode = stripGradeRequirement(modCode);
                result.add(modCode);
            }
            return;
        }

        if (node.isArray()) {
            for (JsonNode subNode : node) {
                extractModuleCodes(subNode, result);
            }
            return;
        }

        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String fieldName = field.getKey();
                if (fieldName.equals("and") || fieldName.equals("or")) {
                    extractModuleCodes(field.getValue(), result);
                }
            }
            return;
        }

        if (node.isTextual()) {
            String text = node.asText().trim();

            text = stripGradeRequirement(text);

            if (text.matches("^[A-Z]{2,3}\\d{4}[A-Z]?$")) {
                result.add(text);
            }
        }
    }

    private String stripGradeRequirement(String moduleCode) {
        int colonIndex = moduleCode.indexOf(':');
        if (colonIndex != -1) {
            return moduleCode.substring(0, colonIndex);
        }
        return moduleCode;
    }

    private boolean isBridgingModule(String moduleCode) {

        return moduleCode.equals("MA1301")
                || moduleCode.equals("MA1301X")
                || moduleCode.equals("MA1301FC")
                || moduleCode.equals("PC1201");
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

        if (Objects.equals(protocol, "jar")) {
            LOGGER.setLevel(Level.OFF);
        } else if (Objects.equals(protocol, "file")) {
            LOGGER.setLevel(Level.ALL);
        }
    }
}