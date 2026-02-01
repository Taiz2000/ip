package sagiri.ui;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import sagiri.task.Task;

public class UiTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    public void testPrintGreeting() {
        Ui.printGreeting();
        String output = outContent.toString();
        assertTrue(output.contains("Hello! I'm Sagiri"));
        assertTrue(output.contains("What can I do for you?"));
    }

    @Test
    public void testPrintTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Task("buy milk"));
        tasks.add(new Task("read book", "25-12-24"));
        Ui.printTasks(tasks);
        String output = outContent.toString();
        assertTrue(output.contains("1.[T][ ] buy milk"));
        assertTrue(output.contains("2.[D][ ] read book"));
    }

    @Test
    public void testPrintMarkedDone() {
        Task task = new Task("buy milk");
        task.markAsDone();
        Ui.printMarkedDone(task);
        String output = outContent.toString();
        assertTrue(output.contains("Nice! I've marked this task as done:"));
        assertTrue(output.contains("[X] buy milk"));
    }

    @Test
    public void testPrintAdded() {
        Task task = new Task("buy milk");
        Ui.printAdded(task, 1);
        String output = outContent.toString();
        assertTrue(output.contains("Got it. I've added this task:"));
        assertTrue(output.contains("[T][ ] buy milk"));
        assertTrue(output.contains("Now you have 1 tasks in the list."));
    }

    @Test
    public void testPrintError() {
        Ui.printError("Test error message");
        String output = outContent.toString();
        assertTrue(output.contains("Test error message"));
    }

    @Test
    public void testPrintBye() {
        Ui.printBye();
        String output = outContent.toString();
        assertTrue(output.contains("Bye. Hope to see you again soon!"));
    }
}
