package Controllers;

import GUI.Dashboard;
import GUI.Viewable;
import Gateways.Reader;
import Gateways.Writer;
import UseCase.ChatManager;
import UseCase.EventManager;
import UseCase.RoomManager;
import UseCase.UserManager;
import UseCase.RequestManager;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.lang.Long;

/**
 * Determines all the behaviour for the text-based UI
 * @author Joyce Huang, Peter Chen, and Amy Miao
 */
public class TechConferenceSystem implements Viewable{

    private LoginSystem loginSystem;
    private MessagingSystem messagingSystem;
    private SchedulingSystem schedulingSystem;
    private SignUpSystem signUpSystem;
    private RequestSystem requestSystem;
    private UserManager userManager;
    private ChatManager chatManager;
    private EventManager eventManager;
    private RoomManager roomManager;
    private RequestManager requestManager;


    /**
     * Constructor for the entire controller, begins the program
     * @param dashboard The GUI to set Techconference as an instance
     */
    public TechConferenceSystem(final Dashboard dashboard){
        dashboard.setView(this);
        createProgram();
    }

    /**
     * A method that determines if an attendee account can be successfully created or not.
     *
     * @param username  The username the user wants to create
     * @param password  The Password the user wants for their account
     * @return          True if Attendee account successfully created. False Otherwise
     */
    public boolean createAttendeeButton(String username, String password){
        return userManager.createAttendeeAccount(username, password);
    }

    /**
     * A method that determines if an organizer account can be successfully created or not.
     *
     * @param username  The username the user wants to create
     * @param password  The Password the user wants for their account
     * @return          True if Organizer account successfully created. False Otherwise
     */
    public boolean createOrganizerButton(String username, String password){
        return userManager.createOrganizerAccount(username, password);
    }

    /**
     * A method that determines if a Speaker account can be successfully created or not.
     *
     * @param username  The username the user wants to create
     * @param password  The Password the user wants for their account
     * @return          True if Speaker account successfully created. False Otherwise
     */
    public boolean createSpeakerButton(String username, String password){
        return userManager.createSpeakerAccount(username, password);
    }


    /**
     * A method that loads an existing conference; Method runs when the user clicks the "load conference" button
     * @param filename The file name to load the conference
     * @return true if successfully loaded the conference, false otherwise
     */
    public boolean loadConferenceButton(String filename){
        Reader reader = new Reader();
        if (reader.verifySaves(filename)) {
            Object[] loadedObjects = reader.loadData(filename);
            chatManager = (ChatManager) loadedObjects[0];
            eventManager = (EventManager) loadedObjects[1];
            roomManager = (RoomManager) loadedObjects[2];
            userManager = (UserManager) loadedObjects[3];
            requestManager = (RequestManager) loadedObjects[4];
        } else {
            return false;
        }
        initializeManagers();
        return true;
    }


    /**
     *
     * @param username   Username that the user wants to login with
     * @param password   Password that the user wants to login with
     * @return          False if username is already in database or invalid. True otherwise.
     */
    public String LogInButton(String username, String password){
        String userType = userManager.userType(username);
        if (userType.equals("Invalid Username")){
            return "false";
        }else{
            if (loginSystem.verifyLogin(username, password)){
                return loginSystem.verifyUserType(username);
            }else{
                return "false";
            }
        }
    }


//--------------------------------------------Messaging Buttons-----------------------------------------

    /**
     * This method is for sending the info of ALL chat usernames to display on GUI. Ex. [1.kailas, 2.william]
     * @param currentUsername The username of the current user
     * @return A list of all the chat names
     */
    @Override
    public ArrayList <String> viewChatNames(String currentUsername){
        ArrayList<String> output = new ArrayList<>();

        List<UUID> chats = messagingSystem.getCurrentChats(currentUsername);
        int index = 1;
        for (UUID chatId: chats) {
            String outputString = index + ". ";
            index ++;

            String chatName = getChatNameByUser(currentUsername, chatId);
            outputString += chatName;
            output.add(outputString);
        }
        return output;
    }

    private String getChatNameByUser(String username, UUID chatId){
        String chatName = messagingSystem.getChatName(chatId);
        List<String> chatMembers = messagingSystem.getChatMembers(chatId);
        if (chatMembers.size() == 2){   //chat name cannot be custom in 2 person chats
            List<String> membersCopy = new ArrayList<>(chatMembers);
            membersCopy.remove(username);
            chatName = membersCopy.get(0);
        }
        return chatName;
    }

