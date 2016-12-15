package fixtpro.com.fixtpro.beans;

import java.io.Serializable;

/**
 * Created by sahil on 8/8/2016.
 */
public class NotificationTypeData implements Serializable {
    String key = "";
    String value = "";

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

