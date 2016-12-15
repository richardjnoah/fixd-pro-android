package fixdpro.com.fixdpro.utilites;

import com.quickblox.chat.model.QBChatMessage;

import java.util.ArrayList;

/**
 * Created by sahil on 03-05-2016.
 */
public class MessageSingleton {
    public static MessageSingleton singleton = new MessageSingleton( );

    /* A private Constructor prevents any other
     * class from instantiating.
     */
    public ArrayList<QBChatMessage> chatlist = new ArrayList<QBChatMessage>() ;
    private MessageSingleton(){ }

    /* Static 'instance' method */
    public static MessageSingleton getInstance( ) {
        return singleton;
    }
}
