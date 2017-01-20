package Serwer;


import Serwer.Users.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Nogaz on 24.11.2016.
 */
public class Serwer {

    private String adresIPSerwera = "192.168.1.18";
    private String adresLocalhost = "127.0.0.1";
    int serverPortNumber = 12345;

    BufferedReader reader;
    ServerSocketChannel serverSocketChannel;

    ArrayList outputStreams;

    Selector channelSelector;

    private final int BUFFER_SIZE = 1024;
    private final String WELCOME_MESSAGE = "Welcome to PituPitu!\n";
    public final static String OPEN_CHANNEL_NAME = "OPEN_CHANNEL";
    private final ByteBuffer WelcomeMessageBuffer = ByteBuffer.wrap(WELCOME_MESSAGE.getBytes());


    private ByteBuffer messageBuffer = ByteBuffer.allocate(BUFFER_SIZE);

    private HashSet<User> users;


    public static void main(String[] args){

        new Serwer().setUpServer();
    }

    public void setUpServer(){
        outputStreams = new ArrayList();

        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind( new InetSocketAddress(serverPortNumber) );
            serverSocketChannel.configureBlocking(false);
            channelSelector  = Selector.open();
            serverSocketChannel.register(channelSelector, SelectionKey.OP_ACCEPT);

            users = new HashSet<User>();
            System.out.println("Users list created, contains: " + users.size() + " users");
            System.out.println("Server started on port: " + serverPortNumber);
/*
            SocketChannel openChannel = SocketChannel.open();
            openChannel.connect(new InetSocketAddress(adresIPSerwera, serverPortNumber));
            openChannel.configureBlocking(false);
            User newUser = new User();
            newUser.setNick("ADMIN");
            newUser.setIpAddress(adresIPSerwera + ":" + serverPortNumber);
            users.add(newUser);
            SelectionKey key = openChannel.register(channelSelector, SelectionKey.OP_READ);
*/
            startWorking();

            serverSocketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void addOpenChatChannel() throws IOException {
        SocketChannel newChannel = SocketChannel.open();

        newChannel.connect(new InetSocketAddress(adresIPSerwera,serverPortNumber));

        newChannel.configureBlocking(false);


        SelectionKey key = newChannel.register(channelSelector, SelectionKey.OP_READ );

    }
    public void addConversationChannel(String user1, String user2) throws IOException {
        SocketChannel newChannel = SocketChannel.open();

        newChannel.connect(new InetSocketAddress(adresIPSerwera,serverPortNumber));

        newChannel.configureBlocking(false);


        SelectionKey key = newChannel.register(channelSelector, SelectionKey.OP_READ);
        key.attach(new PrivateChatData(user1, user2));

    }

    public Channel getChannel(SelectionKey key){
        return key.channel();
    }
    public Selector getSelector(SelectionKey key){
        return key.selector();
    }
    public PrivateChatData getChatData(SelectionKey key){
        return (PrivateChatData)key.attachment();
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
        //System.out.println("User: " + newUser.getNick() + " added to Users list");
        System.out.println("Users list now contains " + users.size() + " users");

        //configure channel and wire with user
        socketChannel.configureBlocking(false);
        socketChannel.register(channelSelector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, newUser);

        //send welcome message and confirm connection
        socketChannel.write(WelcomeMessageBuffer);
        //System.out.println("Accepted connection from: " + newUser.toString());
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
        IncomingMessageHandler.handleIncomingMessage(message, key);
        //System.out.println("mM:" + message);
        //sendBroadCast(message);

        //jesli odczytana wiadomosc ma byc przeslana do innego uzytkownika to zapisujemy ja do kolejki wiadomosci odbiorcy

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
}
