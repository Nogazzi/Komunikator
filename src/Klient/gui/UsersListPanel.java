package Klient.gui;

import Klient.User;

import javax.jws.soap.SOAPBinding;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

/**
 * Created by Nogaz on 27.12.2016.
 */
public class UsersListPanel extends JPanel {

    private Vector<User> usersList;
    private JList<User> listComponent;

    private JButton startConversationButton;


    public UsersListPanel(){
        super();

        usersList = new Vector<User>();
        for(int i = 0 ; i < 15 ; ++i ){
            usersList.add(User.getRandomUser());

        }
        listComponent = new JList( usersList );

        listComponent.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listComponent.setLayoutOrientation(JList.VERTICAL);
        listComponent.setVisibleRowCount(-1);


        JScrollPane listScroller = new JScrollPane(listComponent);
        listScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        System.out.println(usersList.toString());

        startConversationButton = new JButton();
        startConversationButton.setText("Conversate!");
        this.setPreferredSize(new Dimension(200,900));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(listScroller);
        this.add(startConversationButton);
    }

    public void registerStartConversateListener(ActionListener listener) throws NullPointerException {
        if( listener == null ){
            throw new NullPointerException();
        }
        startConversationButton.addActionListener(listener);
    }
}
