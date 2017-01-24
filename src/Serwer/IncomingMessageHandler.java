package Serwer;

import Serwer.Users.User;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import static Serwer.Serwer.OPEN_CHANNEL_NAME;

/**
 * Created by Nogaz on 13.01.2017.
 */
public class IncomingMessageHandler {

    public static final String USER = "USER";
    public static final String NICK = "NICK";
    public static final String SERVER = "SERVER";
    public static final String SQUIT = "SQUIT";
    public static final String QUIT = "QUIT";
    public static final String JOIN = "JOIN";
    public static final String PART = "PART";
    public static final String MODE = "MODE";
    public static final String TOPIC = "TOPIC";
    public static final String NAMES = "NAMES";
    public static final String LIST = "LIST";
    public static final String INVITE = "INVITE";
    public static final String KICK = "KICK";
    public static final String PRIVMSG = "PRIVMSG";




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
                break;
            case NAMES:
                System.out.println("Message header NAMES");
                break;
            case LIST:
                System.out.println("Message header LIST");
                break;
            case INVITE:
                System.out.println("Message header INVITE");
                break;
            case KICK:
                System.out.println("Message header KICK");
                break;
            case PRIVMSG:
                break;
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
        handleWelcomeMessage(key);

    }

    public static void handleNICKcommand(String[] message, SelectionKey key){

        User sender = (User)key.attachment();
        String newNickname = message[1].replace("\n", "");
        newNickname = newNickname.replace("\r", "");
        System.out.println("Setting nickname to: " + newNickname);
        if( sender.getNick() != null ){
            String returnedMessage = ":" + sender.getNick() + " NICK " + newNickname + "\r\n";
            System.out.println("returned message: " + returnedMessage);
            sender.addUnreceivedMessage(returnedMessage);
        }
        sender.setNick(newNickname);
        System.out.println("Ustawilem nick na wartosc: " + sender.getNick());

        sender.joinToChannel(OPEN_CHANNEL_NAME);

        String joinChannelMessage = ":" + sender.getNick() + " JOIN #" + OPEN_CHANNEL_NAME + "\r\n";
        System.out.println("1st message sent:" + joinChannelMessage);
        sender.addUnreceivedMessage( joinChannelMessage );
        System.out.println("1st message sent:" + joinChannelMessage);
        handleWelcomeMessage(key);
    }

    public static void handleQUITcommand(String message, SelectionKey key){
        User sender = (User)key.attachment();
        //usunac usera z listy userów
        //usunac usera z listy kanalow -> key.cancel()
    }

    public static void handleJOINcommand(String message, SelectionKey key){

    }
    public static void handleJOINOpenChannelCommand(String message, SelectionKey key){

    }
    public static void handleWelcomeMessage(SelectionKey key){
        User sender = (User)key.attachment();
        if( !sender.welcomed() && (sender.getNick() != null) && (sender.getUsername() != null) ){
            String welcomeMessage = Serwer.WELCOME_MESSAGE_BASE;
            welcomeMessage += " " + sender.getNick() + "!" + sender.getUsername() + "@" + Serwer.HOST_NAME + "\r\n";
            sender.addUnreceivedMessage(welcomeMessage);
            sender.setWelcomed();
        }

    }
}
