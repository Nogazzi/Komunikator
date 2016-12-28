package Klient;

import Klient.gui.ConversationPanel;
import Klient.gui.UsersListPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by Nogaz on 24.11.2016.
 */
public class Klient {
    public final static String MAIN_CHAT_NAME = "Main room";
    private JFrame ramka;

    private String adresIPSerwera = "169.254.123.78";
    private String adresLocalhost = "127.0.0.1";
    private int portSerwera = 12345;

    private UsersListPanel usersListPanel;
    private JTabbedPane conversationsTabbedPanel;

    private SocketChannel socketChannel;
    private ByteBuffer messageBuffer;

    public void startClient(){



        configureCommunication();
        setupGUI();

    }

    private void configureCommunication(){
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
    }




    public static void main(String[] args){

        new Klient().startClient();
    }


    private void setupGUI(){
        ramka = new JFrame("PituPitu");
        ramka.setSize(600,900);
        ramka.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JSplitPane panelAplikacji = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        //PRAWA     -->>    USERS LIST
        usersListPanel = new UsersListPanel();

        //LEWA      -->>    KONWERSACJA
        conversationsTabbedPanel = new JTabbedPane();

        ConversationPanel mainConversationPanel = new ConversationPanel( conversationsTabbedPanel);
        conversationsTabbedPanel.addTab(MAIN_CHAT_NAME, mainConversationPanel);

        usersListPanel.registerStartConversateListener(new StartConversationListener());
        panelAplikacji.add(conversationsTabbedPanel);
        panelAplikacji.add(usersListPanel);
        panelAplikacji.setLeftComponent(conversationsTabbedPanel);
        panelAplikacji.setRightComponent(usersListPanel);
        panelAplikacji.setDividerLocation(0.8);



        ramka.getContentPane().add(panelAplikacji);
        ramka.setVisible(true);
    }


    private void addNewConversationTab(User user){
        int howMuchTabs = conversationsTabbedPanel.getTabCount();
        if( howMuchTabs <= 0 ) {
            ConversationPanel mainConversationPanel = new ConversationPanel(conversationsTabbedPanel);
            conversationsTabbedPanel.addTab(MAIN_CHAT_NAME, mainConversationPanel);
        }else{
            int searchedTabIndex = conversationsTabbedPanel.indexOfTab(user.getUserName());
            if( searchedTabIndex == -1 ){
                ConversationPanel mainConversationPanel = new ConversationPanel(conversationsTabbedPanel);
                conversationsTabbedPanel.addTab(user.getUserName(), mainConversationPanel);

            }else{
                System.out.println("Chat with " + user.getUserName() + " is active!!!");
                conversationsTabbedPanel.setSelectedIndex(searchedTabIndex);
            }
        }

    }

    private class StartConversationListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            User user = usersListPanel.getSelectedUser();
            if( user == null ){
                System.out.println("None user selected!");
            }else{
                addNewConversationTab(user);
            }

        }
    }





}
