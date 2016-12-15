package fixdpro.com.fixdpro.beans;

import java.io.Serializable;

/**
 * Created by sahil on 07-07-2016.
 */
public class NotificationModal implements Serializable {
    String Type = "";
    String Message = "";
    String JobId = "";
    String TechId = "";
    String JobAppliance = "";
    String dialogId = "";

    public String getDialogId() {
        return dialogId;
    }

    public void setDialogId(String dialogId) {
        this.dialogId = dialogId;
    }

    public String getJobAppliance() {
        return JobAppliance;
    }

    public void setJobAppliance(String jobAppliance) {
        JobAppliance = jobAppliance;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getJobId() {
        return JobId;
    }

    public void setJobId(String jobId) {
        JobId = jobId;
    }

    public String getTechId() {
        return TechId;
    }

    public void setTechId(String techId) {
        TechId = techId;
    }
}

