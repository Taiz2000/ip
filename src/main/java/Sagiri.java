import java.util.Scanner;

import sagiri.command.Parser;
import sagiri.exception.SagiriException;
import sagiri.storage.Storage;
import sagiri.task.Task;
import sagiri.task.TaskList;
import sagiri.ui.Ui;

public class Sagiri {

    /**
     * Processes a user command and performs the appropriate action.
     */
    private static void processCommand(TaskList taskList, Parser.ParsedCommand command) throws SagiriException {
        switch (command.type) {
        case LIST:
            Ui.printTasks(taskList.getTasks());
            break;
        case MARK:
            taskList.markTaskDone(command.taskIndex);
            Ui.printMarkedDone(taskList.getTasks().get(command.taskIndex));
            Storage.saveTasks(taskList);
            break;
        case UNMARK:
            taskList.markTaskNotDone(command.taskIndex);
            Ui.printMarkedNotDone(taskList.getTasks().get(command.taskIndex));
            Storage.saveTasks(taskList);
            break;
        case DELETE:
            Task removed = taskList.deleteTask(command.taskIndex);
            Ui.printDeleted(removed);
            Storage.saveTasks(taskList);
            break;
        case TODO:
            Task addedTodo = taskList.addTodo(command.data);
            Ui.printAdded(addedTodo, taskList.size());
            Storage.saveTasks(taskList);
            break;
        case EVENT:
            Task addedEvent = taskList.addEvent(command.data);
            Ui.printAdded(addedEvent, taskList.size());
            Storage.saveTasks(taskList);
            break;
        case DEADLINE:
            Task addedDeadline = taskList.addDeadline(command.data);
            Ui.printAdded(addedDeadline, taskList.size());
            Storage.saveTasks(taskList);
            break;
        case CHECK:
            Ui.printCheckedTasks(taskList.getTasks(), command.data, TaskList.parseDate(command.data));
            break;
        case FIND:
            Ui.printFoundTasks(taskList.getTasks(), command.data);
            break;
        case BYE:
            // Handled in main
            break;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TaskList taskList = new TaskList();

        try {
            Storage.loadTasks(taskList);
        } catch (SagiriException e) {
            Ui.printLoadingError(e.getMessage());
        }
        Ui.printGreeting();

        String input = scanner.nextLine();

        while (!input.equals("bye")) {
            try {
                Parser.ParsedCommand command = Parser.parse(input);
                if (command.type == Parser.CommandType.BYE) {
                    break;
                }
                processCommand(taskList, command);
            } catch (SagiriException e) {
                Ui.printError(e.getMessage());
            }
            input = scanner.nextLine();
        }

        Ui.printBye();
        scanner.close();
    }
}
