package Controllers;

import UseCase.EventManager;
import UseCase.RoomManager;
import UseCase.UserManager;
import java.util.ArrayList;
import java.util.List;


/**
 * A controller class that interacts with use cases and presenters to prompt and
 * allow the user to schedule an event or add a room.
 * @author Xinyi Chen, Xinpeng Shan(phase 2 changes)
 */
public class SchedulingSystem {
    EventManager em;
    RoomManager rm;
    UserManager um;
    MessagingSystem ms;

    /**
     * Constructor for SchedulingSystem
     * @param eventManager the EventManager for this execution of the program
     * @param roomManager the RoomManager for this execution of the program
     * @param userManager the UserManager for this execution of the program
     * @param messagingSystem the MessagingSystem for this execution of the program
     */
    public SchedulingSystem(EventManager eventManager, RoomManager roomManager, UserManager userManager, MessagingSystem messagingSystem) {
        em = eventManager;
        rm = roomManager;
        um = userManager;
        ms = messagingSystem;
    }


    /**
     * Check if the conditions for adding the given room is satisfied and display error messages accordingly.
     * If satisfied, create new room and print success message.
     * @param rmNum the room number for the event
     * @return true iff the room has been successfully added, false otherwise
     */
    public boolean addRoom(String rmNum, int capacity){
        if (rm.doesRoomExist(rmNum)){
            return false;
        }else{
            rm.createRoom(rmNum, capacity);
            return true;
        }
    }



    /**
     * Check if the conditions for adding the given one speaker event/ multi speaker event is satisfied and return error messages accordingly.
     * If satisfied, create new event, update speaker's list of events, and print success message.
     * @param startDate the start date for the potential event (YYYYMMDD)
     * @param endDate the end date for the potential event (YYYYMMDD)
     * @param startTime the start time for the event (HH:mm:ss)
     * @param endTime the end time for the event (HH:mm:ss)
     * @param rmNum the room number for the event
     * @param speakerUsernames the names of the speakers for the event
     * @param title the title for the event
     * @param maxNum the maximum/ capacity of people that can attend this event
     * @return the error message according to the error or "true" if event successfully created
     */
    public String helper_addSpeakerEvent (Boolean VIP, String startDate, String endDate, String startTime, String endTime, String rmNum, List<String> speakerUsernames, String title, int maxNum){
        //check if dates is valid format and value
        if (!em.parseStringToLocalDate(startDate)){
            return "Uh-oh! The start date entered is not a valid date or not written in the correct format (YYYYMMDD)!";
        }
        else if (!em.parseStringToLocalDate(endDate)){
            return "Uh-oh! The end date entered is not a valid date or not written in the correct format (YYYYMMDD)!";
        }
        //check if start time is valid format and value
        else if (!em.parseStringToLocalTime(startTime)){
            return "Uh-oh! The start time entered is not a valid date or not written in the correct format (24-hour time, HH:MM:SS)!";
        }
        //check if end time is valid format and value
        else if (!em.parseStringToLocalTime(endTime)){
            return "Uh-oh! The end time entered is not a valid date or not written in the correct format (24-hour time, HH:MM:SS)!";
        }
        //check if start time < endtime
        else if (!em.isTimeValid(startDate, endDate, startTime, endTime)){
            return "Uh-oh! The end time entered should be after the start time for this event!";
        }
        //check if room exists
        else if(!rm.doesRoomExist(rmNum)){
            return "Uh-oh! Room does not exist! Please add this room first!";
        }
        //check if room is booked already at this time
        else if(!em.isRoomAvailableAtTime(rmNum, startDate, endDate, startTime, endTime)){
            return "Uh-oh! Room is already booked at the given time!";
        }
        //check if speaker exists
        else if(!helperAreSpeakersExist(speakerUsernames)){
            return "Uh-oh! One or More Speakers you entered does not exist! Please create an account for these speakers first!";
        }
        //check if speaker is already giving another talk at this time
        else if(!helperAreSpeakersAvailable(startDate, endDate, startTime, endTime, speakerUsernames)){
            return"Uh-oh! One or More Speakers you entered is already booked for another event at the given time!";
        }
        //check if event title is unique
        else if(!em.isEventTitleUnique(title)){
            return"Uh-oh! The event title has already been taken!";
        }
        else if (maxNum <= 0){
            return"Uh-oh! The maximum number of people who can attend should be a positive integer. ";
        }
        // check if the maximum number of the people who can attend the event exceeds the assigned room capacity.
        else if (maxNum > rm.getCapacity(rmNum)){
            return "Uh-oh! The maximum number of people who can attend the event exceeds the room capacity.";
        }
        //if everything works out
        else if(canAddSpeakerEvent(startDate, endDate, startTime, endTime, rmNum, speakerUsernames, title, maxNum)){
            em.createEvent(VIP, title, startDate, endDate, startTime, endTime, rmNum, maxNum, speakerUsernames);
            //update the speaker's list of events
            for (String speakerUsername : speakerUsernames){ um.addEventToSpeaker(title, speakerUsername);}
            return "true";
        }
        return "Event was not created due to some unexpected reason.";
    }

