import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Sagiri {

    /**
     * Loads tasks from disk. Reads from ./data/Sagiri.dat in format: type | marked | name | time
     * Throws SagiriException if the file format is corrupted.
     */
    private static void loadTasks(ArrayList<Task> tasks) throws SagiriException {
        try {
            File file = new File("./data/Sagiri.dat");
            if (!file.exists()) {
                return; // No file to load, start with empty list
            }
            
            List<String> lines = Files.readAllLines(Paths.get("./data/Sagiri.dat"));
            for (int lineNum = 1; lineNum <= lines.size(); lineNum++) {
                String line = lines.get(lineNum - 1);
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(" \\| ");
                if (parts.length != 4) {
                    throw new SagiriException("Corrupted data file at line " + lineNum + ": Expected 4 parts separated by ' | ', found " + parts.length);
                }
                
                String type = parts[0].trim();
                String marked = parts[1].trim();
                String name = parts[2].trim();
                String time = parts[3].trim();
                
                // Validate type
                if (!type.equals("T") && !type.equals("E") && !type.equals("D")) {
                    throw new SagiriException("Corrupted data file at line " + lineNum + ": Invalid task type '" + type + "'. Expected T, E, or D");
                }
                
                // Validate marked status
                if (!marked.equals("0") && !marked.equals("1")) {
                    throw new SagiriException("Corrupted data file at line " + lineNum + ": Invalid marked status '" + marked + "'. Expected 0 or 1");
                }
                
                // Validate name
                if (name.isEmpty()) {
                    throw new SagiriException("Corrupted data file at line " + lineNum + ": Task name cannot be empty");
                }
                
                Task task = null;
                if (type.equals("T")) {
                    // Todo tasks should have empty time field
                    if (!time.isEmpty()) {
                        throw new SagiriException("Corrupted data file at line " + lineNum + ": Todo task should have empty time field, found '" + time + "'");
                    }
                    task = new Task(name);
                } else if (type.equals("E")) {
                    // Event tasks should have "start | end" format
                    String[] timeParts = time.split(" \\| ");
                    if (timeParts.length != 2) {
                        throw new SagiriException("Corrupted data file at line " + lineNum + ": Event task time should be in 'start | end' format, found '" + time + "'");
                    }
                    String start = timeParts[0].trim();
                    String end = timeParts[1].trim();
                    if (start.isEmpty() || end.isEmpty()) {
                        throw new SagiriException("Corrupted data file at line " + lineNum + ": Event start and end times cannot be empty");
                    }
                    task = new Task(name, start, end);
                } else if (type.equals("D")) {
                    // Deadline tasks should have single time value
                    if (time.isEmpty()) {
                        throw new SagiriException("Corrupted data file at line " + lineNum + ": Deadline task must have a deadline time");
                    }
                    task = new Task(name, time);
                }
                
                if (task != null) {
                    if (marked.equals("1")) {
                        task.markAsDone();
                    }
                    tasks.add(task);
                }
            }
        } catch (IOException e) {
            throw new SagiriException("Error reading data file: " + e.getMessage());
        }
    }

    /**
     * Saves tasks to disk. Saves to ./data/Sagiri.dat in format: type | marked | name | time
     */
    private static void saveTasks(ArrayList<Task> tasks) {
        try {
            // Create data directory if it doesn't exist
            Files.createDirectories(Paths.get("./data"));
            
            FileWriter fw = new FileWriter("./data/Sagiri.dat");
            for (Task task : tasks) {
                String type = task.getTypeIcon();
                String marked = task.isDone() ? "1" : "0";
                String name = task.getName();
                
                String time = "";
                if (task.getType() == TaskType.EVENT) {
                    String start = formatDateForStorage(task.getStartDateTime());
                    String end = formatDateForStorage(task.getEndDateTime());
                    time = start + " | " + end;
                } else if (task.getType() == TaskType.DEADLINE) {
                    time = formatDateForStorage(task.getEndDateTime());
                }
                
                fw.write(type + " | " + marked + " | " + name + " | " + time + "\n");
            }
            fw.close();
        } catch (IOException e) {
            System.out.println("Error saving tasks: " + e.getMessage());
        }
    }

    /**
     * Formats a LocalDateTime to "dd-mm-yy" format for storage.
     * @param dateTime the LocalDateTime to format
     * @return formatted date string or empty string if dateTime is null
     */
    private static String formatDateForStorage(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DateTimeFormatter.ofPattern("dd-MM-yy"));
    }

    /**
     * Marks a task as done.
     */
    private static void markTaskDone(ArrayList<Task> tasks, int taskIndex) throws SagiriException {
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new SagiriException("Can't find " + (taskIndex + 1));
        }
        tasks.get(taskIndex).markAsDone();
    }

    /**
     * Marks a task as not done.
     */
    private static void markTaskNotDone(ArrayList<Task> tasks, int taskIndex) throws SagiriException {
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new SagiriException("Can't find " + (taskIndex + 1));
        }
        tasks.get(taskIndex).markAsNotDone();
    }

    /**
     * Deletes a task from the list.
     */
    private static Task deleteTask(ArrayList<Task> tasks, int taskIndex) throws SagiriException {
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new SagiriException("Can't find " + (taskIndex + 1));
        }
        return tasks.remove(taskIndex);
    }

    /**
     * Adds a todo task.
     */
    private static Task addTodo(ArrayList<Task> tasks, String taskName) throws SagiriException {
        if (taskName.isEmpty()) {
            throw new SagiriException("Oops! You have to provide a task name");
        }
        Task task = new Task(taskName);
        tasks.add(task);
        return task;
    }

    /**
     * Adds an event task.
     */
    private static Task addEvent(ArrayList<Task> tasks, String desc) throws SagiriException {
        int fromIndex = desc.indexOf(" /from ");
        int toIndex = desc.indexOf(" /to ");
        if (fromIndex == -1 || toIndex == -1 || fromIndex >= toIndex) {
            throw new SagiriException("Invalid event format. You can use: event <name> /from <start> /to <end>");
        }
        // extract name, start and end date/time
        String taskName = desc.substring(0, fromIndex).trim();
        String start = desc.substring(fromIndex + 7, toIndex).trim();
        String end = desc.substring(toIndex + 5).trim();
        if (taskName.isEmpty() || start.isEmpty() || end.isEmpty()) {
            throw new SagiriException("Event name, start time, and end time cannot be empty.");
        }
        
        // Validate date formats
        if (!isValidDateFormat(start)) {
            throw new SagiriException("Invalid start date format. Please use dd-mm-yy format (e.g., 25-12-24)");
        }
        if (!isValidDateFormat(end)) {
            throw new SagiriException("Invalid end date format. Please use dd-mm-yy format (e.g., 25-12-24)");
        }
        
        Task task = new Task(taskName, start, end);
        tasks.add(task);
        return task;
    }

    /**
     * Adds a deadline task.
     */
    private static Task addDeadline(ArrayList<Task> tasks, String desc) throws SagiriException {
        int byIndex = desc.indexOf(" /by ");
        if (byIndex == -1) {
            throw new SagiriException("Invalid deadline format. You can use: deadline <name> /by <date>");
        }
        // extract name and end date/time
        String taskName = desc.substring(0, byIndex).trim();
        String end = desc.substring(byIndex + 5).trim();
        if (taskName.isEmpty() || end.isEmpty()) {
            throw new SagiriException("Deadline name and date cannot be empty.");
        }
        
        // Validate date format
        if (!isValidDateFormat(end)) {
            throw new SagiriException("Invalid deadline date format. Please use dd-mm-yy format (e.g., 25-12-24)");
        }
        
        Task task = new Task(taskName, end);
        tasks.add(task);
        return task;
    }

    /**
     * Processes a user command and performs the appropriate action.
     */
    private static void processCommand(ArrayList<Task> tasks, Parser.ParsedCommand command) throws SagiriException {
        switch (command.type) {
        case LIST:
            Ui.printTasks(tasks);
            break;
        case MARK:
            markTaskDone(tasks, command.taskIndex);
            Ui.printMarkedDone(tasks.get(command.taskIndex));
            saveTasks(tasks);
            break;
        case UNMARK:
            markTaskNotDone(tasks, command.taskIndex);
            Ui.printMarkedNotDone(tasks.get(command.taskIndex));
            saveTasks(tasks);
            break;
        case DELETE:
            Task removed = deleteTask(tasks, command.taskIndex);
            Ui.printDeleted(removed);
            saveTasks(tasks);
            break;
        case TODO:
            Task addedTodo = addTodo(tasks, command.data);
            Ui.printAdded(addedTodo, tasks.size());
            saveTasks(tasks);
            break;
        case EVENT:
            Task addedEvent = addEvent(tasks, command.data);
            Ui.printAdded(addedEvent, tasks.size());
            saveTasks(tasks);
            break;
        case DEADLINE:
            Task addedDeadline = addDeadline(tasks, command.data);
            Ui.printAdded(addedDeadline, tasks.size());
            saveTasks(tasks);
            break;
        case CHECK:
            LocalDateTime targetDate = parseDate(command.data);
            Ui.printCheckedTasks(tasks, command.data, targetDate);
            break;
        case BYE:
            // Handled in main
            break;
        }
    }

    /**
     * Parses a date string in "dd-mm-yy" format to LocalDateTime.
     * @param dateStr the date string to parse
     * @return LocalDateTime object, or null if parsing fails
     */
    private static LocalDateTime parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            // Parse dd-mm-yy format
            String[] parts = dateStr.split("-");
            if (parts.length != 3) {
                return null;
            }
            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]) + 2000; // yy -> 20yy
            
            return LocalDateTime.of(year, month, day, 0, 0);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return null;
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

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Task> tasks = new ArrayList<>();

        try {
            loadTasks(tasks);
        } catch (SagiriException e) {
            Ui.printLoadingError(e.getMessage());
        }
        Ui.printGreeting();

        String input = scanner.nextLine();

        while (!input.equals("bye")) {
            try {
                Parser.ParsedCommand command = Parser.parse(input);
                if (command.type == Parser.CommandType.BYE) {
                    break;
                }
                processCommand(tasks, command);
            } catch (SagiriException e) {
                Ui.printError(e.getMessage());
            }
            input = scanner.nextLine();
        }

        Ui.printBye();
        scanner.close();
    }
}
