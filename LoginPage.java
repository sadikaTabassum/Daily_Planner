import java.util.Scanner;
import java.util.Scanner;

public class LoginPage {
    public void execute() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to Meplanner");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");  

        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            login(scanner);
        } else if (choice == 2) {
            new RegistrationPage().execute();
        }
        else if (choice == 3) {
            System.out.println("Goodbye!");
            System.exit(0);
        }
        else {
            System.out.println("Invalid choice! Please try again.");
            execute();
        }
    }

    private void login(Scanner scanner) {
        System.out.println("Enter phone/email:");
        String phoneOrEmail = scanner.nextLine();
        System.out.println("Enter password:");
        String password = scanner.nextLine();

        User user = PlannerApp.getUserManager().login(phoneOrEmail, password);
        if (user != null) {
            System.out.println("Login successful!");
            new HomePage().execute();
        } else {
            System.out.println("Invalid credentials! Would you like to try again?");
            System.out.println("1. Try login again");
            System.out.println("2. Register");
            int retryChoice = scanner.nextInt();
            scanner.nextLine();

            if (retryChoice == 1) {
                login(scanner);
            } else {
                new RegistrationPage().execute();
            }
        }
    }
}
