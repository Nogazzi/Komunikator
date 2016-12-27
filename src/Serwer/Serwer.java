package Serwer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Nogaz on 24.11.2016.
 */
public class Serwer {

    int serverPortNumber = 12345;

    BufferedReader reader;
    ServerSocketChannel serverSocketChannel;

    ArrayList outputStreams;


    public static void main(String[] args){
        new Serwer().setUpServer();
    }

    public void setUpServer(){
        outputStreams = new ArrayList();

        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind( new InetSocketAddress(serverPortNumber) );
            serverSocketChannel.configureBlocking(false);

            while( true ){
                serverSocketChannel.accept();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendToEveryone(String message){
        Iterator it = outputStreams.iterator();
        while( it.hasNext() ){
            try{
                PrintWriter writer = (PrintWriter) it.next();
                writer.println(message);
                writer.flush();
            }catch( Exception ex){
                ex.printStackTrace();
            }
        }
    }
}
