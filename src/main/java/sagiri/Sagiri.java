package sagiri;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import sagiri.command.Parser;
import sagiri.exception.SagiriException;
import sagiri.storage.Storage;
import sagiri.task.Task;
import sagiri.task.TaskList;
import sagiri.task.TaskType;

/**
 * Core chatbot logic used by the GUI.
 */
public class Sagiri {
    private final TaskList taskList;

    public Sagiri() {
        taskList = new TaskList();
        try {
            Storage.loadTasks(taskList);
        } catch (SagiriException e) {
            // Continue with empty task list when loading fails.
        }
    }

    /**
     * Generates a response for the user's input.
     */
    public String getResponse(String input) {
        try {
            Parser.ParsedCommand command = Parser.parse(input);
            return processCommand(command);
        } catch (SagiriException e) {
            return e.getMessage();
        }
    }

    private String processCommand(Parser.ParsedCommand command) throws SagiriException {
        switch (command.type) {
        case LIST:
            return formatTaskList(taskList.getTasks());
        case MARK:
            taskList.markTaskDone(command.taskIndex);
            Storage.saveTasks(taskList);
            return "Nice! I've marked this task as done:\n" + taskList.getTasks().get(command.taskIndex);
        case UNMARK:
            taskList.markTaskNotDone(command.taskIndex);
            Storage.saveTasks(taskList);
            return "OK, I've marked this task as not done yet:\n" + taskList.getTasks().get(command.taskIndex);
        case DELETE:
            Task removed = taskList.deleteTask(command.taskIndex);
            Storage.saveTasks(taskList);
            return "Noted. I've removed this task:\n" + removed;
        case TODO:
            Task addedTodo = taskList.addTodo(command.data);
            Storage.saveTasks(taskList);
            return "Got it. I've added this task:\n" + addedTodo + "\nNow you have " + taskList.size()
                    + " tasks in the list.";
        case EVENT:
            Task addedEvent = taskList.addEvent(command.data);
            Storage.saveTasks(taskList);
            return "Got it. I've added this task:\n" + addedEvent + "\nNow you have " + taskList.size()
                    + " tasks in the list.";
        case DEADLINE:
            Task addedDeadline = taskList.addDeadline(command.data);
            Storage.saveTasks(taskList);
            return "Got it. I've added this task:\n" + addedDeadline + "\nNow you have " + taskList.size()
                    + " tasks in the list.";
        case CHECK:
            return formatCheckedTasks(taskList.getTasks(), command.data, TaskList.parseDate(command.data));
        case FIND:
            return formatFoundTasks(taskList.getTasks(), command.data);
        case BYE:
            return "Bye. Hope to see you again soon!";
        default:
            throw new SagiriException("No clue what that means :((");
        }
    }

    private String formatTaskList(ArrayList<Task> tasks) {
        if (tasks.isEmpty()) {
            return "You have no tasks in your list.";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            sb.append(i + 1).append('.').append(tasks.get(i));
            if (i < tasks.size() - 1) {
                sb.append('\n');
            }
        }
        return sb.toString();
    }

    private String formatCheckedTasks(ArrayList<Task> tasks, String dateStr, LocalDateTime targetDate) {
        StringBuilder sb = new StringBuilder();
        sb.append("Tasks for ").append(formatDateForDisplay(targetDate)).append(":\n");

        boolean foundTasks = false;
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            boolean matches = false;

            if (task.getType() == TaskType.EVENT) {
                LocalDateTime startDate = task.getStartDateTime();
                LocalDateTime endDate = task.getEndDateTime();
                if ((startDate != null && datesEqual(startDate, targetDate))
                        || (endDate != null && datesEqual(endDate, targetDate))) {
                    matches = true;
                }
            } else if (task.getType() == TaskType.DEADLINE) {
                LocalDateTime deadlineDate = task.getEndDateTime();
                if (deadlineDate != null && datesEqual(deadlineDate, targetDate)) {
                    matches = true;
                }
            }

            if (matches) {
                if (foundTasks) {
                    sb.append('\n');
                }
                sb.append(i + 1).append('.').append(task);
                foundTasks = true;
            }
        }

        if (!foundTasks) {
            sb.append("No tasks found for this date.");
        }

        return sb.toString();
    }

    private String formatFoundTasks(ArrayList<Task> tasks, String keyword) {
        StringBuilder sb = new StringBuilder();
        sb.append("Tasks matching \"").append(keyword).append("\":\n");

        boolean foundTasks = false;
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.getName().toLowerCase().contains(keyword.toLowerCase())) {
                if (foundTasks) {
                    sb.append('\n');
                }
                sb.append(i + 1).append('.').append(task);
                foundTasks = true;
            }
        }

        if (!foundTasks) {
            sb.append("No tasks found matching \"").append(keyword).append("\".");
        }

        return sb.toString();
    }

    private String formatDateForDisplay(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "Unknown";
        }
        return dateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }

    private boolean datesEqual(LocalDateTime date1, LocalDateTime date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return date1.toLocalDate().equals(date2.toLocalDate());
    }
}
