package Serwer;

/**
 * Created by Nogaz on 13.01.2017.
 */
public class ClientData {
    String userName;
    String userIP;

    public ClientData(String userName, String userIP){
        this.userName = userName;
        this.userIP = userIP;
    }
    public String getUserName(){
        return this.userName;
    }
    public String getUserIP(){
        return this.userIP;
    }
}
