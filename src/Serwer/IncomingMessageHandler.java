package Serwer;

import Serwer.Channels.IRCChannel;
import Serwer.Users.User;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;

import static Serwer.Serwer.OPEN_CHANNEL_NAME;

/**
 * Created by Nogaz on 13.01.2017.
 */
public class IncomingMessageHandler {

    Serwer serwerInstance;

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


    public IncomingMessageHandler(){
        serwerInstance = Serwer.getSerwerInstance();
    }


    public void recognizeIncomingMessage(String message, SelectionKey key){
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
                handleMODEcommand(message, key);
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
                System.out.println("Message header PRIVMSG");
                handlePRIVMSGcommand(message, key);
                break;
            default: System.out.println("Message header not recognised");
        }
        handleVerified(key);
    }

    public void handlePRIVMSGcommand(String message, SelectionKey key){
        User user = (User)key.attachment();
        String[] mainBody = message.split(":");
        String bodyMessage = mainBody[1];
        String[] headMessage = mainBody[0].split(" ");
        String channelName = headMessage[1];
        System.out.println("Tresc nadesłanej wiadomosci: " + bodyMessage);
        System.out.println("Kanał nadawczy wiadomości: " + channelName);
        channelName = channelName.replace("#", "");
        bodyMessage = PRIVMSG + " " + user.getNick() + " :" + bodyMessage + "\r\n";
        serwerInstance.getIRCChannel(channelName).sendMessageToAllUsersOnChannel(message);

    }

    public void handleUSERcommand(String message, SelectionKey key){
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

    public void handleNICKcommand(String[] message, SelectionKey key){

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


    }
    public void handleMODEcommand(String message, SelectionKey key){
        User user = (User) key.attachment();

        user.addUnreceivedMessage("ERR_NEEDMOREPARAMS" + "\r\n");
    }

    public void handleQUITcommand(String message, SelectionKey key){
        User sender = (User)key.attachment();
        key.cancel();
    }

    public void handleJOINcommand(String message, SelectionKey key){
        User user = (User)key.attachment();
        String[] str = message.split(" ");
        String channelName = str[1].replace("#", "");
        System.out.println("Uzytkownik " + user.getNick() + " chce dolaczyc do kanalu " + channelName);
        serwerInstance.addUserToChannel(user, channelName);
        user.addUnreceivedMessage(":" + user.getNick() + " JOIN #" + channelName + "\r\n");
    }
    public void handleJOINOpenChannelCommand(String message, SelectionKey key){

    }
    public void handleVerified(SelectionKey key){
        //if verified 1st time
        //  - send welcome message
        //  - join to openChannel
        //  - send channels list
        //  - send users list
        User user = (User)key.attachment();
        if( user.verified() && !user.welcomed() ){

            //send welcome message
            String welcomeMessage = Serwer.WELCOME_MESSAGE_BASE;
            welcomeMessage += " " + user.getNick() + "!" + user.getUsername() + "@" + Serwer.HOST_NAME + "\r\n";
            user.addUnreceivedMessage(welcomeMessage);
            //join to open channel
            user.joinToChannel(OPEN_CHANNEL_NAME);
            serwerInstance.getIRCChannel(OPEN_CHANNEL_NAME).addUser(user);
            String joinChannelMessage = ":" + user.getNick() + " JOIN #" + OPEN_CHANNEL_NAME + "\r\n";
            user.addUnreceivedMessage( joinChannelMessage );
            //send channels list
            String channelsList = "LIST " + getChannelsList() + "\r\n";
            user.addUnreceivedMessage(channelsList);
            //send users list
            String usersListMessage = "NAMES " + getUsersList() + "\r\n";
            user.addUnreceivedMessage(usersListMessage);
            user.setWelcomed();
        }
    }
    public String getUsersList(){
        String users = "";
        for (User user: serwerInstance.getUsers() ) {
            users += user.getNick() + " ";
        }
        return users;
    }
    public String getChannelsList(){
        String channels = "";
        HashMap<String, IRCChannel> channelsList = serwerInstance.getChannels();
        Iterator it = channelsList.entrySet().iterator();
        while( it.hasNext() ){
            HashMap.Entry pair = (HashMap.Entry)it.next();
            channels += pair.getKey().toString();
            if( it.hasNext() ){
                channels += ", ";
            }
            //it.remove();
        }
        return channels;
    }
    public void sendToEveryOneOnChannel(String message, IRCChannel channel){

    }

}