    /**
     * This method sends all the info about the chat messages to display on the GUI. (The chat is determined by receiving an index from 1 to the total number of chats)
     * @param chatNumber The chat number that the user wants to view
     * @param currentUsername The username of the current user
     * @return           Return String that the User wants to view; IN VIEW CHAT NUMBER: Return null if can not find
     *                   chat number;
     *                   The String Array will be in the form [[senderUsername, content, timestamp],....]
     */
    @Override
    public String[][] viewChat(int chatNumber, String currentUsername){
        List<UUID> userChats = messagingSystem.getCurrentChats(currentUsername);
        List<List<String>> messageInfoList = new ArrayList<>();

        if (chatNumber >= 0 && chatNumber < userChats.size()){
            UUID chatId = userChats.get(chatNumber);
            List<UUID> chatMessages = messagingSystem.viewChatMessages(currentUsername, chatId);
            for (UUID messageId: chatMessages){
                List<String> currentMessageInfo = new ArrayList<>(getMessageInfo(chatId, messageId));
                messageInfoList.add(currentMessageInfo); //add a list of message info for this message
            }

            String[][] messageInfoArray = new String[messageInfoList.size()][];
            for (int i = 0; i<messageInfoList.size(); i++){
                List<String> nestedMessageInfo = messageInfoList.get(i);
                messageInfoArray[i] = nestedMessageInfo.toArray(new String[0]); //might have a bug idk
            }
            return messageInfoArray;
        }else{
            return null;
        }

    }

    private List<String> getMessageInfo(UUID chatId, UUID messageId){
        String senderUsername = messagingSystem.getMessageSender(chatId, messageId);
        LocalDateTime timeStamp = messagingSystem.getMessageTimestamp(chatId, messageId);
        String content = messagingSystem.getMessageContent(chatId, messageId);
        String imageString = messagingSystem.getMessageImageString(chatId, messageId);

        // convert datetime to string
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a,    MMM d");    //format time
        String formattedTimestamp = timeStamp.format(formatter);

        if (chatManager.hasImage(chatId, messageId)) {
            return new ArrayList<>(Arrays.asList(senderUsername, content, formattedTimestamp, imageString));
        } else {
            return new ArrayList<>(Arrays.asList(senderUsername, content, formattedTimestamp));
        }
    }

    /**
     * Message all Attendees
     * @param sender The username of the sender
     * @param msg message content
     * @param imagePath image path
     * @return Null if the message was successfully sent, or an error message otherwise
     */
    @Override
    public String msgAllAttendees(String sender, String msg, String imagePath) {
        return messagingSystem.organizerMessageAllAttendees(sender, msg, imagePath);
    }

    /**
     * Message all Speakers
     * @param sender The username of the sender
     * @param msg message content
     * @param imagePath image path
     * @return Null if the message was successfully sent, or an error message otherwise
     */
    @Override
    public String msgAllSpeakers(String sender, String msg, String imagePath) {
        return messagingSystem.organizerMessageAllSpeakers(sender, msg, imagePath);
    }

    /**
     * Message all attendees
     * @param sender The username of the sender
     * @param eventTitles the list of event titles
     * @param msg message content
     * @param imagePath image path
     * @return Null if the message was successfully sent, or an error message otherwise
     */
    @Override
    public String msgAllAttendeeEvent(String sender, List<String> eventTitles, String msg, String imagePath){
        return messagingSystem.speakerMessageEventAttendees(sender, eventTitles, msg, imagePath);
    }

    /**
     * Send a message to one user
     * @param sender The username of the sender
     * @param recipient    The username this user wants to msg
     * @param content     The text this username wants to send
     * @return Null if the message was sent successfully or an Error message otherwise
     */
    @Override
    public String sendOneMsg(String sender, String recipient, String content, String imagePath) {
        return messagingSystem.messageOneUser(sender, recipient, content, imagePath);
    }

