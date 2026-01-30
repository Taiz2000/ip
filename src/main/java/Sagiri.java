import java.util.Scanner;
import java.util.ArrayList;

public class Sagiri {
    private static final String BAR = "____________________________________________________________";

    /**
     * Loads tasks from disk. Placeholder for future implementation.
     */
    private static void loadTasks(ArrayList<Task> tasks) {
        // nothing here yet
    }

    /**
     * Saves tasks to disk. Placeholder for future implementation.
     */
    private static void saveTasks(ArrayList<Task> tasks) {
        // nothing here yet
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
                String msg = "No clue what that means :((\nYou can use todo, event, deadline, mark, unmark, delete, list, or bye";
                throw new SagiriException(msg);
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Task> tasks = new ArrayList<>();

        loadTasks(tasks);
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
