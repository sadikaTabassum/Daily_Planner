import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.io.*;
import java.util.Date;
import java.io.IOException;


public class Pomodoro {
    private int sessionTime;
    private int breakTime;
    private int totalSessions;
    private int completedSessions;
    private boolean isRunning;

    public Pomodoro() {
        this.sessionTime = 25;
        this.breakTime = 5;
        this.totalSessions = 4;
        this.completedSessions = 0;
        this.isRunning = false;
    }


    public void configurePomodoro(int sessionTime, int breakTime, int totalSessions) {
        this.sessionTime = sessionTime;
        this.breakTime = breakTime;
        this.totalSessions = totalSessions;
        this.completedSessions = 0;
    }


    public void startPomodoroSession() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Pomodoro session starting...");
        int sessionCount = 0;
        isRunning = true;

        while (sessionCount < totalSessions && isRunning) {

            currentState("WORKING", sessionTime);

            if (isRunning) {
                currentState("BREAK", breakTime);
            }

            if (isRunning) {
                completedSessions++;
                sessionCount++;
                System.out.println("Pomodoro session completed!");


                saveSessionToFile();


                System.out.println("1. Stop Session");
                System.out.println("2. Start next session");
                System.out.println("3. Go back to Home Page");
                int choice = scanner.nextInt();

                if (choice == 1) {
                    stopPomodoro();
                    break;
                } else if (choice == 2) {
                    continue;
                } else if (choice == 3) {
                    stopPomodoro();
                    PlannerApp.backToHomePage();
                    break;
                }
            }
        }


        if (isRunning) {
            System.out.println("Pomodoro cycle completed!");
            System.out.println("Do you want to go back to the home page?");
            System.out.println("1. Go to Home Page");
            System.out.println("2. Exit");

            int choice = scanner.nextInt();
            if (choice == 1) {
                PlannerApp.backToHomePage();
            } else {
                System.out.println("Goodbye!");
                System.exit(0);
            }
        }
    }


    private void currentState(String state, int minutes) {
        Scanner scanner = new Scanner(System.in);

        int totalSeconds = minutes * 60;
        for (int i = totalSeconds; i >= 0; i--) {
            if (!isRunning) break;

            long minutesRemaining = TimeUnit.SECONDS.toMinutes(i);
            long secondsRemaining = i % 60;
            System.out.println("Current State: " + state);
            System.out.println("Time Remaining: " + minutesRemaining + " minutes, " + secondsRemaining + " seconds");


            if (i == 0) {
                break;
            }


            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                System.out.println("An error occurred.");
            }


            if (i == 0 && isRunning) {
                System.out.println("1. Stop Session");
                System.out.println("2. Continue");
                int choice = scanner.nextInt();
                if (choice == 1) {
                    stopPomodoro();
                    break;
                }
            }
        }
    }


    public void stopPomodoro() {
        isRunning = false;
        System.out.println("Pomodoro session stopped.");
        PlannerApp.backToHomePage();  // Go back to home page
    }


    private void saveSessionToFile() {
        try {
            FileWriter writer = new FileWriter("pomodoro_sessions.txt", true);
            writer.write("Session completed on: " + new Date() + "\n");
            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred while saving the session.");
        }
    }


    public void viewCompletedSessions() {
        try {
            File file = new File("pomodoro_sessions.txt");
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
                reader.close();
            } else {
                System.out.println("No sessions found.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading the sessions.");
        }


        new PlannerApp().backToHomePage();
    }

    public void startBreak(int breakDuration) {

    }
}