    /**
     * Helper method to check if all speakers in the list exists
     * @param speakerUsernames the list of names of the speakers for the event
     * @return true iff all speakers in the list exists
     */
    private boolean helperAreSpeakersExist(List<String> speakerUsernames){
        if (speakerUsernames.isEmpty()){
            return false;
        }
        for(String speakerUsername : speakerUsernames){
            if((!um.isUserExists(speakerUsername)) || (!um.userType(speakerUsername).equals("Speaker"))){
                return false;
            }
        }
        return true;
    }

    /**
     * Helper method to check if all speakers in the list are available
     * @param startDate the start date for the potential event (YYYYMMDD)
     * @param endDate the end date for the potential event (YYYYMMDD)
     * @param startTime the start time for the event (HH:mm:ss)
     * @param endTime the end time for the event (HH:mm:ss)
     * @param speakerUsernames the list of names of the speakers for the event
     * @return true iff all speakers in the list are available
     */
    private boolean helperAreSpeakersAvailable(String startDate, String endDate, String startTime, String endTime, List<String> speakerUsernames){
        if (speakerUsernames.isEmpty()){
            return false;
        }
        for(String speakerUsername : speakerUsernames){
            if(!em.isSpeakerAvailableAtTime(startDate, endDate, startTime,endTime, speakerUsername)){
                return false;
            }
        }
        return true;
    }


    /**
     * Check whether or not the given event can be created satisfying all the requirements detailed in the description.
     * @param startDate the start date for the potential event (YYYYMMDD)
     * @param endDate the end date for the potential event (YYYYMMDD)
     * @param startTime the start time for the event (HH:mm:ss)
     * @param endTime the end time for the event (HH:mm:ss)
     * @param rmNum the room number for the event
     * @param speakerUsernames the list of names of the speakers for the event
     * @param title the title for the event
     * @return true iff the event can be created
     */
    private boolean canAddSpeakerEvent(String startDate, String endDate, String startTime, String endTime, String rmNum, List<String> speakerUsernames, String title, int maxNum){
        return em.parseStringToLocalDate(startDate) && em.parseStringToLocalTime(startTime) && em.parseStringToLocalTime(endTime)
                && rm.doesRoomExist(rmNum) && em.isRoomAvailableAtTime(rmNum, startDate, endDate, startTime, endTime)
                && helperAreSpeakersExist(speakerUsernames) && helperAreSpeakersAvailable(startDate, endDate, startTime, endTime, speakerUsernames)
                && em.isEventTitleUnique(title) && maxNum > 0 && maxNum <= rm.getCapacity(rmNum);
    }

    /**
     * Check whether or not the given event can be created satisfying all the requirements detailed in the description.
     * @param startDate the start date for the potential event (YYYYMMDD)
     * @param endDate the end date for the potential event (YYYYMMDD)
     * @param startTime the start time for the event (HH:mm:ss)
     * @param endTime the end time for the event (HH:mm:ss)
     * @param rmNum the room number for the event
     * @param title the title for the event
     * @return true iff the event can be created
     */
    private boolean canAddParty (String startDate, String endDate, String startTime, String endTime, String rmNum, String title, int maxNum){
        return em.parseStringToLocalDate(startDate) && em.parseStringToLocalTime(startTime) && em.parseStringToLocalTime(endTime)
                && rm.doesRoomExist(rmNum) && em.isRoomAvailableAtTime(rmNum, startDate, endDate, startTime, endTime)
                && em.isEventTitleUnique(title) && maxNum > 0 && maxNum <= rm.getCapacity(rmNum);
    }

