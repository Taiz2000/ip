package sagiri.task;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    private String name;
    private boolean isDone;
    private TaskType type;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    
    /**
     * Constructor for Task (ToDo).
     * @param name
     */
    public Task(String name) {
        this(name, TaskType.TODO, null, null);
    }

    /**
     * Constructor for Task (Event).
     * @param name
     * @param startDate
     * @param endDate
     */
    public Task(String name, String startDate, String endDate) {
        this(name, TaskType.EVENT, parseDate(startDate), parseDate(endDate));
    }

    /**
     * Constructor for Task (Deadline).
     * @param name
     * @param endDate
     */
    public Task(String name, String endDate) {
        this(name, TaskType.DEADLINE, null, parseDate(endDate));
    }

    /**
     * Private constructor for Task.
     * @param name
     * @param type
     * @param startDate
     * @param endDate
     */
    private Task(String name, TaskType type, LocalDateTime startDate, LocalDateTime endDate) {
        this.name = name;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isDone = false;
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
     * Gets the type icon.
     * @return "T", "E", or "D"
     */
    public String getTypeIcon() {
        switch (type) {
        case TODO:
            return "T";
        case EVENT:
            return "E";
        case DEADLINE:
            return "D";
        default:
            return " ";
        }
    }

    /**
     * Marks the task as done.
     * @return "X" or space depending on status
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /**
     * Gets the name of the task.
     * @return name of task
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets the start date of the task.
     * @return start date or null
     */
    public String getStartDate() {
        return formatDate(startDate);
    }

    /**
     * Gets the end date of the task.
     * @return end date or null
     */
    public String getEndDate() {
        return formatDate(endDate);
    }

    /**
     * Gets the start date as LocalDateTime.
     * @return start date or null
     */
    public LocalDateTime getStartDateTime() {
        return startDate;
    }

    /**
     * Gets the end date as LocalDateTime.
     * @return end date or null
     */
    public LocalDateTime getEndDateTime() {
        return endDate;
    }

    /**
     * Formats a LocalDateTime to "dd MMM yyyy" format.
     * @param dateTime the LocalDateTime to format
     * @return formatted date string or null if dateTime is null
     */
    private static String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }

    /**
     * Checks if the task is done.
     * @return true if done, false otherwise
     */
    public boolean isDone() {
        return this.isDone;
    }

    /**
     * Gets the task type.
     * @return the task type
     */
    public TaskType getType() {
        return this.type;
    }

    /**
     * Marks the task as done.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks the task as not done.
     */
    public void markAsNotDone() {
        this.isDone = false;
    }

    @Override
    public String toString() {
        String result = "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + getName();
        if (type == TaskType.EVENT) {
            result += " (from: " + getStartDate() + " to: " + getEndDate() + ")";
        } else if (type == TaskType.DEADLINE) {
            result += " (by: " + getEndDate() + ")";
        }
        return result;
    }
}