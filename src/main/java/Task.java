public class Task {
    private String name;
    private boolean isDone;
    private TaskType type;
    private String startDate;
    private String endDate;
    
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
        this(name, TaskType.EVENT, startDate, endDate);
    }

    /**
     * Constructor for Task (Deadline).
     * @param name
     * @param endDate
     */
    public Task(String name, String endDate) {
        this(name, TaskType.DEADLINE, null, endDate);
    }

    /**
     * Private constructor for Task.
     * @param name
     * @param type
     * @param startDate
     * @param endDate
     */
    private Task(String name, TaskType type, String startDate, String endDate) {
        this.name = name;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isDone = false;
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
            result += " (from: " + startDate + " to: " + endDate + ")";
        } else if (type == TaskType.DEADLINE) {
            result += " (by: " + endDate + ")";
        }
        return result;
    }
}