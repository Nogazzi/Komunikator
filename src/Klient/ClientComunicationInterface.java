package Klient;

/**
 * Created by Nogaz on 13.01.2017.
 */
public interface ClientComunicationInterface {
    public String registerNickname(String userNIck);
    public String createPrivateChat(String user1Nick, String user2Nick);
    public String chatMessage();
}
