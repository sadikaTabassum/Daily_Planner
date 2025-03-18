public class User {
    private String name;
    private String phoneOrEmail;
    private String password;
    private boolean isMorningPerson;
    private String purpose;
    private String startTime;
    private String endTime;

    public User(String name, String phoneOrEmail, String password, boolean isMorningPerson, String purpose, String startTime, String endTime) {
        this.name = name;
        this.phoneOrEmail = phoneOrEmail;
        this.password = password;
        this.isMorningPerson = isMorningPerson;
        this.purpose = purpose;
        this.startTime = startTime;
        this.endTime = endTime;
    }


    public String getPhoneOrEmail() {
        return phoneOrEmail;
    }

    public String getPassword() {
        return password;
    }

    public boolean isMorningPerson() {
        return isMorningPerson;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getName() {
        return name;
    }
}
