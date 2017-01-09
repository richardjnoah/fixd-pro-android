package fixdpro.com.fixdpro.beans.install_repair_beans;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sahil on 21-06-2016.
 */
public class EquipmentInfo implements Serializable{
    boolean isCompleted = false ;
    boolean isLocal = false ;
    String image = "";
    String description = "";
    String job_appliance_id = "";
    String brand_name = "";
    String model_number = "";
    String serial_number = "";
    ArrayList<String> imgServerUrls = new ArrayList<String>();
    ArrayList<String> imgLocalUrls = new ArrayList<String>();



    public boolean isLocal() {
        return isLocal;
    }

    public void setIsLocal(boolean isLocal) {
        this.isLocal = isLocal;
    }

    public ArrayList<String> getImgServerUrls() {
        return imgServerUrls;
    }

    public void setImgServerUrls(ArrayList<String> imgServerUrls) {
        this.imgServerUrls = imgServerUrls;
    }

    public void setImgLocalUrls(ArrayList<String> imgLocalUrls) {
        this.imgLocalUrls = imgLocalUrls;
    }

    public ArrayList<String> getImgLocalUrls() {
        return imgLocalUrls;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getJob_appliance_id() {
        return job_appliance_id;
    }

    public void setJob_appliance_id(String job_appliance_id) {
        this.job_appliance_id = job_appliance_id;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public String getModel_number() {
        return model_number;
    }

    public void setModel_number(String model_number) {
        this.model_number = model_number;
    }

    public String getSerial_number() {
        return serial_number;
    }

    public void setSerial_number(String serial_number) {
        this.serial_number = serial_number;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }
}
