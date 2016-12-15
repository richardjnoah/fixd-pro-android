package fixdpro.com.fixdpro.beans.install_repair_beans;

import java.io.Serializable;

/**
 * Created by sahil on 05-04-2016.
 */
public class RepairType implements Serializable {
    String Type = "";
    String Price = "0";
    String Id  = "";
    String labor_hours  = "";
    String calculatedBy = "";
    String fixed_cost = "0";

    public String getCalculatedBy() {
        return calculatedBy;
    }

    public void setCalculatedBy(String calculatedBy) {
        this.calculatedBy = calculatedBy;
    }

    public String getFixed_cost() {
        return fixed_cost;
    }

    public void setFixed_cost(String fixed_cost) {
        this.fixed_cost = fixed_cost;
    }

    public RepairType(){

     }

    public String getLabor_hours() {
        return labor_hours;
    }

    public void setLabor_hours(String labor_hours) {
        this.labor_hours = labor_hours;
    }

    boolean isCompleted = false ;
    public boolean isCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }
    public RepairType(String Type){
        this.Type = Type ;
    }
    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

}
