package Serwer;

import Serwer.Users.User;

import java.nio.channels.SelectionKey;

/**
 * Created by Nogaz on 13.01.2017.
 */
public class IncomingMessageHandler {

    public static final String USER = "USER";
    public static final String NICK = "NICK";
    public static final String SERVER = "server";
    public static final String SQUIT = "squit";
    public static final String QUIT = "quit";
    public static final String JOIN = "join";
    public static final String PART = "part";
    public static final String MODE = "mode";
    public static final String TOPIC = "topic";
    public static final String NAMES = "names";
    public static final String LIST = "list";
    public static final String INVITE = "invite";
    public static final String KICK = "kick";




    public static void handleIncomingMessage(String message, SelectionKey key){
        System.out.println("Full received message:\t" + message);

        String[] messageInParts = message.split(" ");
        switch( messageInParts[0].toUpperCase() ){
            case USER:
                System.out.println("Message header USER");
                System.out.println("Should change my username to: " + messageInParts[1].replace("\n", ""));
                handleUSERcommand(message, key);
                break;
            case NICK:
                System.out.println("Message header NICK");
                handleNICKcommand(messageInParts, key);
                break;
            case SERVER:
                System.out.println("Message header SERVER");
                break;
            case QUIT:
                System.out.println("Message header QUIT");
                handleQUITcommand(message, key);
                break;
            case SQUIT:
                System.out.println("Message header SQUIT");
                break;
            case JOIN:
                System.out.println("Message header JOIN");
                handleJOINcommand(message, key);
                break;
            case PART:
                System.out.println("Message header PART");
                break;
            case MODE:
                System.out.println("Message header MODE");
                break;
            case TOPIC:
                System.out.println("Message header TOPIC");
            case NAMES:
                System.out.println("Message header NAMES");
            case LIST:
                System.out.println("Message header LIST");
                break;
            case INVITE:
                System.out.println("Message header INVITE");
            case KICK:
                System.out.println("Message header KICK");
            default: System.out.println("Message header not recognised");
        }
    }

    public static void handleUSERcommand(String message, SelectionKey key){
        String[] messageInParts = message.split(" ");
        String newUsername = messageInParts[1].replace("\n", "");
        User sender = (User)key.attachment();
        System.out.println("Setting username to: " + newUsername);
        sender.setUsername(newUsername);


        messageInParts = message.split(":");
        String newRealName = messageInParts[1].replace("\n", "");
        System.out.println("Setting realname to: " + newRealName);
        sender.setRealName(newRealName);


    }

    public static void handleNICKcommand(String[] message, SelectionKey key){
        User sender = (User)key.attachment();
        String newNickname = message[1].replace("\n", "");
        System.out.println("Setting nickname to: " + newNickname);
        sender.setNick(newNickname);
    }

    public static void handleQUITcommand(String message, SelectionKey key){
        User sender = (User)key.attachment();
        //usunac usera z listy userów
        //usunac usera z listy kanalow -> key.cancel()
    }

    public static void handleJOINcommand(String message, SelectionKey key){

    }
}
