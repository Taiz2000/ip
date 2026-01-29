import java.util.Scanner;

public class Sagiri {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String[][] tasks = new String[100][2];
        int index = 0;

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
                for (int i = 0; i < index; i++) {
                    System.out.println((i+1) + ".[" + tasks[i][0] + "] " + tasks[i][1]);

                }
                System.out.println(bar);
            } else if (input.startsWith("mark")) {
                int taskNumber = Integer.parseInt(input.split(" ")[1]) - 1;
                tasks[taskNumber][0] = "X";
                System.out.println(bar);
                System.out.println("Nice! I've marked this task as done:");
                System.out.println("[" + tasks[taskNumber][0] + "] " + tasks[taskNumber][1]);
                System.out.println(bar);
            } else if (input.startsWith("unmark")) {
                int taskNumber = Integer.parseInt(input.split(" ")[1]) - 1;
                tasks[taskNumber][0] = " ";
                System.out.println(bar);
                System.out.println("OK, I've marked this task as not done yet:");
                System.out.println("[" + tasks[taskNumber][0] + "] " + tasks[taskNumber][1]);
                System.out.println(bar);
            } else {
                System.out.println(bar);
                System.out.println("added: " + input);
                tasks[index][1] = input; // add to array
                tasks[index][0] = " "; // default status
                index++;
                System.out.println(bar);
            }
            input = scanner.nextLine();
        }

        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(bar);
        scanner.close();
    }
}
