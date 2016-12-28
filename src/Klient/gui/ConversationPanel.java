package Klient.gui;

import Klient.Klient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.ByteBuffer;

/**
 * Created by Nogaz on 27.12.2016.
 */
public class ConversationPanel extends JPanel {
    private final JTabbedPane parentJTabbedPane;


    JTextArea receivedMessages;
    JTextArea wiadomosc;

    ByteBuffer messageBuffer;

    String lastReceivedMessage = "";
    String lastSentMessage = "";


    public ConversationPanel(JTabbedPane parentJTabbedPane){
        super();
        this.parentJTabbedPane = parentJTabbedPane;
        JSplitPane panelGlowny = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        receivedMessages = new JTextArea(40,80);
        receivedMessages.setLineWrap(true);
        receivedMessages.setWrapStyleWord(true);
        receivedMessages.setEditable(false);
        JScrollPane conversationPanel = new JScrollPane(receivedMessages);
        conversationPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        conversationPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        conversationPanel.setAutoscrolls(true);


        wiadomosc = new JTextArea(5,80);
        wiadomosc.setLineWrap(true);
        JScrollPane sendMessageScroll = new JScrollPane(wiadomosc);
        sendMessageScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        sendMessageScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JButton wyslijButton = new JButton("Wyślij");
        wyslijButton.addActionListener(new WyslijButtonListener());
        JButton clearConversationButton = new JButton("Wyczyść");
        clearConversationButton.addActionListener(new ClearConversationButton());
        JButton exitConversationButton = new JButton("Wyjdź");
        exitConversationButton.addActionListener(new ExitConversationListener());

        JPanel sendingPanel = new JPanel();

        sendingPanel.setLayout(new BoxLayout(sendingPanel, BoxLayout.Y_AXIS));
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
        buttonsPanel.add(wyslijButton);
        buttonsPanel.add(clearConversationButton);
        buttonsPanel.add(exitConversationButton);
        sendingPanel.add(sendMessageScroll);
        sendingPanel.add(buttonsPanel);

        panelGlowny.setTopComponent(conversationPanel);
        panelGlowny.setBottomComponent(sendingPanel);
        panelGlowny.setDividerLocation(0.9d);

        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.setPreferredSize(new Dimension(400,900));
        this.add(panelGlowny);
    }


    public class WyslijButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            receivedMessages.append( wiadomosc.getText() + "\n");
            try{
                /*
                byte[] message = wiadomosc.getText().getBytes();
                messageBuffer.clear();
                messageBuffer.put(message);
                messageBuffer.flip();
                while( messageBuffer.hasRemaining() ){
                    //socketChannel.write(messageBuffer);
                }

                lastSentMessage = wiadomosc.getText();

                receivedMessages.append(lastSentMessage);
                //sendMessage(lastSentMessage);
                */
            }catch (Exception ex){
                ex.printStackTrace();
            }
            clearSendMessagePanel();
        }
    }



    private class ClearConversationButton implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            clearChatWindow();
        }
    }
    private class ExitConversationListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            removeConversation();
        }
    }

    public String getWrittenMessage(){
        return "";
    }
    public void addNewMessage(String message){

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
        //this.writer.println(message);
        //this.writer.flush();
    }
    public void clearSendMessagePanel(){
        wiadomosc.setText("");
        wiadomosc.requestFocus();
    }
    private void removeConversation(){
        System.out.println(this.getName());

        if( parentJTabbedPane.getTitleAt( parentJTabbedPane.indexOfComponent(this)).equals(Klient.MAIN_CHAT_NAME) ){
            System.out.println("Can't remove Main chat window!");
        }else{
            parentJTabbedPane.remove(this);
        }




    }

}
