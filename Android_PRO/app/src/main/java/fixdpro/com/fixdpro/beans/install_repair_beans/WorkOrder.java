package fixdpro.com.fixdpro.beans.install_repair_beans;

import java.io.Serializable;

/**
 * Created by sahil on 05-04-2016.
 */
public class WorkOrder implements Serializable {
    String disgnostic = "0.0";
    String sub_total = "0.0";
    String tax = "0.0";
    String total = "0.0";
    String status = "";
    String hourly_rate = "0.0";
    String is_claim = "0";
    String warranty_fee = "0.0";
    boolean is_covered = false ;
    public String getIs_claim() {
        return is_claim;
    }

    public String getWarranty_fee() {
        return warranty_fee;
    }

    public void setWarranty_fee(String warranty_fee) {
        this.warranty_fee = warranty_fee;
    }

    public boolean is_covered() {
        return is_covered;
    }

    public void setIs_covered(boolean is_covered) {
        this.is_covered = is_covered;
    }

    public void setIs_claim(String is_claim) {
        this.is_claim = is_claim;
    }

    public String getHourly_rate() {
        return hourly_rate;
    }

    public void setHourly_rate(String hourly_rate) {
        this.hourly_rate = hourly_rate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    boolean isCompleted = false ;
    public boolean isCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }
    public String getDisgnostic() {
        return disgnostic;
    }

    public void setDisgnostic(String disgnostic) {
        this.disgnostic = disgnostic;
    }

    public String getSub_total() {
        return sub_total;
    }

    public void setSub_total(String sub_total) {
        this.sub_total = sub_total;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
