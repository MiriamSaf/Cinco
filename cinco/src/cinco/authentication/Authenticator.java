package cinco.authentication;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import cinco.TicketStatus;
import cinco.ticket;

/**
 * Authentication class. Retrieves and updates User information, including tickets and relevant data.
 */
public class Authenticator {

    private List<User> listOfUsers = new ArrayList<User>();

    public Authenticator(List<User> listOfUsers) {
        this.listOfUsers = listOfUsers;
    }

    /**
     * Validates user/password combination, fetches user information, and information relevant to the user.
     * 
     * @param userName username to search for
     * @param password password in cleartext
     * @return an AuthenticationResult instance containing User info and Ticketing info
     * @throws InvalidUserNameOrPasswordException
     * @throws UnsupportedOperationException
     */
    public AuthenticationResult authenticate(String userName, String password) 
        throws InvalidUserNameOrPasswordException, UnsupportedOperationException 
    {
        // See if user exists first
        User user = this.findUser(userName);
        if (user == null) {
            throw new InvalidUserNameOrPasswordException();
        }

        // Next, validate credentials
        validatePassword(user, password);

        // Get the user's data
        switch (user.getGroup()) {
            case STAFF_MEMBER:
            	break;
		case TECHNICIAN:
                break;
            default:
                throw new UnsupportedOperationException("Invalid Group");
        }
       
        // Return authentication result
        return new AuthenticationResult(user);
    }

    public static List<ticket> findTickets(List<ticket> tickets, User tech) {
        return Authenticator.findTickets(tickets, tech, false);
    }

    public static List<ticket> findTickets(List<ticket> tickets, User tech, boolean includeArchived) {
        // If not a tech, just show the ticket
        if (tech == null) {
            return tickets;
        }

        List<ticket> result = new ArrayList<ticket>();
        for (ticket t : tickets) {
            
            // Matches tech and the ticket is open
            boolean matchesUserAndOpen = t.getTechName() == tech.getUserName() && t.getStatus() == TicketStatus.OPEN;

            // Matches anyone and the ticket is closed and not archived (or archived if requested)
            boolean isArchived = t.isArchived();
            boolean closedAndNotArchived = (!isArchived || includeArchived) && t.getStatus() != TicketStatus.OPEN;
            
            if (matchesUserAndOpen || closedAndNotArchived) {
                result.add(t);
            }
        }

        return result;
    }
    public static List<ticket> findStaffTickets(List<ticket> tickets, User staff){
        if (staff == null) {
            return tickets;
        }

        List<ticket> result = new ArrayList<ticket>();
        for (ticket t : tickets) {
            boolean include = staff == null | t.getUserName() == staff.getUserName(); // Only include tickets that belong to the staff member
            include = include && t.getStatus() == TicketStatus.OPEN;                // Only include tickets that are OPEN

            if (include) {
                result.add(t);
            }
        }

        return result;
    }

    /**
     * Sets a new password for a user of a given username
     * @param email username
     * @param password new password
     * @throws InvalidUserNameOrPasswordException
     * @throws UnsupportedOperationException
     * @throws NoSuchAlgorithmException
     */
    public void resetPassword(String email, String password)
        throws InvalidUserNameOrPasswordException, UnsupportedOperationException, NoSuchAlgorithmException {
        
        // See if user exists first
        User user = this.findUser(email);
        if (user == null) {
            throw new InvalidUserNameOrPasswordException();
        }

        user.setPassword(password);
    }

    /**
     * Checks if a username exists
     * @param response username to check
     * @return username exists?
     */
    public boolean usernameExists(String response) {
        return this.findUser(response) != null;
    }

    /**
     * Adds a new user to the "database"
     * @param user
     */
    public void addUser(User user) {
        this.listOfUsers.add(user);
    }

    /**
     * Validates a provided password with a user instance's password.
     * 
     * Currently only stores and compares cleartext passwords #techdebt.
     * 
     * @param user existing populated user object
     * @param password a password entered in cleartext
     */
    private void validatePassword(User user, String password) {
        if (password.equals(user.getPassword())) {
            return;
        }

        // Password is incorrect
        throw new InvalidUserNameOrPasswordException();
    }

    /**
     * Finds a user with a matching username
     * 
     * @param userName username to match against (should be unique)
     * @return A matching user. Returns null if user not found.
     */
    private User findUser(String userName) {
        // Loop until User is found. Better to use a Dictionary or similar for efficiency.
        for (User user : this.listOfUsers) {
            if (user.getUserName().equals(userName)) {
                return user;
            }
        }

        return null;
    }
    
}
