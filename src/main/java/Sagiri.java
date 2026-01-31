import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

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
                    time = task.getStartDate() + " | " + task.getEndDate();
                } else if (task.getType() == TaskType.DEADLINE) {
                    time = task.getEndDate();
                }
                
                fw.write(type + " | " + marked + " | " + name + " | " + time + "\n");
            }
            fw.close();
        } catch (IOException e) {
            System.out.println("Error saving tasks: " + e.getMessage());
        }
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
