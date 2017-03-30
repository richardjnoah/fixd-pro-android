package fixdpro.com.fixdpro.singleton;

import java.util.ArrayList;

import fixdpro.com.fixdpro.beans.SkillTrade;
import fixdpro.com.fixdpro.utilites.Constants;

/**
 * Created by sahil on 05-04-2016.
 */
public class CurrentServiceAddingSingleTon {
    private static CurrentServiceAddingSingleTon singleton = new CurrentServiceAddingSingleTon( );
    SkillTrade skillTrade ;
    ArrayList<String> arrayList1 = new ArrayList<String>();
    ArrayList<String> arrayList2 = new ArrayList<String>();
    String selectedServicetype = "";
    String powerSource = "Other";
    String selectedApplianceId = "";
    public String getSelectedServicetype() {
        return selectedServicetype;
    }

    public void setSelectedServicetype(String selectedServicetype) {
        this.selectedServicetype = selectedServicetype;
    }
    public void setSelectedPowerSource(String powerSource){
        this.powerSource = powerSource ;
    }
    public String getSelectedPowerSource(){
        return powerSource;
    }
    public void setSelectedApplianceId(String selectedApplianceId){
        this.selectedApplianceId = selectedApplianceId;
    }
    public String getSelectedApplianceId(){
        return selectedApplianceId;
    }
    public ArrayList<String> getPowerSources(){
        arrayList2.clear();
        arrayList2.add("Electric");
        arrayList2.add("Gas");
        arrayList2.add("Other");
        return arrayList2;
    }
    /* A private Constructor prevents any other
         * class from instantiating.
         */
    private CurrentServiceAddingSingleTon(){ }
    /* Static 'instance' method */
    public static CurrentServiceAddingSingleTon getInstance( ) {

        return singleton;
    }
    /* Other methods protected by singleton-ness */
    public void setSkillTrade(SkillTrade skillTrade){
        this.skillTrade = skillTrade ;
        arrayList1.clear();
        if (skillTrade.getTitle().equals("Electronics") || skillTrade.getTitle().equals("Plumbing") || skillTrade.getTitle().equals("Electrical")){
            arrayList1.add(Constants.REPAIR);
//            arrayList1.add("Install");
        }else {
            arrayList1.add(Constants.REPAIR);
            //arrayList1.add("Maintenance");
            arrayList1.add(Constants.MAINTAIN);
            arrayList1.add(Constants.INSPECTION);
        }
    }
    public SkillTrade getSkillTrade(){
        return  skillTrade;
    }
    public ArrayList<String> getServiceTypeList(){
        return arrayList1;
    }
}

