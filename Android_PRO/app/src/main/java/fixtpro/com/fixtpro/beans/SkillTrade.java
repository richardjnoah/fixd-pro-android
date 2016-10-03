package fixtpro.com.fixtpro.beans;

import java.io.Serializable;

/**
 * Created by sony on 09-02-2016.
 */
public class SkillTrade implements Serializable {
    int id = 0;
    String title = "";
    String number = "";
    String for_consumer = "";
    String display_order = "";
    boolean isChecked = false;

    public String getFor_consumer() {
        return for_consumer;
    }

    public void setFor_consumer(String for_consumer) {
        this.for_consumer = for_consumer;
    }

    public String getDisplay_order() {
        return display_order;
    }

    public void setDisplay_order(String display_order) {
        this.display_order = display_order;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
}
