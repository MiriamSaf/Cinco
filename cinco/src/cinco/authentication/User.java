package cinco.authentication;

/**
 * Base interface for all users
 */
public interface User {
    String getUserName();
    String getPassword();
    Group getGroup();
    String getPhNumber();
    String getFName();
    String getLName();
    

    void setUserName(String userName);
    void setGroup(Group group);
    void setPassword(String password);
    void setPhNumber(String phNumber);
    void setFName(String FName);
    void setLName(String LName);
}
