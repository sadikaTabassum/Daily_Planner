import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;
import java.io.File;
import java.util.Scanner;
import java.util.Scanner;

public class PomodoroMenu {
    private Pomodoro pomodoro;

    public PomodoroMenu() {
        this.pomodoro = new Pomodoro();
    }

    public void execute() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Pomodoro Menu:");
        System.out.println("1. Start Pomodoro Session");
        System.out.println("2. View Completed Sessions");
        System.out.println("3. Back to Home Page");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                startPomodoroSession();
                break;
            case 2:
                pomodoro.viewCompletedSessions();
                break;
            case 3:
                PlannerApp.backToHomePage();
                break;
            default:
                System.out.println("Invalid choice. Returning to Pomodoro Menu.");
                execute();
        }
    }

    private void startPomodoroSession() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter session duration (minutes):");
        int duration = scanner.nextInt();
        System.out.println("Enter break time (minutes):");
        int breakTime = scanner.nextInt();
        System.out.println("Enter number of focus sessions:");
        int totalSessions = scanner.nextInt();

        pomodoro.configurePomodoro(duration, breakTime, totalSessions);
        pomodoro.startPomodoroSession();
    }
}
