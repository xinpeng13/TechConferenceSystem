package UseCase;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import Entities.Attendee;
import Entities.Organizer;
import Entities.Speaker;
import Entities.User;

/**
 * A UseCase class that manages the functionalities of User class.
 *
 */
public class UserManager implements Serializable {
    private final List <Attendee> allAttendee = new ArrayList<>();
    private final List <Organizer> allOrganizer = new ArrayList<>();
    private final List <Speaker> allSpeaker = new ArrayList<>();

    public UserManager() {}


    /**
     * Getter for all userName of all attendees
     * @return A list of username that includes all attendees.
     */
    public List<String> getAllAttendee() {
        List <String> list = new ArrayList<>();
        for (Attendee attendees: allAttendee){
            list.add(attendees.getUsername());
        }
        return list;
    }

    /**
     * Getter for all userName of all Speakers
     * @return A list of username that includes all speakers.
     */
    public List<String> getAllSpeaker() {
        List <String> list = new ArrayList<>();
        for (Speaker speaker: allSpeaker){
            list.add(speaker.getUsername());
        }
        return list;
    }

    /**
     * Check whether the user account exists.
     * @param userName the name of the user account that you want to check.
     * @return true iff the username exists.
     */

    public boolean isUserExists(String userName){
        for (Attendee attendee: allAttendee){
            if (userName.equals(attendee.getUsername())) { return true; }
        }

        for (Organizer organizer: allOrganizer){
            if (userName.equals(organizer.getUsername())) { return true; }
        }

        for (Speaker speaker: allSpeaker){
            if (userName.equals(speaker.getUsername())) { return true; }
        }
        return false;
    }

    /**
     * Authorization of a user trying to log into their account
     *
     * @param enteredUsername  the username entered into the system
     * @param enteredPassword  the password entered into the system
     * @return a boolean value if the user successfully logged into the system of not.
     */
    public boolean credentialAuthorization(String enteredUsername, String enteredPassword){

        if(isUserExists(enteredUsername)){
            String userType = userType(enteredUsername);
            if (userType.equals("Speaker")){
                Speaker user = stringToSpeaker(enteredUsername);
                return user.getPassword().equals(enteredPassword);
            }
            Attendee user = stringToAttendee(enteredUsername);
            return user.getPassword().equals(enteredPassword);
        }

        return false;
    }

    /**
     * Signs up attendee for a particular event
     *
     * @param username The user we wants to sign up for an event
     * @param eventTitle  The event the user wants to sign up for
     */
    public void signUpEventAttendee(String username, String eventTitle){
        // This method is called only when the user have logged in to the system, thus the user must exist.
        Attendee user = stringToAttendee(username);
        List<String> eventList = user.getEventAttending();
        eventList.add(eventTitle);
        user.setEventAttending(eventList);
    }

    /**
     * Cancels an event that the attendee has already signed up for
     *
     * @param username   The user wants to cancel their event
     * @param eventTitle  The event the user wants to cancel
     */
    public void cancelSpotAttendee(String username, String eventTitle){
        // This method is called only when the user have logged in to the system, thus the user must exist.
        Attendee user = stringToAttendee(username);
        List<String> eventList = user.getEventAttending();
        eventList.remove(eventTitle);
        user.setEventAttending(eventList);
    }

    /**
     * Get all eventTitles for events this user signed up
     * @param username   The userName for the user
     * @return a list that contains all eventTitles this user have signed up
     */
    public List<String> getEventAttending(String username){
        // This method is called only when the user have logged in to the system, thus, the user must exist.
        Attendee user = stringToAttendee(username);
        return user.getEventAttending();
    }

    /**
     * Creates an Attendee Account
     *
     * @param userName  Username for an Attendee Account
     * @param password  Password for an Attendee Account
     * @return          true if successfully created an attendee account. False otherwise.
     */
    public boolean createAttendeeAccount(String userName, String password){

        if(isUserExists(userName)) { return false; }

        allAttendee.add(new Attendee(userName, password));
        return true;
    }


    /**
     * Creates an Organizer Account
     *
     * @param userName  Username for an Organizer Account
     * @param password  Password for an Organizer Account
     * @return          true if successfully created an organizer account. False otherwise
     */
    public boolean createOrganizerAccount(String userName, String password){

        if(isUserExists(userName)){ return false; }

        allOrganizer.add(new Organizer(userName, password));
        return true;
    }


    /**
     * Creates a Speaker Account
     *
     * @param userName    Username for a speaker Account
     * @param password    Password for a speaker Account
     * @return            true if successfully created a speaker account. False otherwise
     */
    public boolean createSpeakerAccount(String userName, String password){
        if(isUserExists(userName)){
            return false;
        }

        allSpeaker.add(new Speaker(userName, password));
        return true;
    }


