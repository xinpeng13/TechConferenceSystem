package Entities;
import java.util.ArrayList;
import java.util.List;

/**
 * Speaker is a class that gives talks and communicates with attendees.
 */
public class Speaker extends User {
    // list of talks the speaker gives
    private final List<String> talkTitles = new ArrayList<>();


    /**
     * Constructor specifying Speaker's Username and Password
     */
    public Speaker(String username, String password){
        super(username, password);
    }
    /**
     * Getter for the list of EventAttending.
     * @return the EventAttending of this Speaker.
     */
    public List<String> getTalkTitles() {return talkTitles;}

//    /**
//     * Setter for the list of EventAttending.
//     * @param talkTitles the new EventAttending that you wan to set.
//     */
//    public void setEventAttending(List<String> talkTitles){this.talkTitles = talkTitles;}

    /**
     * Add the given event title to list of events the speaker is attending/hosting.
     * @param title the title of the new event
     */
    public void addEventToSpeaker(String title){
        talkTitles.add(title);
    }

    /**
     * Delete the given event title from the list of events the speaker is attending/hosting.
     * @param title the title of the new event
     */
    public void deleteEventForSpeaker(String title){
        talkTitles.remove(title);
    }
}
