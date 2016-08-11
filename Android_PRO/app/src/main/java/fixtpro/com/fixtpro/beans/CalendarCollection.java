package fixtpro.com.fixtpro.beans;

/**
 * Created by sony on 06-02-2016.
 */
import java.util.ArrayList;

public class CalendarCollection {

    public String date="";
    public String event_message="";
    public ArrayList<AvailableJobModal> list;
    public static ArrayList<CalendarCollection> date_collection_arr = new ArrayList<CalendarCollection>();
    public CalendarCollection(String date, String event_message,ArrayList<AvailableJobModal> list){
        this.date = date;
        this.event_message = event_message;
        this.list = list ;
    }

}