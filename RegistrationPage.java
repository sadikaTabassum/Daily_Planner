import java.util.Scanner;
import java.io.*;
import java.util.Scanner;

public class RegistrationPage {
    public void execute() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter your name:");
        String name = scanner.nextLine();

        System.out.println("Enter your phone number:");
        String phone = scanner.nextLine();

        System.out.println("Enter your email:");
        String email = scanner.nextLine();

        System.out.println("Enter your password:");
        String password = scanner.nextLine();

        System.out.println("Are you a morning person or night person? (morning/night):");
        String personType = scanner.nextLine();

        boolean isMorningPerson = personType.equalsIgnoreCase("morning");

        System.out.println("What is your purpose? (e.g., university, job, school):");
        String purpose = scanner.nextLine();

        System.out.println("Enter your start time (HH:mm format, e.g., 08:00):");
        String startTime = scanner.nextLine();

        System.out.println("Enter your end time (HH:mm format, e.g., 17:00):");
        String endTime = scanner.nextLine();

        boolean isRegistered = PlannerApp.getUserManager().registerUser(name, phone, password, isMorningPerson, purpose, startTime, endTime);
        if (isRegistered) {
            System.out.println("Registration successful! You can now log in.");
            new LoginPage().execute();
        } else {
            System.out.println("User already exists with this email/phone. Please try again.");
            execute();
        }
    }
}
