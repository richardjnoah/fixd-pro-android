package fixdpro.com.fixdpro.beans;

import java.io.Serializable;

/**
 * Created by Harwinder Paras on 10/12/2016.
 */
public class TradeLicenseModal implements Serializable{
    String id = "";
    String license_number = "";
    String service_id = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLicense_number() {
        return license_number;
    }

    public void setLicense_number(String license_number) {
        this.license_number = license_number;
    }

    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }
}
