package UseCase;

import Entities.Event;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Manages all the events for the program. Calls methods in Event or Room or RoomManager.
 * @author Xinyi Chen and Xinpeng Shan
 */
public class EventManager implements Serializable {
    // The list allEvents including all events created including all parties, talks and panels.
    private final List<Event> allEvents;
    private final List<Event> allParties;
    private final List<Event> allTalks;
    private final List<Event> allPanels;



    /**
     * Initialize a new EventManager.
     */
    public EventManager() {
        allEvents = new ArrayList<>();
        allParties = new ArrayList<>();
        allTalks = new ArrayList<>();
        allPanels = new ArrayList<>();
    }

    /**
     * Create an Event object based on the parameters and add it into the list of talks/panels/parties accordingly.
     * @param VIP whether or not the event is VIP
     * @param title the title for the event
     * @param startDate the date for the event (YYYYMMDD)
     * @param endDate the date for the event (YYYYMMDD)
     * @param startTime the start time for the party (HH:mm:ss)
     * @param endTime the start time for the party (HH:mm:ss)
     * @param rmNum the room number for the party
     * @param maxNum the maximum number of people that can attend this event
     * @param speakerUserNames the list of speaker usernames for this event
     */
    public void createEvent(boolean VIP, String title, String startDate, String endDate, String startTime, String endTime,
                               String rmNum, int maxNum, List<String> speakerUserNames){
        List<LocalDateTime> time = parseStringToLocalDateTime(startDate, endDate, startTime, endTime);

        // Create an event object with the specification
        Event event = new Event(title, time.get(0), time.get(1), rmNum, VIP, maxNum, speakerUserNames);
        allEvents.add(event);
        //also need to add to corresponding list variables
        if (speakerUserNames.size() == 0) {
            allParties.add(event);
        } else if(speakerUserNames.size() == 1){
            allTalks.add(event);
        }else {
            allPanels.add(event);
        }
    }

    /**
     * Delete event from the corresponding list variable
     * @param title the title for the event
     */
    //delete event from the corresponding list variable
    public void deleteEvent(String title){
        Event event = helperEventTitle(title);
        String type = event.getEventType();
        switch (type) {
            case "Party":
                allParties.removeIf(e1 -> e1.getTitle().equals(title));
                break;
            case "Talk":
                allTalks.removeIf(e2 -> e2.getTitle().equals(title));
                break;
            case "Panel":
                allPanels.removeIf(e3 -> e3.getTitle().equals(title));
                break;
        }
        allEvents.removeIf(e -> e.getTitle().equals(title));
    }

    /**
     * Change event maxNum from the corresponding list variable
     * @param title the title for the event
     */
    public void changeEventMaxNum(String title, int maxNum){
        Event event = helperEventTitle(title);
        String type = event.getEventType();
        switch (type) {
            case "Party":
                for (Event e1 : allParties){
                    if (e1.getTitle().equals(title)){
                        e1.setMaxNum(maxNum);
                    }
                }
                break;
            case "Talk":
                for (Event e2 : allTalks){
                    if (e2.getTitle().equals(title)){
                        e2.setMaxNum(maxNum);
                    }
                }
                break;
            case "Panel":
                for (Event e3 : allPanels){
                    if (e3.getTitle().equals(title)){
                        e3.setMaxNum(maxNum);
                    }
                }
                break;
        }
    }

    /**
     * Returns a list of 2 LocalDateTime object representing the start time and end time of a potential event
     * @param startDate the date for the event (YYYYMMDD)
     * @param endDate the date for the event (YYYYMMDD)
     * @param startTime the start time for the event (HH:mm:ss)
     * @param endTime the start time for the event (HH:mm:ss)
     * @return a list of LocalDateTime objects parsed from the date and startTime parameters
     */
    public List<LocalDateTime> parseStringToLocalDateTime (String startDate, String endDate, String startTime, String endTime){
        //parse date to LocalDate format
        DateTimeFormatter formatter = DateTimeFormatter.BASIC_ISO_DATE;
        LocalDate localStartDate = LocalDate.parse(startDate, formatter);
        LocalDate localEndDate = LocalDate.parse(endDate, formatter);

        //parse startTime and endTime to LocalTime format (end time will be 1 hour later)
        LocalTime localStartTime = LocalTime.parse(startTime);
        LocalTime localEndTime = LocalTime.parse(endTime);

        //create LocalDateTime objects for start time and end time
        LocalDateTime sTime = LocalDateTime.of(localStartDate, localStartTime);
        LocalDateTime eTime = LocalDateTime.of(localEndDate, localEndTime);

        return Arrays.asList(sTime, eTime);
    }

    /**
     * Returns whether the given date is a valid date written in the form "YYYYMMDD"
     * @param date the date for the potential event (YYYYMMDD)
     * @return true iff the date is a valid date written in the correct format
     */
    public boolean parseStringToLocalDate(String date){
        //source https://www.baeldung.com/java-string-valid-date
        try {
            LocalDate.parse(date, DateTimeFormatter.BASIC_ISO_DATE);
        } catch (DateTimeParseException | NullPointerException e) {
            return false;
        }
        return true;
    }

