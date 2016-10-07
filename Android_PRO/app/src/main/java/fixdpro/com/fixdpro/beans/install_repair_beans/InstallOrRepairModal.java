package fixdpro.com.fixdpro.beans.install_repair_beans;

import java.io.Serializable;

/**
 * Created by sahil on 05-04-2016.
 */
public class InstallOrRepairModal implements Serializable{
    RepairInfo repairInfo = new RepairInfo();
    RepairType repairType = new RepairType(); //repai |maintain | Install
//    Equipment Info replaces Whats Wrong
    EquipmentInfo equipmentInfo = new EquipmentInfo();
//    now no use of Whats Wrong
    WhatsWrong whatsWrong = new WhatsWrong();
    Signature signature = new Signature();
    WorkOrder workOrder = new WorkOrder();
    PartsContainer partsContainer = new PartsContainer();

    public EquipmentInfo getEquipmentInfo() {
        return equipmentInfo;
    }

    public void setEquipmentInfo(EquipmentInfo equipmentInfo) {
        this.equipmentInfo = equipmentInfo;
    }

    public RepairInfo getRepairInfo() {
        return repairInfo;
    }

    public void setRepairInfo(RepairInfo repairInfo) {
        this.repairInfo = repairInfo;
    }

    public RepairType getRepairType() {
        return repairType;
    }

    public void setRepairType(RepairType repairType) {
        this.repairType = repairType;
    }

    public WhatsWrong getWhatsWrong() {
        return whatsWrong;
    }

    public void setWhatsWrong(WhatsWrong whatsWrong) {
        this.whatsWrong = whatsWrong;
    }

    public Signature getSignature() {
        return signature;
    }

    public void setSignature(Signature signature) {
        this.signature = signature;
    }

    public WorkOrder getWorkOrder() {
        return workOrder;
    }

    public void setWorkOrder(WorkOrder workOrder) {
        this.workOrder = workOrder;
    }

    public PartsContainer getPartsContainer() {
        return partsContainer;
    }

    public void setPartsContainer(PartsContainer partsContainer) {
        this.partsContainer = partsContainer;
    }
}
