package UseCase;

import Entities.Request;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Manage all the requests for the program. Calls methods in Request.
 * @author Xinyi Chen and Xinpeng Shan
 */
public class RequestManager implements Serializable {
    private final List<Request> allRequests;

    /**
     * Initialize a new RequestManager
     */
    public RequestManager(){
        allRequests = new ArrayList<>();
    }

    /**
     * Create Request object, add it to all Requests list
     *
     * @param senderUserName the sender's username
     * @param content the content of the request
     */
    public void createRequest(String senderUserName, String content){
        List<Integer> allRequestNum= allRequestNum();
        int newRequestNum = allRequestNum.size() + 1;
        Request request = new Request(newRequestNum, senderUserName, content);
        allRequests.add(request);
    }

    /**
     * Mark the request as addressed
     *
     * @param requestNum the request number of the request
     */
    public void markedAsAddressed(Integer requestNum){
        for(Request request: allRequests ){
            if(request.getRequestNum().equals(requestNum)){
                request.setStatusAddressed();
            }
        }
    }

    /**
     * Mark the request as pending
     *
     * @param requestNum the request number of the request
     */
    public void markedAsPending(Integer requestNum){
        for(Request request: allRequests ){
            if(request.getRequestNum().equals(requestNum)){
                request.setStatusPending();
            }
        }
    }

    /**
     * Return whether or not the given request number exists.
     *
     * @param requestNum the request number of the request
     * @return true iff the request number exists and is a natural number
     */
    public boolean isRequestNumValid(Integer requestNum){
        if (requestNum < 1 || requestNum > allRequests.size()){
            return false;
        }else{
            return true;
        }
    }

    /**
     * Get all request numbers
     *
     * @return a list of request numbers (where all request numbers are unique integers)
     */
    public List<Integer> allRequestNum() {
        List<Integer> requestNumList = new ArrayList<>();
        for(Request request: allRequests){
            requestNumList.add(request.getRequestNum());
        }
        return requestNumList;
    }

    /**
     * Private helper method to get the request info of a request
     *
     * @param request a Request
     * @return the string description of the request contains the request number, request content and request status
     */
    private String getRequestInfo(Request request){
        String status;
        if(request.getStatus()){
            status = "addressed";
        }else {status = "pending";}
        String requestContent = request.getContent();
        Integer requestNum = request.getRequestNum();
        String requestSender = request.getSenderUsername();
        return  "Request No." + requestNum + "(from: " + requestSender + ") : " + requestContent + " [ " + status + " ] ";
    }

    /**
     * Get all request info
     *
     * @return a Arraylist of request info of all requests
     */
    public String[] allRequestInfo(){
        List<String> requestInfo = new ArrayList<>();
        for(Request request : allRequests){
            requestInfo.add(getRequestInfo(request));
        }
        return requestInfo.toArray(new String[0]);
    }
}
