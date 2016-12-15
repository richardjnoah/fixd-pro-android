package fixtpro.com.fixtpro.beans;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sahil on 18-04-2016.
 */
public class CalenderScheduledJobModal  implements Serializable{
    String day = "" ;
    String date = "" ;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    ArrayList<AvailableJobModal> list = new ArrayList<AvailableJobModal>();

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public ArrayList<AvailableJobModal> getList() {
        return list;
    }

    public void setList(ArrayList<AvailableJobModal> list) {
        this.list = list;
    }
}
