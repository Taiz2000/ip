import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TaskList {
    private ArrayList<Task> tasks;

    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Adds a todo task.
     */
    public Task addTodo(String taskName) throws SagiriException {
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
    public Task addEvent(String desc) throws SagiriException {
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
    public Task addDeadline(String desc) throws SagiriException {
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
     * Marks a task as done.
     */
    public void markTaskDone(int taskIndex) throws SagiriException {
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new SagiriException("Can't find " + (taskIndex + 1));
        }
        tasks.get(taskIndex).markAsDone();
    }

    /**
     * Marks a task as not done.
     */
    public void markTaskNotDone(int taskIndex) throws SagiriException {
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new SagiriException("Can't find " + (taskIndex + 1));
        }
        tasks.get(taskIndex).markAsNotDone();
    }

    /**
     * Deletes a task from the list.
     */
    public Task deleteTask(int taskIndex) throws SagiriException {
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new SagiriException("Can't find " + (taskIndex + 1));
        }
        return tasks.remove(taskIndex);
    }

    /**
     * Gets the list of tasks.
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }

    /**
     * Gets the number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Parses a date string in "dd-mm-yy" format to LocalDateTime.
     * @param dateStr the date string to parse
     * @return LocalDateTime object, or null if parsing fails
     */
    public static LocalDateTime parseDate(String dateStr) {
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
    public static boolean isValidDateFormat(String dateStr) {
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
}