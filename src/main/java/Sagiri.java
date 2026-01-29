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
            if (input.equals("list")) {
                System.out.println(bar);
                for (int i = 0; i < tasks.size(); i++) {
                    System.out.println((i+1) + "." + tasks.get(i).toString());
                }
                System.out.println(bar);
            } else if (input.startsWith("mark")) {
                int taskNumber = Integer.parseInt(input.split(" ")[1]) - 1;
                tasks.get(taskNumber).markAsDone();
                System.out.println(bar);
                System.out.println("Nice! I've marked this task as done:");
                System.out.println(tasks.get(taskNumber).toString());
                System.out.println(bar);
            } else if (input.startsWith("unmark")) {
                int taskNumber = Integer.parseInt(input.split(" ")[1]) - 1;
                tasks.get(taskNumber).markAsNotDone();
                System.out.println(bar);
                System.out.println("OK, I've marked this task as not done yet:");
                System.out.println(tasks.get(taskNumber).toString());
                System.out.println(bar);
            } else {
                System.out.println(bar);
                if (input.startsWith("todo")) {
                    String taskName = input.substring(5).trim();
                    tasks.add(new Task(taskName));
                    System.out.println("Got it. I've added this task:");
                    System.out.println("  " + tasks.get(tasks.size() - 1).toString());
                } else if (input.startsWith("event")) {
                    String desc = input.substring(6).trim();
                    int fromIndex = desc.indexOf(" /from ");
                    int toIndex = desc.indexOf(" /to ");
                    // extract name, start and end date/time
                    String taskName = desc.substring(0, fromIndex).trim();
                    String start = desc.substring(fromIndex + 7, toIndex).trim();
                    String end = desc.substring(toIndex + 5).trim();
                    tasks.add(new Task(taskName, start, end));
                    System.out.println("Got it. I've added this task:");
                    System.out.println("  " + tasks.get(tasks.size() - 1).toString());
                } else if (input.startsWith("deadline")) {
                    String desc = input.substring(9).trim();
                    int byIndex = desc.indexOf(" /by ");
                    // extract name and end date/time
                    String taskName = desc.substring(0, byIndex).trim();
                    String end = desc.substring(byIndex + 5).trim();
                    tasks.add(new Task(taskName, end));
                    System.out.println("Got it. I've added this task:");
                    System.out.println("  " + tasks.get(tasks.size() - 1).toString());
                }
                System.out.println("Now you have " + tasks.size() + " tasks in the list.");
                System.out.println(bar);
            }
            input = scanner.nextLine();
        }

        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(bar);
        scanner.close();
    }
}
