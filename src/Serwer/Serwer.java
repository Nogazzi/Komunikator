package Serwer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Nogaz on 24.11.2016.
 */
public class Serwer {

    int serverPortNumber = 12345;

    ArrayList outputStreams;
    public class ClientService implements Runnable{

        BufferedReader reader;
        Socket socket;

        public ClientService(Socket clientSocket){
            try{
                socket = clientSocket;
                InputStreamReader isReader = new InputStreamReader(socket.getInputStream());
                reader = new BufferedReader(isReader);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            String wiadomosc;
            try{
                while( (wiadomosc = reader.readLine()) != null ){
                    System.out.println("Received: " + wiadomosc);
                    sendToEveryone(wiadomosc);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args){
        new Serwer().doRoboty();
    }

    public void doRoboty(){
        outputStreams = new ArrayList();
        try{
            ServerSocket serverSocket = new ServerSocket(serverPortNumber);

            while(true){

                Socket clientSocket = serverSocket.accept();
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                outputStreams.add(writer);

                Thread t = new Thread(new ClientService(clientSocket));
                t.start();
                System.out.println("Mamy połączenie");
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
