package seedu.classcraft.parser;

import seedu.classcraft.command.Command;
import seedu.classcraft.command.AddCommand;
import seedu.classcraft.command.DeleteCommand;
import seedu.classcraft.command.CommandList;
import seedu.classcraft.command.ExitCommand;
import seedu.classcraft.command.HelpCommand;
import seedu.classcraft.command.InvalidCommand;
import seedu.classcraft.command.SpecCommand;
import seedu.classcraft.command.ViewSamplePlanCommand;
import seedu.classcraft.command.ViewGradReqCommand;
import seedu.classcraft.command.ViewCurrentPlanCommand;
import seedu.classcraft.studyplan.StudyPlan;

public class Parser {
    public String commandType;
    public String userInputString;
    public String userInstructions;
    public StudyPlan studyPlan;

    public Parser(String userInput) {
        this.userInputString = userInput;
        // this.studyPlan = studyPlan;
        parseInstructions();
    }

    public Command parseInput() {
        switch (commandType) {
        case "help":
            return new HelpCommand();
        case "add":
            String[] addModuleInfo = parseAdd();
            // return new InvalidCommand();
            return new AddCommand(addModuleInfo);
        case "delete":
            String deleteModuleCode = parseDelete();
            // return new InvalidCommand();
            return new DeleteCommand(deleteModuleCode);
        case "confirm":
            return new InvalidCommand();
        //return new ConfirmCommand();
        case "view":
            String viewItems = parseView();
            switch (viewItems) {
            case "sample":
                return new ViewSamplePlanCommand();
            case "grad":
                return new ViewGradReqCommand();
            case "plan":
                return new ViewCurrentPlanCommand();
            // return new InvalidCommand();
            default:
                return new InvalidCommand();
            }
        case "exit":
            return new ExitCommand();
        case "spec":
            String specItems = parseSpec();
            return new SpecCommand(specItems);
        default:
            return new InvalidCommand();
        }
    }

    //parses the user input into command type and instructions
    //returns invalid command if command type is not found
    public void parseInstructions() {
        String[] instructions = userInputString.split(" ", 2);

        if (!CommandList.isFound(instructions[0])) {
            this.commandType = "invalid";
            return;
        }

        try {
            if (instructions.length == 1) {
                if (!(instructions[0].equals("help") || instructions[0].equals("exit")
                        || instructions[0].equals("confirm"))) {
                    throw new IllegalArgumentException("OOPS!!! The description of a " +
                            instructions[0] + " cannot be empty.");
                }
                this.commandType = instructions[0];
            } else if (instructions.length == 2) {
                if (instructions[1].isEmpty()) {
                    throw new IllegalArgumentException("OOPS!!! The description of a " +
                            instructions[0] + " cannot be empty.");
                }
                this.commandType = instructions[0];
                this.userInstructions = instructions[1];
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            this.commandType = "invalid";
        }
    }

    //parses the user input for add command
    //returns an array of module code and semester
    public String[] parseAdd() {
        String[] addModuleInformation = new String[2];
        try {
            String[] addInstructions = userInstructions.split("s/", 2);
            String moduleCode = addInstructions[0].split("n/", 2)[1].trim();
            String semester = addInstructions[1].trim();
            addModuleInformation[0] = moduleCode;
            addModuleInformation[1] = semester;
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            System.out.println("Error: Invalid input format. Please enter input in " +
                    "the correct format (e.g.add n/CG2111A s/2). ");
            return new String[0];
        }
        return addModuleInformation;

    }

    //parses the user input for delete command
    //returns the module code to be deleted
    public String parseDelete() {
        String deleteModuleInformation;
        try {
            String moduleCode = userInstructions.split(" ", 2)[0].trim();
            deleteModuleInformation = moduleCode;
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            System.out.println("Error: Invalid input format. Please enter " +
                    "input in the correct format (e.g.delete CG2111A).");
            return "";
        }
        return deleteModuleInformation;
    }

    //parses the user input for view command
    //returns the information to be viewed (plan, grad or sample)
    public String parseView() {
        String viewItemsInformation;
        try {
            String viewInstructions = userInstructions.split(" ", 2)[0].trim().toLowerCase();

            if (viewInstructions.equals("plan")) {
                viewItemsInformation = "plan";
            } else if (viewInstructions.equals("grad")) {
                viewItemsInformation = "grad";
            } else if (viewInstructions.equals("sample")) {
                viewItemsInformation = "sample";
            } else {
                throw new IllegalArgumentException("OOPS!!! The description of a view " +
                        "command must be either plan, grad or sample.");
            }
        } catch (ArrayIndexOutOfBoundsException | NullPointerException | IllegalArgumentException e) {
            System.out.println("Error: Invalid input format. Please enter input in the correct format. ");
            return "";
        }
        return viewItemsInformation;
    }

    public String parseSpec() {
        String specItemsInformation;
        try {
            String specInstructions = userInstructions.split(" ", 2)[0].trim().toLowerCase();

            if (specInstructions.equals("ae")) {
                specItemsInformation = "ae";
            } else if (specInstructions.equals("4.0")) {
                specItemsInformation = "4.0";
            } else if (specInstructions.equals("iot")) {
                specItemsInformation = "iot";
            } else if (specInstructions.equals("robotics")) {
                specItemsInformation = "robotics";
            } else if (specInstructions.equals("st")) {
                specItemsInformation = "st";
            } else {
                throw new IllegalArgumentException("OOPS!!! The specialisation must be either " +
                        "ae, 4.0, iot, robotics or st.");
            }
        } catch (ArrayIndexOutOfBoundsException | NullPointerException | IllegalArgumentException e) {
            System.out.println("Error: Invalid input format. Please enter input in the correct format. ");
            return "";
        }
        return specItemsInformation;
    }
}
