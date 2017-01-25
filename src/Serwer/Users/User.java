package Serwer.Users;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.*;

/**
 * Created by Nogaz on 20.01.2017.
 */
public class User {
    private final int BUFFER_SIZE = 512;
    private String ipAddress;
    private String nick;
    private String username;
    private String realName;
    private String portNumber;
    private ArrayList<String> unreceivedMessageQueue;
    private ByteBuffer messageBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    private HashSet<String> channelsList;
    boolean welcomed = false;

    public User(){
        //unreceivedMessageQueue = new Queue<String>();
        channelsList = new HashSet<String>();
        unreceivedMessageQueue = new ArrayList<String>();
    }

    public void setNick(String nick){
        this.nick = nick;
    }
    public String getNick(){
        return this.nick;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public String getUsername(){
        return this.username;
    }
    public void setRealName(String realName){
        this.realName = realName;
    }
    public String getRealName(){
        return this.realName;
    }
    public void setIpAddress(String ipAddress){
        String[] data = ipAddress.split(":");

        this.ipAddress = data[0].replace("/", "");
        this.portNumber = data[1];

    }
    public String getIpAddress(){
        return this.ipAddress;
    }
    public String getPortNumber(){
        return this.portNumber;
    }
    public void addUnreceivedMessage(String message){
        this.unreceivedMessageQueue.add(message);
    }
    /*public String getUnreceivedMessage(){
        return this.unreceivedMessageQueue.element();
    }*/
    public void joinToChannel(String channelName){
        this.channelsList.add(channelName);
    }
    public boolean inChannel(String channelName){
        try{
            if( channelsList.contains(channelName) ){
                return true;
            }
        }catch (NullPointerException e){
        }
        return false;
    }

    @Override
    public String toString() {
        String string = "Nickname: " + nick + ", IP: " + ipAddress + ", port No: " + portNumber;
        return string;
    }
    public boolean remainMessage(){
        if( unreceivedMessageQueue.size() > 0 ){
            return true;
        }
        return false;
    }
    public String getRemainedMessage(){
        String message = unreceivedMessageQueue.get(0);
        unreceivedMessageQueue.remove(0);
        return message;

    }

    public static void sendMessage(SelectionKey key, String messageToSend){
        User keyUser = (User)key.attachment();
        SocketChannel channel = (SocketChannel)key.channel();
        ByteBuffer messageBuffer = ByteBuffer.wrap(messageToSend.getBytes());
        try {
            channel.write(messageBuffer);
            messageBuffer.rewind();
            StringBuilder sb = new StringBuilder();
            String message = new String(messageBuffer.array());
            System.out.println("Message: \"" + message + "\" sent");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public boolean welcomed(){
        return this.welcomed;
    }
    public void setWelcomed(){
        this.welcomed = true;
    }
    public boolean verified(){
        if( (this.username != null) && (this.nick != null) ){
            return true;
        }
        return false;
    }
}
