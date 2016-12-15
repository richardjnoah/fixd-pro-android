package fixdpro.com.fixdpro.beans.install_repair_beans;

import java.io.Serializable;

/**
 * Created by sahil on 05-04-2016.
 */
public class WhatsWrong implements Serializable{
    String image = "";
    String diagnosis_and_resolution = "";
    boolean isCompleted = false ;
    public boolean isCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDiagnosis_and_resolution() {
        return diagnosis_and_resolution;
    }

    public void setDiagnosis_and_resolution(String diagnosis_and_resolution) {
        this.diagnosis_and_resolution = diagnosis_and_resolution;
    }
}
