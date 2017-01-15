package Serwer;

import java.util.ArrayList;

/**
 * Created by Nogaz on 13.01.2017.
 */
public class PrivateChatData {
    ArrayList<String> conversation;
    String user1;
    String user2;

    public PrivateChatData(String user1, String user2){
        this.user1 = user1;
        this.user2 = user2;
        conversation = new ArrayList<String>();
    }
    public String[] getChatUsers(){
        return new String[]{this.user1, this.user2};
    }
}
