package fixdpro.com.fixdpro.adapters;

import java.io.Serializable;

/**
 * Created by sahil on 9/6/2016.
 */
public class ApplianceReceipt implements Serializable {
    String  appliance_name = "";
    String pro_sub_total = "";

    public String getAppliance_name() {
        return appliance_name;
    }

    public void setAppliance_name(String appliance_name) {
        this.appliance_name = appliance_name;
    }

    public String getPro_sub_total() {
        return pro_sub_total;
    }

    public void setPro_sub_total(String pro_sub_total) {
        this.pro_sub_total = pro_sub_total;
    }
}
