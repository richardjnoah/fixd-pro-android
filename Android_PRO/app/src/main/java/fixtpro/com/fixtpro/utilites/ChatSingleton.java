package fixtpro.com.fixtpro.utilites;

import com.quickblox.chat.model.QBDialog;

import java.util.ArrayList;

/**
 * Created by sahil on 02-05-2016.
 */
public class ChatSingleton {
    public static ChatSingleton singleton = new ChatSingleton( );

    /* A private Constructor prevents any other
     * class from instantiating.
     */
    public ArrayList<QBDialog> dataSourceUsers = new ArrayList<QBDialog>();
    private ChatSingleton(){ }

    /* Static 'instance' method */
    public static ChatSingleton getInstance( ) {
        return singleton;
    }

}

