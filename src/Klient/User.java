package Klient;

import com.sun.deploy.util.StringUtils;

import java.util.Random;

/**
 * Created by Nogaz on 27.12.2016.
 */
public class User {
    private int[] userIpAdress;
    private String userName;

    public User(int[] ipAdress, String name){
        this.userIpAdress = ipAdress;
        this.userName = name;
    }

    public int[] getUserIpAdress(){
        return this.userIpAdress;
    }
    public String getUserName(){
        return this.userName;
    }

    public static User getRandomUser(){
        int[] ipAdress = new int[4];
        for( int i = 0 ; i < ipAdress.length ; ++i ){
            ipAdress[i] = (int)(Math.random()*255 + 1);
        }

        User user = new User(ipAdress, getRandomString(15));
        return user;
    }
    public static String getRandomString(int length){
        String result = "";
        for(int i = 0 ; i < length ; ++i ){
            Random r = new Random();
            result += (char)(r.nextInt(26) + 'a'); ;

        }
        return result;
    }
    @Override
    public String toString(){
        return this.userName + "[" + this.userIpAdress[0] + "." + this.userIpAdress[1] + "." + this.userIpAdress[2] + "." + this.userIpAdress[3] + "]";
    }
}
