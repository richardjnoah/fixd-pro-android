package fixdpro.com.fixdpro.utilites;

import java.util.ArrayList;

import fixdpro.com.fixdpro.beans.AvailableJobModal;
import fixdpro.com.fixdpro.beans.JobAppliancesModal;
import fixdpro.com.fixdpro.beans.install_repair_beans.InstallOrRepairModal;
import fixdpro.com.fixdpro.beans.install_repair_beans.ReapirInstallProcessModal;

/**
 * Created by sahil on 31-03-2016.
 */
public class CurrentScheduledJobSingleTon {

    AvailableJobModal availableJobModal = null;
    JobAppliancesModal jobApplianceModal = null;
    ArrayList<ReapirInstallProcessModal> repairInstallProceessList = new ArrayList<ReapirInstallProcessModal>();
    ReapirInstallProcessModal reapirInstallProcessModal = null ;
    InstallOrRepairModal installOrRepairModal ;
    private static CurrentScheduledJobSingleTon CurrentScheduledJobSingleTon = new CurrentScheduledJobSingleTon( );
    public  static String  LastFragment = Constants.START_JOB_FRAGMENT ;


    /* A private Constructor prevents any other 
     * class from instantiating.
     */
    private CurrentScheduledJobSingleTon(){ }

    /* Static 'instance' method */
    public static CurrentScheduledJobSingleTon getInstance( ) {
        return CurrentScheduledJobSingleTon;
    }

    /* Other methods protected by CurrentScheduledJobSingleTon-ness */
    public   void setCurrentJonModal(AvailableJobModal availableJobModal) {
        this.availableJobModal = availableJobModal ;
    }
    public void setSelectedJobApplianceModal(JobAppliancesModal jobApplianceModal){
        this.jobApplianceModal = jobApplianceModal;
        setInstallOrRepairModal(jobApplianceModal.getInstallOrRepairModal());
    }
    public JobAppliancesModal getJobApplianceModal(){
        return jobApplianceModal;
    }
    public ArrayList<ReapirInstallProcessModal> getReapirInstallProcessModalList(){
        return repairInstallProceessList;
    }
    public  AvailableJobModal getCurrentJonModal() {
        return availableJobModal;
    }

    public void setCurrentReapirInstallProcessModal(ReapirInstallProcessModal reapirInstallProcessModal){
        this.reapirInstallProcessModal = reapirInstallProcessModal ;
    }
    public ReapirInstallProcessModal getCurrentReapirInstallProcessModal(){
        return reapirInstallProcessModal;
    }
    public InstallOrRepairModal  getInstallOrRepairModal(){
        return installOrRepairModal;
    }

    public void setInstallOrRepairModal(InstallOrRepairModal installOrRepairModal) {
        this.installOrRepairModal = installOrRepairModal;
        repairInstallProceessList.clear();
        if (!jobApplianceModal.getAppliance_type_name().equals("Re Key")){
            repairInstallProceessList.add(new ReapirInstallProcessModal(Constants.EQUIPMENT_INFO,installOrRepairModal.getEquipmentInfo().isCompleted()));
        }
        if (jobApplianceModal.getJob_appliances_service_type().equals("Repair")){
            repairInstallProceessList.add(new ReapirInstallProcessModal(Constants.REPAIR_TYPE,installOrRepairModal.getRepairType().isCompleted()));
        }else if (jobApplianceModal.getJob_appliances_service_type().equals("Install") || jobApplianceModal.getJob_appliances_service_type().equals("Re Key")){
            repairInstallProceessList.add(new ReapirInstallProcessModal(Constants.INSTALL_TYPE,installOrRepairModal.getRepairType().isCompleted()));
        }else {
            repairInstallProceessList.add(new ReapirInstallProcessModal(Constants.INSTALL_TYPE,installOrRepairModal.getRepairType().isCompleted()));
        }

        repairInstallProceessList.add(new ReapirInstallProcessModal(Constants.PARTS,installOrRepairModal.getPartsContainer().isCompleted()));
        repairInstallProceessList.add(new ReapirInstallProcessModal(Constants.WORK_ORDER,installOrRepairModal.getWorkOrder().isCompleted()));
//        if (!jobApplianceModal.getJob_appliances_service_type().equals("Install")){
//            repairInstallProceessList.add(new ReapirInstallProcessModal(Constants.REPAIR_INFO,installOrRepairModal.getRepairInfo().isCompleted()));
//        }else {
//            repairInstallProceessList.add(new ReapirInstallProcessModal(Constants.INSTALL_INFO,installOrRepairModal.getRepairType().isCompleted()));
//        }

        repairInstallProceessList.add(new ReapirInstallProcessModal(Constants.SIGNATURE,installOrRepairModal.getSignature().isCompleted()));
    }
}
