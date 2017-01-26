package Serwer.Channels;

import Serwer.Users.User;

import java.util.HashSet;

/**
 * Created by Nogaz on 25.01.2017.
 */
public class IRCChannel {
    String channelName;
    HashSet<User> users;

    public IRCChannel(String channelName){
        this.channelName = channelName;
        users = new HashSet<User>();
    }

    public void addUser(User user){
        this.users.add(user);
    }
    public void removeUser(User user){
        this.users.remove(user);
    }
    public boolean hasUser(User user){
        if( users.contains(user) ){
            return true;
        }
        return false;
    }
    public void sendMessageToAllUsersOnChannel(String message){
        for (User user: users ) {
            user.addUnreceivedMessage(message);
        }
    }
    public String getChannelName(){
        return this.channelName;
    }

    @Override
    public boolean equals(Object obj) {
        IRCChannel channel = (IRCChannel)obj;
        return this.channelName.equals(channel.getChannelName());
    }

    @Override
    public int hashCode() {
        return this.channelName.hashCode();
    }
}
