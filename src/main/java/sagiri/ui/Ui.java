package sagiri.ui;
import java.util.ArrayList;

import sagiri.task.Task;
import sagiri.task.TaskType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Ui {
    private static final String BAR = "____________________________________________________________";

    /**
     * Shows the initial greeting message.
     */
    public static void printGreeting() {
        System.out.println(BAR);
        System.out.println("Hello! I'm Sagiri");
        System.out.println("What can I do for you?");
        System.out.println(BAR);
    }

    /**
     * Lists all tasks in the task list.
     */
    public static void printTasks(ArrayList<Task> tasks) {
        System.out.println(BAR);
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i).toString());
        }
        System.out.println(BAR);
    }

    /**
     * Prints message when task is marked as done.
     */
    public static void printMarkedDone(Task task) {
        System.out.println(BAR);
        System.out.println("Nice! I've marked this task as done:");
        System.out.println(task.toString());
        System.out.println(BAR);
    }

    /**
     * Prints message when task is marked as not done.
     */
    public static void printMarkedNotDone(Task task) {
        System.out.println(BAR);
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println(task.toString());
        System.out.println(BAR);
    }

    /**
     * Prints message when task is deleted.
     */
    public static void printDeleted(Task task) {
        System.out.println(BAR);
        System.out.println("Noted. I've removed this task:");
        System.out.println(task.toString());
        System.out.println(BAR);
    }

    /**
     * Prints message when task is added.
     */
    public static void printAdded(Task task, int totalTasks) {
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task.toString());
        System.out.println("Now you have " + totalTasks + " tasks in the list.");
        System.out.println(BAR);
    }

    /**
     * Prints tasks for a specific date.
     */
    public static void printCheckedTasks(ArrayList<Task> tasks, String dateStr, LocalDateTime targetDate) {
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
     * Prints error message.
     */
    public static void printError(String message) {
        System.out.println(BAR);
        System.out.println(message);
        System.out.println(BAR);
    }

    /**
     * Prints bye message.
     */
    public static void printBye() {
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(BAR);
    }

    /**
     * Prints loading error.
     */
    public static void printLoadingError(String message) {
        System.out.println(BAR);
        System.out.println("Error loading saved tasks: " + message);
        System.out.println("Starting with empty task list.");
        System.out.println(BAR);
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
}
