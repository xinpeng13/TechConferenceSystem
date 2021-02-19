package Controllers;

import UseCase.EventManager;
import UseCase.RoomManager;
import UseCase.UserManager;
import java.util.ArrayList;
import java.util.List;

/**
 * A class that allow Users to sign up/cancel spot for an event
 */
public class SignUpSystem {
    EventManager em;
    UserManager um;
    RoomManager rm;

    /**
     * Constructor for SignUpSystem
     * @param em the EventManager for this execution of the program
     * @param um the UserManager for this execution of the program
     */
    public SignUpSystem(EventManager em, UserManager um, RoomManager rm) {
        this.em = em;
        this.um = um;
        this.rm = rm;
    }

    /**
     * Method that calls methods in EventManager and UserManager to sign up for an event
     * @param userName the username of this Attendee
     * @param eventTitle the event title of the event that this attendee want to sign up for
     */
    public int signUpEvent(String userName, String eventTitle){
        if (!em.isEventExist(eventTitle)){
            return 3;
        }
        else if (em.isAttendeeAdded(userName, eventTitle)){
            return 1;
        }
        else if(em.isEventFull(eventTitle)){
            return 2;
        }
        else if (!um.isAttendeeVIP(userName) & em.VIP(eventTitle)){
            return 4;
        }
        else{
            em.addAttendee(userName, eventTitle);
            um.signUpEventAttendee(userName, eventTitle);
            um.setAttendeeVIP(userName);
            return 0;
        }
    }

    /**
     * Method that calls methods in EventManager and UserManager to cancel spot for an event
     * @param userName the username of this Attendee
     * @param eventTitle the event title of the event that this attendee want to cancel spot
     */
    public int cancelSpotEvent(String userName, String eventTitle){
        if(!em.isEventExist(eventTitle)){
            return 2;
        }else if(!em.isAttendeeAdded(userName, eventTitle)){
            return 1;
        }
        else{
            em.deleteAttendee(userName, eventTitle);
            um.cancelSpotAttendee(userName, eventTitle);
            um.setAttendeeVIP(userName);
            return 0;
        }
    }

    /**
     * This method gets the event's a username has signed up for
     * @param username List of events that their signed up for already
     * @return A list of events that this username has signed up for
     */
    public String[] displaySignedUpEvents(String username) {
        List<String> events;
        List<String> eventinfo = new ArrayList<>();
        String[] toReturn;
        if (um.userType(username).equals("Speaker")){
            events = um.getEventsSpeaking(username);
            toReturn = events.toArray(new String[0]);
        }else {
            events = um.getEventAttending(username);
            for(String event : events){
                eventinfo.add(em.getEventInfo(event));
            }
            toReturn = eventinfo.toArray(new String[0]);
        }
        return toReturn;
    }

    /**
     * Gets all events possible to display;
     * @return   A list of event info
     */
    public String[] displayAllEvents() {
        List<String> events = em.getAllEventTitle();
        List<String> eventinfo = new ArrayList<>();
        for(String event : events){
            eventinfo.add(em.getEventInfo(event));
        }
        return eventinfo.toArray(new String[0]);
    }

    /**
     * Checks whether username meets the requirement for VIP account or not (attends at least 2 events)
     * @param username the username want to check
     * @return iff the user is VIP
     */
    public boolean userIsVIP(String username) {
        if (um.userType(username).equals("Speaker")){
            return false;
        }else{
            return um.isAttendeeVIP(username);
        }
    }
}