    /**
     * Check if the conditions for adding the given no speaker event is satisfied and return error messages accordingly.
     * If satisfied, create new event, update speaker's list of events, and print success message.
     * @param startDate the start date for the potential event (YYYYMMDD)
     * @param endDate the end date for the potential event (YYYYMMDD)
     * @param startTime the start time for the event (HH:mm:ss)
     * @param endTime the end time for the event (HH:mm:ss)
     * @param rmNum the room number for the event
     * @param speakerUsernames the names of the speakers for the event (should just be empty)
     * @param title the title for the event
     * @param maxNum the maximum/ capacity of people that can attend this event
     * @return the error message according to the error or "true" if event successfully created
     */
    public String helper_addParty (Boolean VIP, String startDate, String endDate, String startTime, String endTime, String rmNum, List<String> speakerUsernames, String title, int maxNum){
        //check if dates is valid format and value
        if (!em.parseStringToLocalDate(startDate)){
            return "Uh-oh! The start date entered is not a valid date or not written in the correct format (YYYYMMDD)!";
        }
        else if (!em.parseStringToLocalDate(endDate)){
            return "Uh-oh! The end date entered is not a valid date or not written in the correct format (YYYYMMDD)!";
        }
        //check if start time is valid format and value
        else if (!em.parseStringToLocalTime(startTime)){
            return "Uh-oh! The start time entered is not a valid date or not written in the correct format (24-hour time, HH:MM:SS)!";
        }
        //check if end time is valid format and value
        else if (!em.parseStringToLocalTime(endTime)){
            return "Uh-oh! The end time entered is not a valid date or not written in the correct format (24-hour time, HH:MM:SS)!";
        }
        //check if start time < endtime
        else if (!em.isTimeValid(startDate, endDate, startTime, endTime)){
            return "Uh-oh! The end time entered should be after the start time for this event!";
        }
        //check if room exists
        else if(!rm.doesRoomExist(rmNum)){
            return "Uh-oh! Room does not exist! Please add this room first!";
        }
        //check if room is booked already at this time
        else if(!em.isRoomAvailableAtTime(rmNum, startDate, endDate, startTime, endTime)){
            return "Uh-oh! Room is already booked at the given time!";
        }
        //check if event title is unique
        else if(!em.isEventTitleUnique(title)){
            return"Uh-oh! The event title has already been taken!";
        }
        else if (maxNum <= 0){
            return"Uh-oh! The maximum number of people who can attend should be a positive integer. ";
        }
        // check if the maximum number of the people who can attend the event exceeds the assigned room capacity.
        else if (maxNum > rm.getCapacity(rmNum)){
            return "Uh-oh! The maximum number of people who can attend the event exceeds the room capacity.";
        }
        //if everything works out
        else if(canAddParty(startDate, endDate, startTime, endTime, rmNum, title, maxNum)){
            em.createEvent(VIP, title, startDate, endDate, startTime, endTime, rmNum, maxNum, speakerUsernames);
            return "true";
        }
        return "Event was not created due to some unexpected reason.";
    }


    /**
     * Perform necessary checks & operations for cancelling an event.
     * @param   title the event name entered
     * @param   username the username of the organizer that chose to cancel this event
     * @return  true if successfully canceled event, False Otherwise.
     */
    public boolean cancelEvent(String title, String username){
        //check if event already exists
        if (em.isEventExist(title)){
//          Update all attendees’ and Speakers of the event for the change,
            List<String> eventTitles = new ArrayList<>();
            eventTitles.add(title);
            String content = "ANNOUNCEMENT: One of the events you are participating in: " + eventTitles.get(0) + " has been cancelled! ";
            ms.organizerMessageEventSpeakersAndAttendees(username,eventTitles, content, "");
            //delete the event in their list of attending
            List<String> attendees = em.getAllAttendeesByTitle(title);
            for(String a : attendees){
                um.cancelSpotAttendee(a, title);
            }

            //delete the event in the speaker's list of talks
            List<String> speakers = em.getSpeakerUsernameByTitle(title);
            for(String s : speakers){
                um.deleteEventForSpeaker(title, s);
            }
            //delete the actual event
            em.deleteEvent(title);
            return true;
        }else{
            return false;
        }
    }

    /**
     * Perform necessary checks & operations for changing the capacity of an event.
     * @param title   String event name to change capacity
     * @param capacity    capacity to change to
     * @param username    the username of the organizer that chose to change the capacity of this event
     * @return            true if successfully changed, false otherwise.
     */
    public String changeCapacity(String title, int capacity, String username, String rmNum){
        //check if event already exists
        if (!em.isEventExist(title)){
            return "Uh-oh! The event you have entered does not exist!";
        }else if (capacity > rm.getCapacity(rmNum)){
            return "Uh-oh! Either the new capacity you entered exceeds the room capacity for this event, or the room does not exist!";
        }else if (capacity < em.attendeeNum(title)){
            return "Uh-oh! The number of attendees already signed up for this event exceeds the new capacity entered! It's not nice to kick people out :))";
        }else{
            List<String> eventTitles = new ArrayList<>();
            eventTitles.add(title);
            String content = "ANNOUNCEMENT: One of the events you are participating in: " + eventTitles.get(0) +
                    " has been updated to allow " + capacity + " attendee(s)! ";
            ms.organizerMessageEventSpeakersAndAttendees(username, eventTitles, content, "");
            em.changeEventMaxNum(title, capacity);
            return "true";
        }
    }

}
