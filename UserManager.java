import java.io.*;
import java.util.*;

public class UserManager {
    private List<User> users;
    private static User currentUser;
    private static final String USERS_FILE = "users.txt";

    public UserManager() {
        users = new ArrayList<>();
        loadUsers();
    }


    public void loadUsers() {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 7) {
                    String name = data[0];
                    String phoneOrEmail = data[1];
                    String password = data[2];
                    boolean isMorningPerson = Boolean.parseBoolean(data[3]);
                    String purpose = data[4];
                    String startTime = data[5];
                    String endTime = data[6];

                    User user = new User(name, phoneOrEmail, password, isMorningPerson, purpose, startTime, endTime);
                    users.add(user);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading users: " + e.getMessage());
        }
    }


    public boolean registerUser(String name, String phoneOrEmail, String password, boolean isMorningPerson, String purpose, String startTime, String endTime) {
        if (getUserByPhoneOrEmail(phoneOrEmail) != null) {
            return false;
        }

        User user = new User(name, phoneOrEmail, password, isMorningPerson, purpose, startTime, endTime);
        users.add(user);
        saveUsers();
        return true;
    }


    public User getUserByPhoneOrEmail(String phoneOrEmail) {
        for (User user : users) {
            if (user.getPhoneOrEmail().equals(phoneOrEmail)) {
                return user;
            }
        }
        return null;
    }


    private void saveUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (User user : users) {
                writer.write(user.getName() + "," + user.getPhoneOrEmail() + "," + user.getPassword() + ","
                        + user.isMorningPerson() + "," + user.getPurpose() + "," + user.getStartTime() + "," + user.getEndTime() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }


    public User login(String phoneOrEmail, String password) {
        for (User user : users) {
            if (user.getPhoneOrEmail().equals(phoneOrEmail) && user.getPassword().equals(password)) {
                currentUser = user; // Set the logged-in user
                return user;
            }
        }
        return null;
    }


    public static User getCurrentUser() {
        return currentUser;
    }

    public boolean isMorningPerson() {
        return currentUser.isMorningPerson();
    }

    public String getStartTime() {
        return currentUser.getStartTime();
    }

    public String getEndTime() {
        return currentUser.getEndTime();
    }

    public String getSleepEndTime() {
        return currentUser.getStartTime();
    }

    public String getSleepStartTime() {
        return currentUser.getEndTime();
    }
}
