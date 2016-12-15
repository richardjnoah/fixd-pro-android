package fixdpro.com.fixdpro.singleton;

import java.util.ArrayList;

import fixdpro.com.fixdpro.beans.AssignTechModal;
import fixdpro.com.fixdpro.beans.TechnicianModal;

/**
 * Created by sahil on 13-06-2016.
 */
public class TechniciansListSinglton {
    public static  ArrayList<TechnicianModal> technicianModalsList = new ArrayList<TechnicianModal>();
    public static  ArrayList<AssignTechModal> technicianModalsListForAssign = new ArrayList<AssignTechModal>();

    private static TechniciansListSinglton singleton = new TechniciansListSinglton();
  private TechniciansListSinglton(){ }

    /* Static 'instance' method */
    public static TechniciansListSinglton getInstance( ) {
        return singleton;
    }
    /* Other methods protected by singleton-ness */


}

