package Serwer;

/**
 * Created by Nogaz on 13.01.2017.
 */
public class IncomingMessageHandler {

    public static final String USER = "USER";
    public static final String NICK = "NICK";
    public static final String SERVER = "SERVER";
    public static final String SQUIT = "SQUIT";
    public static final String QUIT = "QUIT";
    public static final String JOIN = "JOIN";
    public static final String PART = "PART";
    public static final String MODE = "MODE";
    public static final String TOPIC = "TOPIC";
    public static final String NAMES = "NAMES";
    public static final String LIST = "LIST";
    public static final String INVITE = "INVITE";
    public static final String KICK = "KICK";




    public static void handleIncomingMessage(String message){
        String[] messageInParts = message.split(" ");

        switch( messageInParts[0] ){
            case USER:

            case NICK:

            case SERVER:

            case QUIT:

            case SQUIT:

            case JOIN:

            case PART:

            case MODE:

            case TOPIC:

            case NAMES:

            case LIST:

            case INVITE:

            case KICK:

            default: System.out.println("Message header not recognised");
        }
    }
}
