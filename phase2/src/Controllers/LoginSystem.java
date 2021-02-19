package Controllers;

import java.io.Serializable;
import UseCase.UserManager;

/**
 * How Users or any type are able to login into the app. The run method will also hold information on what Usertype it
 * is in userType to be used in the corresponding UserMenu.
 *
 */
public class LoginSystem implements Serializable {
    UserManager manager;

    /**
     * Constructor for loginSystem
     * @param manager The usermanager to execute the class
     */
    public LoginSystem(UserManager manager) {
        this.manager = manager;
    }

    /**
     *
     * @param enteredUsername The Username the User inputs
     * @param enteredPassword The Password the User inputs
     * @return Need to create the UI for the menus for this to make sense. Perhaps return the User type and call on
     * the UI class?
     */
    public boolean verifyLogin(String enteredUsername, String enteredPassword) {
        boolean verified = false;
        if (manager.credentialAuthorization(enteredUsername, enteredPassword)) {
            verified = true;
        }
        return verified;
    }

    /**
     * When a User logs in, the system should recognize what kind of User they are and provide the appropriate menu.
     * This method should only be run once the login is successful.
     * @param username that we want to verify user type
     * @return User object to corresponding Username
     */
    public String verifyUserType(String username) {
        return manager.userType(username);
    }


}