    /**
     * Delete a message from the chat
     * PRECONDITION : messageIdString wasn't used to call deleteMsg before
     * @param currentUsername the username of the current user
     * @param chatIndex The index of the chat
     * @param messageIndex the index of the message
     * @return Null if the message was deleted properly. An error message otherwise.
     */
    @Override
    public String deleteMsg(String currentUsername, int chatIndex, int messageIndex){
        UUID chatId = messagingSystem.getCurrentChats(currentUsername).get(chatIndex);
        UUID messageId = messagingSystem.getMessageByIndex(chatId, messageIndex);
        return messagingSystem.deleteUserMessage(currentUsername, chatId, messageId);
    }

    /**
     * Mark chat as unread.
     * @param currentUsername The username of the current user
     * @param chatIndex The index of the chat
     * @return Null if the chat was marked as unread. An error message otherwise.
     */
    @Override
    public String markChatAsUnread(String currentUsername, int chatIndex){
        UUID chatId = messagingSystem.getCurrentChats(currentUsername).get(chatIndex);
        return messagingSystem.markUserChatAsUnread(currentUsername, chatId);
    }

    /**
     * Archive chat.
     * @param currentUsername The username of the current user
     * @param chatIndex The index of the chat
     * @return Null if the chat was archived. An error message otherwise.
     */
    @Override
    public String archiveChats(String currentUsername, int chatIndex){
        UUID chatId = messagingSystem.getCurrentChats(currentUsername).get(chatIndex);
        return messagingSystem.archiveUserChat(currentUsername, chatId);
    }

    /**
     * Check if the message includes an image
     * @param currentUsername The current username of someone who wants to send a image message
     * @param chatIndex The index of the chat
     * @param messageIndex The index of the message
     * @return Boolean representing whether the message includes an image
     */
    @Override
    public boolean includesImage(String currentUsername, int chatIndex, int messageIndex) {
        UUID chatId = messagingSystem.getCurrentChats(currentUsername).get(chatIndex);
        UUID messageId = messagingSystem.getMessageByIndex(chatId, messageIndex);
        return messagingSystem.doesMessageHaveImage(chatId, messageId);
    }

    /**
     * Get the chat names of all chats with new messages. Note: list elements correspond to the values of the same index in the lists returned from the other newMessages methods.
     * @param currentUsername The username of the current user
     * @return An ordered list of all the chat names with new messages
     */
    @Override
    public List<String> getNewMessagesChatNames(String currentUsername){
        Map<UUID, List<UUID>> newMessages =  messagingSystem.viewAllNewMessages(currentUsername, true);
        List<String> newMessagesChatNames = new ArrayList<>();

        for (Map.Entry<UUID, List<UUID>> mapItem : newMessages.entrySet()){
            UUID chatId = mapItem.getKey();
            String chatName = getChatNameByUser(currentUsername, chatId);
            newMessagesChatNames.add(chatName);
        }
        return newMessagesChatNames;
    }

    /**
     * Get the timestamps of the chats with new messages. Note: List elements correspond to the values of the same index in the lists returned from the other newMessages methods.
     * @param currentUsername The username of the current user
     * @return An ordered list of the chat timestamps
     */
    @Override
    public List<String> getNewMessagesTimestamp(String currentUsername){
        Map<UUID, List<UUID>> newMessages =  messagingSystem.viewAllNewMessages(currentUsername, true);
        List<String> newMessagesTimestamps = new ArrayList<>();

        for (Map.Entry<UUID, List<UUID>> mapItem : newMessages.entrySet()){
            UUID chatId = mapItem.getKey();
            List<UUID> messageIds = mapItem.getValue();

            //showing time difference from now and last message
            LocalDateTime lastMessageTime = messagingSystem.getMessageTimestamp(chatId, messageIds.get(messageIds.size() - 1));
            Duration timeDifference = Duration.between(lastMessageTime, LocalDateTime.now());
            newMessagesTimestamps.add(Long.toString(timeDifference.toMinutes()));
        }
        return newMessagesTimestamps;
    }

