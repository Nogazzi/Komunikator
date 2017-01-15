package Serwer;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Nogaz on 13.01.2017.
 */
public class OpenChatData {
    private HashSet<ClientData> clientsList;
    private ArrayList<String> openConversation;

    public OpenChatData(){
        clientsList = new HashSet<ClientData>();
        openConversation = new ArrayList<String>();
    }
    public void addUser(ClientData clientData){
        clientsList.add(clientData);
    }
    public void removeUser(ClientData clientData){
        clientsList.remove(clientData);
    }
    public void appendConversationMessage(String message){
        openConversation.add(message);
    }

}