    /**
     * Returns whether the given time is a valid time written in the form "HH:MM:SS"
     * @param time the time for the potential event (HH:MM:SS)
     * @return true iff the time is a valid time written in the correct format
     */
    public boolean parseStringToLocalTime(String time){
        //source https://www.baeldung.com/java-string-valid-date
        try {
            LocalTime.parse(time);
        } catch (DateTimeParseException | NullPointerException e) {
            return false;
        }
        return true;
    }


    /**
     * Returns if the given time frame is a valid time frame (start time < endtime)
     * @param startDate the date for the event (YYYYMMDD)
     * @param endDate the date for the event (YYYYMMDD)
     * @param startTime the start time for the event (HH:mm:ss)
     * @param endTime the start time for the event (HH:mm:ss)
     * @return true if this time frame is a valid time frame (start time < endtime)
     */
    public boolean isTimeValid(String startDate, String endDate, String startTime, String endTime){
        LocalDateTime start = parseStringToLocalDateTime(startDate, endDate, startTime, endTime).get(0);
        LocalDateTime end = parseStringToLocalDateTime(startDate, endDate, startTime, endTime).get(1);
        return end.isAfter(start);
    }

    /**
     * Returns if the given time intervals overlap.
     * @param start1 the LocalDateTime object for the start time of the first interval
     * @param end1 the LocalDateTime object for the end time of the first interval
     * @param start2 the LocalDateTime object for the start time of the second interval
     * @param end2 the LocalDateTime object for the end time of the second interval
     * @return true if the two time intervals overlap
     */
    public boolean doTimesOverlap(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2){
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    /**
     * Returns whether or not the given room is booked at the given date and time
     * @param roomNum the room number for the potential event
     * @param startDate the start date for the potential event (YYYYMMDD)
     * @param endDate the end date for the potential event (YYYYMMDD)
     * @param startTime the start time for the potential event (HH:mm:ss)
     * @param endTime the start time for the potential event (HH:mm:ss)
     * @return true iff the room given is not booked by another event at the same time
     */
    public boolean isRoomAvailableAtTime(String roomNum, String startDate, String endDate, String startTime, String endTime){
        LocalDateTime start = parseStringToLocalDateTime(startDate, endDate, startTime, endTime).get(0);
        LocalDateTime end = parseStringToLocalDateTime(startDate, endDate, startTime, endTime).get(1);
        //check if room is booked at the time
        for(Event e: allEvents){
            //lower and upper limits
            LocalDateTime lower = e.getStartTime();
            LocalDateTime upper = e.getEndTime();
            if (doTimesOverlap(lower, upper, start, end) && e.getRoomNum().equals(roomNum)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns whether or not the given speaker is already hosting an event at the given date and time
     * @param startDate the start date for the potential event (YYYYMMDD)
     * @param endDate the end date for the potential event (YYYYMMDD)
     * @param startTime the start time for the potential event (HH:mm:ss)
     * @param endTime the start time for the potential event (HH:mm:ss)
     * @param speakerUserName the username for the speaker of the event
     * @return true iff the speaker is not booked for any other event at give date and time
     */
    public boolean isSpeakerAvailableAtTime(String startDate, String endDate, String startTime, String endTime, String speakerUserName){
        LocalDateTime start = parseStringToLocalDateTime(startDate, endDate, startTime, endTime).get(0);
        LocalDateTime end = parseStringToLocalDateTime(startDate, endDate, startTime, endTime).get(1);
        //check if speaker is booked at the time
        for(Event event: allEvents){
            //lower and upper limits
            LocalDateTime lower = event.getStartTime();
            LocalDateTime upper = event.getEndTime();
            if (doTimesOverlap(lower, upper, start, end) && event.getSpeakerUserNames().contains(speakerUserName)){
                return false;
            }
        }
        return true;
    }

    /**
     * Returns whether or not the given event title is unique
     * @param title the date for the potential event (YYYYMMDD)
     * @return true iff the given event title has not been created before
     */
    public boolean isEventTitleUnique(String title){
        for (Event e: allEvents){
            if (e.getTitle().equals(title)){
                return false;
            }
        }
        return true;
    }

    /**
     * Returns whether or not the event exists
     * @param eventTitle the event title that we want to check
     * @return true iff the event with the corresponding event title is in the allEvents list
     */
    public boolean isEventExist(String eventTitle){
        for(Event event:allEvents){
            if (event.getTitle().equals(eventTitle)){return true;}
        }
        return false;
    }

    /**
     * Returns whether or not the username of this attendee is in attendeeList of this event
     * @param userName the username of the attendee that we want to check
     * @param eventTitle the event title that we want to check if the attendee is in
     * @return true iff the username of this attendee is in attendeeList of this event
     */
    public boolean isAttendeeAdded(String userName, String eventTitle){
        Event event = helperEventTitle(eventTitle);
        return event.getAttendeeList().contains(userName);
    }

    /**
     * Returns whether or not the the capacity of the event is full
     * @param eventTitle the event title that we want to check
     * @return true iff the event is in its full capacity
     */
    public boolean isEventFull(String eventTitle){

        Event event = helperEventTitle(eventTitle);
        int eventNum = event.getMaxNum();
        int currentNum = event.getAttendeeList().size();
        return eventNum <= currentNum;

    }

    /**
     * private helper method for finding corresponding Event base on eventTitle
     * Precondition: eventTitle correspond to a event in event List
     * @param eventTitle the eventTitle of the Event
     * @throws IllegalArgumentException if eventTitle does not correspond to any event in event List
     * @return the event that has this eventTitle
     */
    private Event helperEventTitle(String eventTitle) {
        assert isEventExist(eventTitle);
        for (Event event : allEvents) {
            //issue with allEvents; Try to create a event. and Sign up that event. The error occurs here;
            if (event.getTitle().equals(eventTitle)) {
                return event;
            }
        }
        throw new IllegalArgumentException("eventTitle does not correspond to any event in event List");

    }

    /**
     * get the number of attendee who have signed up for this event
     * @param eventTitle the event title that we want to check
     * @return the number of attendee currently, who have signed up for this event
     */
    public int attendeeNum(String eventTitle){
        Event event = helperEventTitle(eventTitle);
        return event.getAttendeeList().size();
    }


    /**
     * Add attendee to the attendeeList stored in Event
     * @param attendeeUserName the username of attendee that is added to the attendeeList
     * @param eventTitle the event that this attendee sign up to
     */
    public void addAttendee(String attendeeUserName, String eventTitle){
        Event event = helperEventTitle(eventTitle);
        List<String> currAttendee = event.getAttendeeList();
        currAttendee.add(attendeeUserName);
        event.setAttendeeList(currAttendee);
    }

    /**
     * Delete attendee from the attendeeList stored in Event
     * @param attendeeUserName the username of attendee that is is deleted from the attendeeList
     * @param eventTitle the event that this attendee want to cancel spot from
     */
    public void deleteAttendee(String attendeeUserName, String eventTitle){
        Event event = helperEventTitle(eventTitle);
        List<String> currAttendee = event.getAttendeeList();
        currAttendee.remove(attendeeUserName);
        event.setAttendeeList(currAttendee);
    }

    /**
     * Get a list of all the event titles that are scheduled
     * @return a list of all the event titles that are booked
     */
    public List<String> getAllEventTitle(){
        List<String> eventList = new ArrayList<>();
        for(Event event: allEvents){
            eventList.add(event.getTitle());
        }
        return eventList;
    }

    /**
     * Get a string representation for the given event title
     * @param eventTitle the title for the event
     * @return a string with the details about the event with given event title
     */
    public String getEventInfo(String eventTitle){
        Event event = helperEventTitle(eventTitle);
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT);
        String startTime = event.getStartTime().format(formatter);
        String endTime = event.getEndTime().format(formatter);
        StringBuilder speakers = new StringBuilder();
        for (String speaker: event.getSpeakerUserNames()){
                speakers.append(speaker).append(" ");
            }
        String roomNum = event.getRoomNum();
        String eventType = "( " + event.getEventType() + " )";
        if(event.getVIP() & event.getEventType().equals("Party"))
        {return eventTitle + eventType +"[VIP-only]"+ ": " + startTime + " - " + endTime + ", in Room " + roomNum +
                ".";}
        else if(event.getVIP() & !event.getEventType().equals("Party")){{return eventTitle + eventType +"[VIP-only]"+ ": " + startTime + " - " + endTime + ", in Room " + roomNum +
                ". Speaker: " + speakers;}}
        else if(!event.getVIP() & !event.getEventType().equals("Party")){return eventTitle + eventType + ": " + startTime + " - " + endTime + ", in Room " + roomNum +
                ". Speaker: " + speakers;}
        else{return eventTitle + eventType + ": " + startTime + " - " + endTime + ", in Room " + roomNum +
                ". ";}

    }

    /**
     * Get a list of all the usernames of attendees for the given event title
     * @return a list of all the attendee usernames for the given event title
     */
    public List<String> getAllAttendeesByTitle(String title){
        for(Event event: allEvents) {
            if (event.getTitle().equals(title)) {
                return event.getAttendeeList();
            }
        }
        throw new IllegalArgumentException("The given title does not correspond to any event in the event list.");
    }

    /**
     * Get speaker username for the given event title
     * @return the speaker username for the given event title
     */
    public List<String> getSpeakerUsernameByTitle(String eventTitle){
        Event event = helperEventTitle(eventTitle);
        return event.getSpeakerUserNames();
    }


    /**
     * Check whether the event is a VIP-only event.
     * @param eventTitle the event title of the event that you want to check
     * @return return true iff the this event exists and is a VIP-only event, otherwise, return false.
     */
    public boolean VIP(String eventTitle){
        if (isEventExist(eventTitle)){
            return helperEventTitle(eventTitle).getVIP();
        }
        return false;
    }
}
