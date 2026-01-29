import java.util.Scanner;
import java.util.ArrayList;

public class Sagiri {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Task> tasks = new ArrayList<>();

        String name = "Sagiri";
        String bar = "____________________________________________________________";
        System.out.println(bar);
        System.out.println("Hello! I'm " + name);
        System.out.println("What can I do for you?");
        System.out.println(bar);

        String input = scanner.nextLine();

        while (!input.equals("bye")) {
            try {
                if (input.equals("list")) {
                    System.out.println(bar);
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println((i+1) + "." + tasks.get(i).toString());
                    }
                    System.out.println(bar);
                } else if (input.startsWith("mark")) {
                    try {
                        int taskNumber = Integer.parseInt(input.split(" ")[1]) - 1;
                        if (taskNumber < 0 || taskNumber >= tasks.size()) {
                            throw new SagiriException("Can't find " + (taskNumber + 1));
                        }
                        tasks.get(taskNumber).markAsDone();
                        System.out.println(bar);
                        System.out.println("Nice! I've marked this task as done:");
                        System.out.println(tasks.get(taskNumber).toString());
                        System.out.println(bar);
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                        throw new SagiriException("Nope that's not a valid task number");
                    }
                } else if (input.startsWith("unmark")) {
                    try {
                        int taskNumber = Integer.parseInt(input.split(" ")[1]) - 1;
                        if (taskNumber < 0 || taskNumber >= tasks.size()) {
                            throw new SagiriException("Can't find " + (taskNumber + 1));
                        }
                        tasks.get(taskNumber).markAsNotDone();
                        System.out.println(bar);
                        System.out.println("OK, I've marked this task as not done yet:");
                        System.out.println(tasks.get(taskNumber).toString());
                        System.out.println(bar);
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                        throw new SagiriException("Nope that's not a valid task number");
                    }
                } else {
                    System.out.println(bar);
                    if (input.startsWith("todo")) {
                        String taskName = input.substring(5).trim();
                        if (taskName.isEmpty()) {
                            throw new SagiriException("Oops! You have to provide a task name");
                        }
                        tasks.add(new Task(taskName));
                        System.out.println("Got it. I've added this task:");
                        System.out.println("  " + tasks.get(tasks.size() - 1).toString());
                    } else if (input.startsWith("event")) {
                        String desc = input.substring(6).trim();
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
                    } else if (input.startsWith("deadline")) {
                        String desc = input.substring(9).trim();
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
                    } else {
                        String msg = "No clue what that means :((\nYou can use todo, event, deadline, mark, unmark, list, or bye";
                        throw new SagiriException(msg);
                    }
                    System.out.println("Now you have " + tasks.size() + " tasks in the list.");
                    System.out.println(bar);
                }
            } catch (SagiriException e) {
                System.out.println(bar);
                System.out.println(e.getMessage());
                System.out.println(bar);
            }
            input = scanner.nextLine();
        }

        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(bar);
        scanner.close();
    }
}
