package fixdpro.com.fixdpro.beans;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by sahil on 22-07-2016.
 */
public class NotificationListModal implements Serializable {
    String ID = "";
    String UserID = "";
    String JobID = "";
    String Text = "";
    String IsActive = "";
    String CreateAt = "";
    String UpdatedAt = "";
    String ServiceType = "";
    String ServiceName = "";
    String IconImage = "";
    String IsRead = "";
    String is_active = "";

    public String getIs_active() {
        return is_active;
    }

    public void setIs_active(String is_active) {
        this.is_active = is_active;
    }

    HashMap<String,NotificationTypeData> data = new HashMap<String,NotificationTypeData>();

    public HashMap<String, NotificationTypeData> getData() {
        return data;
    }

    public void setData(HashMap<String, NotificationTypeData> data) {
        this.data = data;
    }
    public String getIsRead() {
        return IsRead;
    }

    public void setIsRead(String isRead) {
        IsRead = isRead;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getJobID() {
        return JobID;
    }

    public void setJobID(String jobID) {
        JobID = jobID;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public String getIsActive() {
        return IsActive;
    }

    public void setIsActive(String isActive) {
        IsActive = isActive;
    }

    public String getCreateAt() {
        return CreateAt;
    }

    public void setCreateAt(String createAt) {
        CreateAt = createAt;
    }

    public String getUpdatedAt() {
        return UpdatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        UpdatedAt = updatedAt;
    }

    public String getServiceType() {
        return ServiceType;
    }

    public void setServiceType(String serviceType) {
        ServiceType = serviceType;
    }

    public String getServiceName() {
        return ServiceName;
    }

    public void setServiceName(String serviceName) {
        ServiceName = serviceName;
    }

    public String getIconImage() {
        return IconImage;
    }

    public void setIconImage(String iconImage) {
        IconImage = iconImage;
    }
}
