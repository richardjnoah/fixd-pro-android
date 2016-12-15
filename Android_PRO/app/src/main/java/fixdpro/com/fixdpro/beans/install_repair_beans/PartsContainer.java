package fixdpro.com.fixdpro.beans.install_repair_beans;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by sahil on 21-04-2016.
 */
public class PartsContainer implements Serializable{
    ArrayList<Parts> partsArrayList = new ArrayList<Parts>();
    boolean isCompleted = false ;
    public boolean isCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }
    public ArrayList<Parts> getPartsArrayList() {
        return partsArrayList;
    }

    public void setPartsArrayList(ArrayList<Parts> partsArrayList) {
        this.partsArrayList = partsArrayList;
    }


}
