public class Task {
    private String name;
    private boolean isDone;
    
    /**
     * Constructor for Task.
     * @param name
     */
    public Task(String name) {
        this.name = name;
        this.isDone = false;
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
        return "[" + getStatusIcon() + "] " + getName();
    }
}