package Serwer;


import Serwer.Channels.IRCChannel;
import Serwer.Users.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

/**
 * Created by Nogaz on 24.11.2016.
 */
public class Serwer {

    private static Serwer serwerInstance = null;

    private String adresIPSerwera = "192.168.1.18";
    private String adresLocalhost = "127.0.0.1";
    int serverPortNumber = 12345;

    BufferedReader reader;
    ServerSocketChannel serverSocketChannel;

    Selector channelSelector;

    private final int BUFFER_SIZE = 1024;
    public final static String WELCOME_MESSAGE_BASE = "Welcome to the Internet Relay Network";
    public final static String OPEN_CHANNEL_NAME = "OpenChannel";
    private ByteBuffer WelcomeMessageBuffer;
    public static final String HOST_NAME = "PituPitu";

    IncomingMessageHandler incomingMessageHandler;


    private ByteBuffer messageBuffer = ByteBuffer.allocate(BUFFER_SIZE);

    private HashSet<User> users;
    private HashMap<String, IRCChannel> channels;

    private Serwer(){

    }
    public synchronized static Serwer getSerwerInstance(){
        if( serwerInstance == null ){
            serwerInstance = new Serwer();
        }
        return serwerInstance;
    }

    public void runSerwer(){
        incomingMessageHandler = new IncomingMessageHandler();

        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind( new InetSocketAddress(serverPortNumber) );
            serverSocketChannel.configureBlocking(false);
            channelSelector  = Selector.open();
            serverSocketChannel.register(channelSelector, SelectionKey.OP_ACCEPT);

            users = new HashSet<User>();
            channels = new HashMap<String, IRCChannel>();
            setUpOpenChannel();
            System.out.println("Users list created, contains: " + users.size() + " users");
            System.out.println("Server started on port: " + serverPortNumber);

            startWorking();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Channel getChannel(SelectionKey key){
        return key.channel();
    }
    public Selector getSelector(SelectionKey key){
        return key.selector();
    }
    public IRCChannel getIRCChannel(String name){
        return channels.get(name);
    }
    public HashSet<User> getUsers(){
        return this.users;
    }
    public HashMap<String, IRCChannel> getChannels(){
        return this.channels;
    }
    public void startWorking() throws IOException {
        boolean interrupted = false;
        while(!interrupted){

            int readyChannels = channelSelector.select();
            if( readyChannels <= 0 ) continue;

            Set<SelectionKey> selectionKeys = channelSelector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();

            while( keyIterator.hasNext() ){
                SelectionKey key = keyIterator.next();

                if( key.isAcceptable() ) {
                    handleAccept(key);
                }else if( key.isConnectable() ){
                    handleConnect(key);
                }else if( key.isReadable() ){
                    handleRead(key);
                }else if( key.isWritable() ){
                    handleWrite(key);
                }
                keyIterator.remove();
            }
        }
        serverSocketChannel.close();
    }



    public void handleAccept(SelectionKey key) throws IOException {
        //receive accept request
        SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
        String address = socketChannel.socket().getInetAddress() + ":" + socketChannel.socket().getPort();

        //create base client profile
        User newUser = new User();
        newUser.setIpAddress(address);
        users.add(newUser);
        setUserToOpenChannel(newUser);
        System.out.println("\nAccepted connection from " + address);
        System.out.println("Users list now contains " + users.size() + " users");

        //configure channel and wire with user
        socketChannel.configureBlocking(false);
        socketChannel.register(channelSelector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, newUser);

    }
    public void handleConnect(SelectionKey key){
    }
    public void handleRead(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        StringBuilder stringBuilder = new StringBuilder();
        User sender = null;
        if( key.attachment() instanceof User ){
            sender = (User)key.attachment();
            System.out.println("\nWiadomosc nadana przez: " + sender);
        }

        messageBuffer.clear();
        int read = 0;
        while( (read = socketChannel.read(messageBuffer)) > 0 ){
            messageBuffer.flip();
            byte[] bytes = new byte[messageBuffer.limit()];
            messageBuffer.get(bytes);
            stringBuilder.append(new String(bytes));
            messageBuffer.clear();
        }
        String message;
        if( read < 0 ){
            message = ((User)key.attachment()).getNick() + " left a chat\n";
            socketChannel.close();
        }else{
            message = stringBuilder.toString();
        }
        incomingMessageHandler.recognizeIncomingMessage(message, key);

    }

    private void sendBroadCast(String message) throws IOException {
        ByteBuffer messageByteBuffer = ByteBuffer.wrap(message.getBytes());
        for( SelectionKey key : channelSelector.keys() ){
            if( key.isValid() && (key.channel() instanceof SocketChannel) ){
                SocketChannel channel = (SocketChannel) key.channel();
                channel.write(messageByteBuffer);
                messageByteBuffer.rewind();
            }
        }
    }

    public void handleWrite(SelectionKey key){
        //wpisujemy do bufora kolejne bajty z kolejki wiadomosci rozpatrywanego odbiorcy
        User keyUser = (User)key.attachment();
        SocketChannel channel = (SocketChannel)key.channel();
        if( keyUser.remainMessage() ) {

            ByteBuffer messageBuffer = ByteBuffer.allocate( BUFFER_SIZE );
            messageBuffer.clear();
            messageBuffer.put( keyUser.getRemainedMessage().getBytes() );
            messageBuffer.flip();
            System.out.println("ilosc wolnego miejsca w buforze wysylania: " + messageBuffer.remaining() );
            try {
                channel.write(messageBuffer);
                String message = new String(messageBuffer.array());
                System.out.println("Message: \"" + message + "\" sent");
                messageBuffer.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void checkUsersActivity(){
        //pingujemy uzytkownika
        //jesli odpowiada to git, jesli nie to usuwamy go
    }
    public void setOneToOneCommunication(){

    }
    public void printUsersList(){
        //wypisujemy liste aktywnych uzytkownikow
    }
    public void setUserToOpenChannel(User user){
        user.joinToChannel(OPEN_CHANNEL_NAME);
    }
    public void quitUser(SelectionKey key){
        users.remove((User)key.attachment());
        key.cancel();
    }
    public void addUserToChannel(User user, String channel){
        if( channels.containsKey( channel )){
            user.joinToChannel(channel);
        }else{
            throw new NullPointerException();
        }
    }

    private void setUpOpenChannel(){
        this.channels.put(OPEN_CHANNEL_NAME, new IRCChannel(OPEN_CHANNEL_NAME));
    }
}
