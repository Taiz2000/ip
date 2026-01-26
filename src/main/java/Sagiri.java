import java.util.Scanner;

public class Sagiri {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String name = "Sagiri";
        String bar = "____________________________________________________________";
        System.out.println(bar);
        System.out.println("Hello! I'm " + name);
        System.out.println("What can I do for you?");
        System.out.println(bar);

        String input = scanner.nextLine();

        while (!input.equals("bye")) { // check if input is bye
            // loop to read and echo user input
            System.out.println(bar);
            System.out.println(input);
            System.out.println(bar);
            input = scanner.nextLine();
        }

        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(bar);
    }
}
