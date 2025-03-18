public class LogoutPage {
    public void execute() {
        System.out.println("You have successfully logged out.");
        new LoginPage().execute();
    }
}
