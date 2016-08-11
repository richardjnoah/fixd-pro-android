package fixtpro.com.fixtpro.beans;

import java.io.Serializable;

/**
 * Created by sahil on 05-03-2016.
 */
public class AssignTechModal implements Serializable {
    String Image = "";
    String FirstName = "";
    String LasttName = "";
    String Rating = "";
    String JobScheduleCount = "";
    String TechId = "";
    String Tech_User_id = "";

    public String getTech_User_id() {
        return Tech_User_id;
    }

    public void setTech_User_id(String tech_User_id) {
        Tech_User_id = tech_User_id;
    }

    public String getTechId() {
        return TechId;
    }

    public void setTechId(String techId) {
        TechId = techId;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLasttName() {
        return LasttName;
    }

    public void setLasttName(String lasttName) {
        LasttName = lasttName;
    }

    public String getRating() {
        return Rating;
    }

    public void setRating(String rating) {
        Rating = rating;
    }

    public String getJobSchedule() {
        return JobScheduleCount;
    }

    public void setJobSchedule(String JobScheduleCount) {
        this.JobScheduleCount = JobScheduleCount;
    }
}
