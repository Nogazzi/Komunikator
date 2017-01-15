package Serwer;

import Klient.gui.ConversationPanel;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Nogaz on 13.01.2017.
 */
public class ConversationChannel {
    private String name;
    HashSet<String> clientList;
    public ConversationChannel(String name){
        this.name = name;
        clientList = new HashSet<String>();
    }
    public String getChannelName(){
        return this.name;
    }
}
