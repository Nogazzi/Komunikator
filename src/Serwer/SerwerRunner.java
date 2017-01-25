package Serwer;

/**
 * Created by Nogaz on 25.01.2017.
 */
public class SerwerRunner {


    public static void main(String[] args){
        Serwer ircSerwer = Serwer.getSerwerInstance();
        ircSerwer.runSerwer();
    }
}
