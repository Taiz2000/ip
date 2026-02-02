package sagiri.command;

import sagiri.exception.SagiriException;

public class Parser {

    /**
     * Parses the user input and returns a ParsedCommand.
     * Throws SagiriException for invalid inputs.
     */
    public static ParsedCommand parse(String input) throws SagiriException {
        if (input.equals("list")) {
            return new ParsedCommand(CommandType.LIST);
        } else if (input.startsWith("mark ")) {
            try {
                int taskNumber = Integer.parseInt(input.split(" ")[1]) - 1;
                return new ParsedCommand(CommandType.MARK, taskNumber);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                throw new SagiriException("Nope that's not a valid task number");
            }
        } else if (input.startsWith("unmark ")) {
            try {
                int taskNumber = Integer.parseInt(input.split(" ")[1]) - 1;
                return new ParsedCommand(CommandType.UNMARK, taskNumber);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                throw new SagiriException("Nope that's not a valid task number");
            }
        } else if (input.startsWith("delete ")) {
            try {
                int taskNumber = Integer.parseInt(input.split(" ")[1]) - 1;
                return new ParsedCommand(CommandType.DELETE, taskNumber);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                throw new SagiriException("Nope that's not a valid task number");
            }
        } else if (input.startsWith("check ")) {
            try {
                String dateStr = input.substring(6).trim();
                if (!isValidDateFormat(dateStr)) {
                    throw new SagiriException("Invalid date format. Please use dd-mm-yy format (e.g., 25-12-24)");
                }
                return new ParsedCommand(CommandType.CHECK, dateStr);
            } catch (StringIndexOutOfBoundsException e) {
                throw new SagiriException("Invalid check command. Please use: check dd-mm-yy");
            }
        } else if (input.startsWith("todo ")) {
            String taskName = input.substring(5).trim();
            return new ParsedCommand(CommandType.TODO, taskName);
        } else if (input.startsWith("event ")) {
            String desc = input.substring(6).trim();
            return new ParsedCommand(CommandType.EVENT, desc);
        } else if (input.startsWith("deadline ")) {
            String desc = input.substring(9).trim();
            return new ParsedCommand(CommandType.DEADLINE, desc);
        } else if (input.equals("bye")) {
            return new ParsedCommand(CommandType.BYE);
        } else {
            String msg = "No clue what that means :((\nYou can use todo, event, deadline, mark, unmark, delete, list, check, or bye";
            throw new SagiriException(msg);
        }
    }

    /**
     * Validates if a date string is in "dd-mm-yy" format.
     * @param dateStr the date string to validate
     * @return true if valid, false otherwise
     */
    private static boolean isValidDateFormat(String dateStr) {
        if (dateStr == null || dateStr.length() != 8) {
            return false;
        }
        try {
            String[] parts = dateStr.split("-");
            if (parts.length != 3) {
                return false;
            }
            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);

            // Basic validation
            if (day < 1 || day > 31 || month < 1 || month > 12 || year < 0 || year > 99) {
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Enum of command types.
     */
    public enum CommandType {
        LIST, MARK, UNMARK, DELETE, TODO, EVENT, DEADLINE, CHECK, BYE
    }

    public static class ParsedCommand {
        public CommandType type;
        public String data;
        public int taskIndex;

        public ParsedCommand(CommandType type) {
            this.type = type;
        }

        public ParsedCommand(CommandType type, String data) {
            this.type = type;
            this.data = data;
        }

        public ParsedCommand(CommandType type, int taskIndex) {
            this.type = type;
            this.taskIndex = taskIndex;
        }
    }
}
