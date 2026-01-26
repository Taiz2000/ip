import java.util.Scanner;

public class Sagiri {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String[] tasks = new String[100];
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
                    System.out.println((i+1) + ". " + tasks[i]);
                }
                System.out.println(bar);
            } else {
                System.out.println(bar);
                System.out.println("added: " + input);
                tasks[index] = input; // add to array
                index++;
                System.out.println(bar);
            }
            input = scanner.nextLine();
        }

        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(bar);
    }
}
