package GUI;

import java.util.List;
import java.util.ArrayList;

/**
 * Interface containing all methods needed for Dashboard classes to interact with TechConferenceSystem
 */
public interface Viewable {

    boolean createAttendeeButton(String username, String password);
    boolean createOrganizerButton(String username, String password);
    boolean createSpeakerButton(String username, String password);
    boolean loadConferenceButton(String filename);
    boolean confirmRoom(String roomNumber, int capacity);
    boolean includesImage(String currentUsername, int chatIndex, int messageIndex);
    boolean cancelEvent(String eventName, String username);
    boolean userIsVIP(String username);
    boolean markAddressed(int requestNumber);
    boolean markPending(int requestNumber);
    void addRequest(String username, String request);
    boolean saveProgram(String filename);
    int signUpForEvent(String username, String eventTitle);
    int cancelAttendEvent(String username, String eventTitle);
    ArrayList<String> viewChatNames(String username);
    List<String> getNewMessagesChatNames(String currentUsername);
    List<String> getNewMessagesTimestamp(String currentUsername);
    List<String[][]> getNewMessagesLast8Messages(String currentUsername);
    String[] displayAllEvents();
    String[] displaySignedUpEvents(String username);
    String LogInButton(String username, String password);
    String sendOneMsg(String sender, String recipient, String content, String imagePath);
    String[][] viewChat(int chatNumber, String username);
    String addFriend(String mainUsername, String newFriendUsername);
    String msgAllAttendees(String sender, String msg, String imagePath);
    String msgAllSpeakers(String sender, String msg, String imagePath);
    String msgAllAttendeeEvent(String sender, List<String> eventTitles, String msg, String imagePath);
    String deleteMsg(String currentUsername, int chatIndex, int messageIndex);
    String markChatAsUnread(String currentUsername, int chatIndex);
    String archiveChats(String currentUsername, int chatIndex);
    String changeCapacity(String eventName, int capacity, String username, String rmNum);
    String createSpeakerEvent(boolean VIP, String startDate, String endDate, String startTime, String endTime,
                              String roomNum, List<String> speakerUsernames, String eventTitle, int capacity);
    String createParty(boolean VIP, String startDate, String endDate, String startTime, String endTime, String roomNum,
                       List<String> speakerUsernames, String eventTitle, int capacity);
    String[] displayRequests();

}
