package fixdpro.com.fixdpro.singleton;


import java.util.ArrayList;

import fixdpro.com.fixdpro.beans.NotificationListModal;

/**
 * Created by sahil on 22-07-2016.
 */
public class NotificationSingleton {
    private static NotificationSingleton singleton = new NotificationSingleton( );

    /* A private Constructor prevents any other 
     * class from instantiating.
     */
    private NotificationSingleton(){ }

    /* Static 'instance' method */
    public static NotificationSingleton getInstance( ) {
        return singleton;
    }

    ArrayList<NotificationListModal> arrayList = new ArrayList<NotificationListModal>();

    
    public  int page = 1 ;
    
  
    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
    
   
    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }
    
    public  String next = "null";
    
    /* A private Constructor prevents any other
     * class from instantiating.
     */
    
    public ArrayList<NotificationListModal> getlist() {
        return arrayList;
    }

    public void setlist(ArrayList<NotificationListModal> arrayList) {
        this.arrayList = arrayList;
    }

}