    /**
     * Get the last 8 new messages for every chat. Note: List elements correspond to the values of the same index in the lists returned from the other newMessages methods.
     * @param currentUsername The username of the current user
     * @return A list of 2d string arrays. Each string array is for a different chat. Each string array will be in the form [[senderUsername, content],....].
     */
    @Override
    public List<String[][]> getNewMessagesLast8Messages(String currentUsername){
        Map<UUID, List<UUID>> newMessages =  messagingSystem.viewAllNewMessages(currentUsername,false);
        List<String[][]> newMessagesLast8 = new ArrayList<>();

        for (Map.Entry<UUID, List<UUID>> mapItem : newMessages.entrySet()){
            UUID chatId = mapItem.getKey();
            List<UUID> messageIds = mapItem.getValue();  //list will never be empty because of messagingSystem

            //showing last eight messages
            List<UUID> last8Messages = messageIds.subList(messageIds.size()- Math.min(messageIds.size(), 8), messageIds.size());
            String[][] chatNew8Messages = new String[last8Messages.size()][2]; //stored as [[senderUsername, content],......]
            for (int i= 0; i< last8Messages.size(); i++){
                chatNew8Messages[i][0] = messagingSystem.getMessageSender(chatId,last8Messages.get(i)); //set message sender
                chatNew8Messages[i][1] = messagingSystem.getMessageContent(chatId,last8Messages.get(i)); // set message content
            }
            newMessagesLast8.add(chatNew8Messages); // add this chat's new messages to list
        }
        return newMessagesLast8;
    }

    /**
     * Add a user as friend. Friends are allowed to message each other.
     * @param mainUsername The username of the current user
     * @param newFriendUsername The username this user wants to add
     * @return Null if successfully added. An error message if adding the friend was unsuccessful.
     */
    @Override
    public String addFriend(String mainUsername, String newFriendUsername){
        return messagingSystem.addPeopleToMessage(mainUsername, newFriendUsername);
    }


//-----------------------------------------Scheduling Buttons-------------------------------------------

    /**
     * Perform necessary checks & operations for cancelling an event.
     * @param   title the event name entered
     * @param   username the username of the organizer that chose to cancel this event
     * @return  true if successfully canceled event, False Otherwise.
     */
    @Override
    public boolean cancelEvent(String title, String username){
        return schedulingSystem.cancelEvent(title, username);
    }


    /**
     * Perform necessary checks & operations for changing the capacity of an event.
     * @param eventName   String event name to change capacity
     * @param capacity    capacity to change to
     * @param username    the username of the organizer that chose to change the capacity of this event
     * @param rmNum       the room number for this event
     * @return            'true' if successfully changed. Otherwise it will return an error msg
     */
    @Override
    public String changeCapacity(String eventName, int capacity, String username, String rmNum){
        return schedulingSystem.changeCapacity(eventName, capacity, username, rmNum);
    }

    /**
     * Method to add a room and capacity to the system.
     * @param roomNumber  String room number the user want it to be
     * @param capacity    int capacity of event
     * @return            returns String 'true' if successfully changed. otherwise it will return an error msg.
     */
    public boolean confirmRoom(String roomNumber, int capacity){
        return schedulingSystem.addRoom(roomNumber, capacity);
    }


    /**
     * Check if the conditions for adding the given one speaker/ multi speaker event is satisfied and return error messages accordingly.
     * If satisfied, create new event, update speaker's list of events, and print success message.
     * @param VIP whether the event is of type VIP
     * @param startDate the start date for the potential event (YYYYMMDD)
     * @param endDate the end date for the potential event (YYYYMMDD)
     * @param startTime the start time for the event (HH:mm:ss)
     * @param endTime the end time for the event (HH:mm:ss)
     * @param roomNum the room number for the event
     * @param speakerUsernames the names of the speakers for the event
     * @param eventTitle the title for the event
     * @param capacity the maximum/capacity of people that can attend this event
     * @return the error message according to the error or "true" if event successfully created
     */
    public String createSpeakerEvent(boolean VIP, String startDate, String endDate, String startTime, String endTime, String roomNum, List<String>
            speakerUsernames, String eventTitle, int capacity){
        return schedulingSystem.helper_addSpeakerEvent(VIP, startDate, endDate, startTime, endTime, roomNum, speakerUsernames, eventTitle, capacity);
    }

