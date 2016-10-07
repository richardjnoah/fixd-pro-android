package fixdpro.com.fixdpro.beans;

import java.io.Serializable;

/**
 * Created by sahil on 13-07-2016.
 */
public class CityBeans implements Serializable {
    String id = "";
    String cityname = "";
    String stateabbr = "";
    String statename = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCityname() {
        return cityname;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }

    public String getStateabbr() {
        return stateabbr;
    }

    public void setStateabbr(String stateabbr) {
        this.stateabbr = stateabbr;
    }

    public String getStatename() {

        return statename;
    }

    public void setStatename(String statename) {
        this.statename = statename;
    }
}
