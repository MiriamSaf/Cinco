package cinco.authentication;

public class AuthenticationResult {

    private User user;


    public AuthenticationResult(User user) {
        this.setUser(user);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