    /**
     * Check if the conditions for adding the given no speaker event is satisfied and return error messages accordingly.
     * If satisfied, create new event, update speaker's list of events, and print success message.
     * @param VIP whether the event is of type VIP
     * @param startDate the start date for the potential event (YYYYMMDD)
     * @param endDate the end date for the potential event (YYYYMMDD)
     * @param startTime the start time for the event (HH:mm:ss)
     * @param endTime the end time for the event (HH:mm:ss)
     * @param roomNum the room number for the event
     * @param speakerUsernames the names of the speakers for the event (SHOULD BE JUST EMPTY)
     * @param eventTitle the title for the event
     * @param capacity the maximum/capacity of people that can attend this event
     * @return the error message according to the error or "true" if event successfully created
     */
    public String createParty(boolean VIP, String startDate, String endDate, String startTime, String endTime, String roomNum, List<String>
            speakerUsernames, String eventTitle, int capacity){
        return schedulingSystem.helper_addParty(VIP, startDate, endDate, startTime, endTime, roomNum, speakerUsernames, eventTitle, capacity);
    }

//--------------------------------------------Sign Up Buttons-----------------------------------------


    /**
     * Method to send all possible events to display onto GUI
     * @return   A string list of events
     */
    public String[] displayAllEvents() {
        return signUpSystem.displayAllEvents();
    }

    /**
     * Method to send all events that this username has signed up for to display onto GUI
     * @param username   This username's event that has already been signed up for
     * @return          A string list of events
     */
    public String[] displaySignedUpEvents(String username) {
        return signUpSystem.displaySignedUpEvents(username);
    }

    /**  Method for user to sign up for event
     *
     * @param username     The username of someone signing up for the event
     * @param eventTitle   The title of the event to sign up for
     * @return      an integer based on what error occurs.
     */
    public int signUpForEvent(String username, String eventTitle) {
        return signUpSystem.signUpEvent(username, eventTitle);
    }

    /**
     *
     * @param username  The username that someone wants to cancel their event by
     * @param eventTitle   The title of event the username wants to cancel for
     * @return     an integer based on what error occurs(or not) when they try to cancel
     */
    public int cancelAttendEvent(String username, String eventTitle) {
        return signUpSystem.cancelSpotEvent(username, eventTitle);
    }

    //--------------------------------------------Request Buttons-----------------------------------------

    /**
     * A method to add user request into the system
     * @param username  The username that wants to create the request
     * @param request   A string containing what request the user wants
     */
    public void addRequest(String username, String request) {
        requestSystem.sendRequest(username, request);
    }

    /**
     * A method that marks the request if it has been addressed or not
     * @param requestNumber    The requestNumber integer to see if the request has been addressed or not
     * @return                 true if addressed, false otherwise.
     */
    public boolean markAddressed(int requestNumber) {
        return requestSystem.markedAsAddressed(requestNumber);
    }

    /**
     * A method that marks if the request is pending or not
     * @param requestNumber    The requestNumber integer to see if the request is pending or not
     * @return              true if it is marked as pending, false otherwise
     */
    public boolean markPending(int requestNumber) {
        return requestSystem.markedAsPending(requestNumber);
    }

    /**
     * A method that displays all requests (sends the information neccessary to GUI)
     * @return  A list of string to be displayed onto GUI.
     */
    public String[] displayRequests() {
        return requestSystem.displayAllRequests();
    }

    //--------------------------------------------Creating Controller-----------------------------------------
    private void createProgram() {
        chatManager = new ChatManager();
        eventManager = new EventManager();
        roomManager = new RoomManager();
        userManager = new UserManager();
        requestManager = new RequestManager();
        initializeManagers();
    }

    private void initializeManagers() {
        loginSystem = new LoginSystem(userManager);
        messagingSystem = new MessagingSystem(chatManager, userManager, eventManager);
        schedulingSystem = new SchedulingSystem(eventManager, roomManager, userManager, messagingSystem);
        signUpSystem = new SignUpSystem(eventManager, userManager, roomManager);
        requestSystem = new RequestSystem(requestManager);
    }

    /**
     * A method to save all the information.
     * @return whether or not the save was successful
     * @param filename   file name to save the program
     */
    public boolean saveProgram(String filename) {
        Writer writer = new Writer();
        Object[] saveObjects = new Object[5];
        saveObjects[0] = chatManager;
        saveObjects[1] = eventManager;
        saveObjects[2] = roomManager;
        saveObjects[3] = userManager;
        saveObjects[4] = requestManager;
        return writer.writeToFile(filename, saveObjects);
    }

    /**
     * Method to determine if user is VIP or not
     * @param username    To check if this username is VIP
     * @return            true if user is VIP, false otherwise.
     */
    public boolean userIsVIP(String username) {
        return signUpSystem.userIsVIP(username);
    }
}
