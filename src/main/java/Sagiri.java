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
    private static final String BAR = "____________________________________________________________";

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
     * Shows the initial greeting message.
     */
    private static void showGreeting() {
        System.out.println(BAR);
        System.out.println("Hello! I'm Sagiri");
        System.out.println("What can I do for you?");
        System.out.println(BAR);
    }

    /**
     * Lists all tasks in the task list.
     */
    private static void listTasks(ArrayList<Task> tasks) {
        System.out.println(BAR);
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i).toString());
        }
        System.out.println(BAR);
    }

    /**
     * Marks a task as done.
     */
    private static void markTaskDone(ArrayList<Task> tasks, int taskIndex) throws SagiriException {
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new SagiriException("Can't find " + (taskIndex + 1));
        }
        tasks.get(taskIndex).markAsDone();
        System.out.println(BAR);
        System.out.println("Nice! I've marked this task as done:");
        System.out.println(tasks.get(taskIndex).toString());
        System.out.println(BAR);
    }

    /**
     * Marks a task as not done.
     */
    private static void markTaskNotDone(ArrayList<Task> tasks, int taskIndex) throws SagiriException {
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new SagiriException("Can't find " + (taskIndex + 1));
        }
        tasks.get(taskIndex).markAsNotDone();
        System.out.println(BAR);
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println(tasks.get(taskIndex).toString());
        System.out.println(BAR);
    }

    /**
     * Deletes a task from the list.
     */
    private static void deleteTask(ArrayList<Task> tasks, int taskIndex) throws SagiriException {
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new SagiriException("Can't find " + (taskIndex + 1));
        }
        Task removedTask = tasks.remove(taskIndex);
        System.out.println(BAR);
        System.out.println("Noted. I've removed this task:");
        System.out.println(removedTask.toString());
        System.out.println(BAR);
    }

    /**
     * Adds a todo task.
     */
    private static void addTodo(ArrayList<Task> tasks, String taskName) throws SagiriException {
        if (taskName.isEmpty()) {
            throw new SagiriException("Oops! You have to provide a task name");
        }
        tasks.add(new Task(taskName));
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + tasks.get(tasks.size() - 1).toString());
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
        System.out.println(BAR);
    }

    /**
     * Adds an event task.
     */
    private static void addEvent(ArrayList<Task> tasks, String desc) throws SagiriException {
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
        
        tasks.add(new Task(taskName, start, end));
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + tasks.get(tasks.size() - 1).toString());
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
        System.out.println(BAR);
    }

    /**
     * Adds a deadline task.
     */
    private static void addDeadline(ArrayList<Task> tasks, String desc) throws SagiriException {
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
        
        tasks.add(new Task(taskName, end));
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + tasks.get(tasks.size() - 1).toString());
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
        System.out.println(BAR);
    }

    /**
     * Processes a user command and performs the appropriate action.
     */
    private static void processCommand(ArrayList<Task> tasks, String input) throws SagiriException {
        if (input.equals("list")) {
            listTasks(tasks);
        } else if (input.startsWith("mark")) {
            try {
                int taskNumber = Integer.parseInt(input.split(" ")[1]) - 1;
                markTaskDone(tasks, taskNumber);
                saveTasks(tasks);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                throw new SagiriException("Nope that's not a valid task number");
            }
        } else if (input.startsWith("unmark")) {
            try {
                int taskNumber = Integer.parseInt(input.split(" ")[1]) - 1;
                markTaskNotDone(tasks, taskNumber);
                saveTasks(tasks);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                throw new SagiriException("Nope that's not a valid task number");
            }
        } else if (input.startsWith("delete")) {
            try {
                int taskNumber = Integer.parseInt(input.split(" ")[1]) - 1;
                deleteTask(tasks, taskNumber);
                saveTasks(tasks);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                throw new SagiriException("Nope that's not a valid task number");
            }
        } else if (input.startsWith("check")) {
            try {
                String dateStr = input.substring(6).trim();
                if (!isValidDateFormat(dateStr)) {
                    throw new SagiriException("Invalid date format. Please use dd-mm-yy format (e.g., 25-12-24)");
                }
                checkTasksForDate(tasks, dateStr);
            } catch (StringIndexOutOfBoundsException e) {
                throw new SagiriException("Invalid check command. Please use: check dd-mm-yy");
            }
        } else {
            System.out.println(BAR);
            if (input.startsWith("todo")) {
                String taskName = input.substring(5).trim();
                addTodo(tasks, taskName);
                saveTasks(tasks);
            } else if (input.startsWith("event")) {
                String desc = input.substring(6).trim();
                addEvent(tasks, desc);
                saveTasks(tasks);
            } else if (input.startsWith("deadline")) {
                String desc = input.substring(9).trim();
                addDeadline(tasks, desc);
                saveTasks(tasks);
            } else {
                String msg = "No clue what that means :((\nYou can use todo, event, deadline, mark, unmark, delete, list, check, or bye";
                throw new SagiriException(msg);
            }
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
     * Checks and displays tasks (deadlines and events) for a specific date.
     * @param tasks the list of tasks to check
     * @param dateStr the date string in "dd-mm-yy" format
     */
    private static void checkTasksForDate(ArrayList<Task> tasks, String dateStr) {
        // Parse the target date
        LocalDateTime targetDate = parseDateForComparison(dateStr);
        
        System.out.println(BAR);
        System.out.println("Tasks for " + formatDateForDisplay(targetDate) + ":");
        
        boolean foundTasks = false;
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            boolean matches = false;
            
            if (task.getType() == TaskType.EVENT) {
                // Check if event starts or ends on the target date
                LocalDateTime startDate = task.getStartDateTime();
                LocalDateTime endDate = task.getEndDateTime();
                if ((startDate != null && datesEqual(startDate, targetDate)) ||
                    (endDate != null && datesEqual(endDate, targetDate))) {
                    matches = true;
                }
            } else if (task.getType() == TaskType.DEADLINE) {
                // Check if deadline is on the target date
                LocalDateTime deadlineDate = task.getEndDateTime();
                if (deadlineDate != null && datesEqual(deadlineDate, targetDate)) {
                    matches = true;
                }
            }
            
            if (matches) {
                System.out.println((i + 1) + "." + task.toString());
                foundTasks = true;
            }
        }
        
        if (!foundTasks) {
            System.out.println("No tasks found for this date.");
        }
        System.out.println(BAR);
    }
    
    /**
     * Parses a date string for comparison purposes.
     * @param dateStr the date string in "dd-mm-yy" format
     * @return LocalDateTime object for comparison
     */
    private static LocalDateTime parseDateForComparison(String dateStr) {
        return parseDate(dateStr);
    }
    
    /**
     * Formats a LocalDateTime for display in the check command.
     * @param dateTime the LocalDateTime to format
     * @return formatted date string
     */
    private static String formatDateForDisplay(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "Unknown";
        }
        return dateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }
    
    /**
     * Compares two LocalDateTime objects for date equality (ignores time).
     * @param date1 first date
     * @param date2 second date
     * @return true if dates are equal, false otherwise
     */
    private static boolean datesEqual(LocalDateTime date1, LocalDateTime date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return date1.toLocalDate().equals(date2.toLocalDate());
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
            System.out.println(BAR);
            System.out.println("Error loading saved tasks: " + e.getMessage());
            System.out.println("Starting with empty task list.");
            System.out.println(BAR);
        }
        showGreeting();

        String input = scanner.nextLine();

        while (!input.equals("bye")) {
            try {
                processCommand(tasks, input);
            } catch (SagiriException e) {
                System.out.println(BAR);
                System.out.println(e.getMessage());
                System.out.println(BAR);
            }
            input = scanner.nextLine();
        }

        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(BAR);
        scanner.close();
    }
}