    /**
     * Precondition: Username exists in database
     * Returns a User object that corresponds to Username
     *
     * @param username Username of an Attendee or Organizer that wants to be searched for
     * @return         User object that matches with username
     */
    private Attendee stringToAttendee(String username){
        assert isUserExists(username);
        for (Attendee attendee: allAttendee){
            if (attendee.getUsername().equals(username)){
                return attendee;
            }
        }
        for (Organizer organizer: allOrganizer){
            if (organizer.getUsername().equals(username)){
                return organizer;
            }
        }
        throw new IllegalArgumentException("There is no such an attendee or organizer with the username. ");
    }

    /**
     * Precondition: Username exists in the database, and it is a username of a speaker.
     * @param username Username of a speaker that wants to be searched for
     * @return         Speaker object that matches with username
     */

    private Speaker stringToSpeaker(String username){
        assert isUserExists(username) && userType(username).equals("Speaker");
        for (Speaker speaker: allSpeaker) {
            if (speaker.getUsername().equals(username)) {
                return speaker;
            }
        }
        throw new IllegalArgumentException("There is no such a speaker with the username " + username + ". ");
    }

    /**
     * Check whether the user has added this friend.
     * @param usernameA user who wants to add new friend
     * @param usernameB user that is being added
     * @return true iff the user have already added the friends.
     */

    public boolean isAddFriend(String usernameA, String usernameB){
        if (userType(usernameA).equals("Speaker")){
            Speaker userA = stringToSpeaker(usernameA);
            List<String> friends = userA.getFriends();
            for (String friend: friends){
                if(friend.equals(usernameB)){
                    return true;
                }
            }
        }else{
            Attendee userA = stringToAttendee(usernameA);
            List<String> friends = userA.getFriends();
            for (String friend: friends){
                if(friend.equals(usernameB)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Method to add user to friend's list
     *
     * @param usernameA  user wants to add user b
     * @param usernameB  user that is being added
     * @return   true if successfully added (iff the user b exists, they aren't already a friend and user b!= user a), false otherwise
     */
    public boolean addFriend(String usernameA, String usernameB){
        User userA;
        if (userType(usernameA).equals("Speaker")) {
            userA = stringToSpeaker(usernameA);
        }else{
            userA = stringToAttendee(usernameA);
        }

        List<String> friends = userA.getFriends();
        if (isUserExists(usernameB) && !isAddFriend(usernameA, usernameB) && !usernameA.equals(usernameB)){
                friends.add(usernameB);
                userA.setFriends(friends);
                return true;
        }
        return false;
    }

    /**
     * Add the given event title to the given speaker's list of events.
     * @param title the title of the event
     * @param speakerUserName the username of the speaker
     */
    public void addEventToSpeaker(String title, String speakerUserName){
        Speaker speaker = stringToSpeaker(speakerUserName);
        speaker.addEventToSpeaker(title);
    }

    /**
     * Delete the given event title for the given speaker's list of events.
     * @param title the title of the event
     * @param speakerUserName the username of the speaker
     */
    public void deleteEventForSpeaker(String title, String speakerUserName){
        Speaker speaker = stringToSpeaker(speakerUserName);
        speaker.deleteEventForSpeaker(title);
    }

    /**
     * Check the type of the user.
     * @param username the username of the user that you want to check.
     * @return return "Attendee" if the user is an attendee;
     * return "Organizer" if the user is an organizer;
     * return "Speaker" if the user is a speaker.
     * return "Invalid Username" if there is no such a user with the username.
     */

    public String userType(String username){
        for(Attendee attendee: allAttendee){
            if(username.equals(attendee.getUsername())) {return "Attendee";}
        }
        for (Organizer organizer: allOrganizer){
            if(username.equals(organizer.getUsername())) {return "Organizer";}
        }
        for(Speaker speaker: allSpeaker){
            if(username.equals(speaker.getUsername())) {return "Speaker";}
        }
        return "Invalid Username";
    }

    /**
     * PRECONDITION: Attendee or Speaker account with this username must exist
     * Returns a list of events names(strings) that this speaker is giving
     *
     * @param username String username of a specific speaker account
     * @return         a list of events titles they are speaking(for speaker)
     */
    public List<String> getEventsSpeaking(String username){
        // This method is called only when the user have logged in to the system, thus the speaker must exist.
        Speaker speaker = stringToSpeaker(username);
        return speaker.getTalkTitles();
    }

    /**
     * PRECONDITION: Attendee/Organizer account with this username must exist
     * Returns whether this Attendee/organizer is VIP
     *
     * @param userName String username of a Attendee/organizer account
     * @return         true iff this attendee/organizer is VIP
     */
    public boolean isAttendeeVIP(String userName){
        Attendee attendee = stringToAttendee(userName);
        return attendee.isVIP();
    }


    /**     Sets Attendee/Organizer account with VIP status
     *
     * @param userName Setting this Attendee/Organizer with VIP info;
     */
    public void setAttendeeVIP(String userName){
        Attendee attendee = stringToAttendee(userName);
        int eventNum = attendee.getEventAttending().size();
        attendee.setVIP(eventNum >= 2);
    }
}
