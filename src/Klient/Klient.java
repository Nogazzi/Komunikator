package Klient;

import Klient.gui.ConversationPanel;
import Klient.gui.UsersListPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

/**
 * Created by Nogaz on 24.11.2016.
 */
public class Klient {

    JFrame ramka;

    String adresIPSerwera = "169.254.123.78";
    String adresLocalhost = "127.0.0.1";
    int portSerwera = 12345;

    JTextArea receivedMessages;
    JTextArea wiadomosc;


    PrintWriter writer;
    BufferedReader reader;
    Socket socket;

    SocketChannel socketChannel;
    ByteBuffer messageBuffer;

    String lastReceivedMessage = "";
    String lastSentMessage = "";
    public void startClient(){



        configureCommunication();
        setupGUI();
        Thread receiveNewMessages = new Thread(new MessagesReceiver());
        receiveNewMessages.start();

    }

    public void configureCommunication(){
        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress(adresLocalhost, portSerwera));

            messageBuffer = ByteBuffer.allocate(1024);

            while( ! socketChannel.finishConnect() ){

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*
        try{
            socket = new Socket(adresLocalhost, portSerwera);
            InputStreamReader readerStream = new InputStreamReader(socket.getInputStream());
            reader = new BufferedReader(readerStream);
            writer = new PrintWriter(socket.getOutputStream());
            System.out.println("Obsluga sieci gotowa do uzycia");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }




    public static void main(String[] args){

        new Klient().startClient();
    }

    private class MessagesReceiver implements Runnable {
        @Override
        public void run() {
            String wiadomosc;
            try{
                while( (wiadomosc = reader.readLine()) != null ){
                    System.out.println("Received: " + wiadomosc);
                    receivedMessages.append(wiadomosc + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public String readMessage(){
        return "";
    }

    public void setupGUI(){
        ramka = new JFrame("PituPitu");
        ramka.setSize(600,900);
        ramka.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JSplitPane panelAplikacji = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        //PRAWA     -->>    USERS LIST
        UsersListPanel usersListPanel = new UsersListPanel();

        //LEWA      -->>    KONWERSACJA
        JTabbedPane conversationsPanel = new JTabbedPane();

        ConversationPanel mainConversationPanel = new ConversationPanel();
        conversationsPanel.addTab("Main room", mainConversationPanel);

        ConversationPanel mainConversationPanel1 = new ConversationPanel();
        conversationsPanel.addTab("#1 room", mainConversationPanel1);



        panelAplikacji.add(conversationsPanel);
        panelAplikacji.add(usersListPanel);
        panelAplikacji.setLeftComponent(conversationsPanel);
        panelAplikacji.setRightComponent(usersListPanel);
        panelAplikacji.setDividerLocation(0.8);


        //podzielony kontener
        //GORA -->> konwersacja
        //DOL  -->> nadawanie wiadomosci
        /*
        JSplitPane panelGlowny = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        receivedMessages = new JTextArea(40,4);
        receivedMessages.setLineWrap(true);
        receivedMessages.setWrapStyleWord(true);
        receivedMessages.setEditable(false);
        JScrollPane conversationPanel = new JScrollPane(receivedMessages);
        conversationPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        conversationPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        conversationPanel.setAutoscrolls(true);


        wiadomosc = new JTextArea(3,40);
        wiadomosc.setLineWrap(true);
        JScrollPane sendMessageScroll = new JScrollPane(wiadomosc);
        sendMessageScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        sendMessageScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JButton wyslijButton = new JButton("Wyślij");
        wyslijButton.addActionListener(new wyslijButtonListener());
        JButton clearConversationButton = new JButton("Wyczyść");
        clearConversationButton.addActionListener(new ClearConversationButton());
        JButton exitConversationButton = new JButton("Wyjdź");
        exitConversationButton.addActionListener(new exitConversationListener());

        JPanel sendingPanel = new JPanel();

        sendingPanel.setLayout(new BoxLayout(sendingPanel, BoxLayout.Y_AXIS));
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(wyslijButton);
        buttonsPanel.add(clearConversationButton);
        buttonsPanel.add(exitConversationButton);
        sendingPanel.add(sendMessageScroll);
        sendingPanel.add(buttonsPanel);

        panelGlowny.setTopComponent(conversationPanel);
        panelGlowny.setBottomComponent(sendingPanel);
        panelGlowny.setDividerLocation(0.5);
*/
        ramka.getContentPane().add(panelAplikacji);
        ramka.setVisible(true);
    }

    public void clearChatWindow(){
        this.receivedMessages.setText("");
    }
    public String getLastReceivedMessage(){
        return this.lastReceivedMessage;
    }
    public String getLastSentMessage(){
        return this.lastSentMessage;
    }
    public void sendMessage(String message){
        this.writer.println(message);
        this.writer.flush();
    }
    public void clearSendMessagePanel(){
        wiadomosc.setText("");
        wiadomosc.requestFocus();
    }

}
