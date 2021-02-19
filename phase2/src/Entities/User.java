package Entities;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * <User> is an abstract class that contains basic information
 * every user of this program should have
 */
public abstract class User implements Serializable {
    private final String username;
    private final String password;
    private List <String> friends;


    /**
     * Constructor specifying a User's username and password
     */

    public User(String username, String password){
        this.username = username;
        this.password = password;
        this.friends = new ArrayList<>();
    }

    /**
     * Getter for this <User></User>'s username.
     *
     * @return this <User></User>'s username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Getter for this <User></User>'s password.
     *
     * @return this <User></User>'s password
     */
    public String getPassword() {
        return password;
    }

    /*
     * Setter for this <User></User>'s password
     *
     *   the new password

    public void setPassword(String newPassword) {
        password = newPassword;
    }  */


    /**
     * Getter for friend list
     * @return  list of friends
     */
    public List<String> getFriends() {
        return friends;
    }


    /**
     * Setter for friend list
     * @param friends  list of friends
     */
    public void setFriends(List<String> friends) {
        this.friends = friends;
    }
}
