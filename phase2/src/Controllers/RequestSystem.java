package Controllers;

import UseCase.RequestManager;

public class RequestSystem {
    RequestManager rm;

    /**
     * Constructor for RequestSystem
     * @param rm the RequestManager for this execution of the program
     */
    public RequestSystem(RequestManager rm) {
        this.rm = rm;
    }

    public void sendRequest(String senderUsername, String content){
        rm.createRequest(senderUsername, content);
    }

    /**
     * Mark the request as addressed
     * @param requestNum the request number that a organizer want to marked addressed
     * @return true iff the request has been successfully marked as addressed
     */
    public boolean markedAsAddressed(int requestNum){
        if (!rm.isRequestNumValid(requestNum)){
            return false;
        }else{
            rm.markedAsAddressed(requestNum);
            return true;
        }
    }

    /**
     * Mark the request as pending
     * @param requestNum the request number that a organizer want to marked pending
     * @return true iff the request has been successfully marked as addressed
     */
    public boolean markedAsPending(int requestNum){
        if (!rm.isRequestNumValid(requestNum)){
            return false;
        }else{
            rm.markedAsPending(requestNum);
            return true;
        }
    }

    /**
     * Gets all requests possible to display;
     * @return   A list of requests
     */
    public String[] displayAllRequests() {
        return rm.allRequestInfo();
    }
}
