package fixdpro.com.fixdpro.utilites;

import java.util.ArrayList;

import fixdpro.com.fixdpro.beans.AvailableJobModal;
import fixdpro.com.fixdpro.singleton.TechniciansListSinglton;

/**
 * Created by sahil on 02-03-2016.
 */
public class Singleton {

    private static Singleton singleton = new Singleton( );
    ArrayList<AvailableJobModal> availablejoblist = new ArrayList<AvailableJobModal>();
    ArrayList<AvailableJobModal> schedulejoblist  = new ArrayList<AvailableJobModal>();
    ArrayList<AvailableJobModal> completedjoblist  = new ArrayList<AvailableJobModal>();
    ArrayList<AvailableJobModal> progressjoblist  = new ArrayList<AvailableJobModal>();

    public  int pageAvaileble = 1 ;
    public  int pageSheduled = 1 ;
    public  int compltedpage = 1 ;
    public  int progresspage = 1 ;

    public int getPageAvaileble() {
        return pageAvaileble;
    }

    public void setPageAvaileble(int pageAvaileble) {
        this.pageAvaileble = pageAvaileble;
    }

    public int getPageSheduled() {
        return pageSheduled;
    }

    public void setPageSheduled(int pageSheduled) {
        this.pageSheduled = pageSheduled;
    }

    public int getCompltedpage() {
        return compltedpage;
    }

    public void setCompltedpage(int compltedpage) {
        this.compltedpage = compltedpage;
    }

    public String getNextCompleted() {
        return nextCompleted;
    }

    public void setNextCompleted(String nextCompleted) {
        this.nextCompleted = nextCompleted;
    }

    public String getNextSchduled() {
        return nextSchduled;
    }

    public void setNextSchduled(String nextSchduled) {
        this.nextSchduled = nextSchduled;
    }

    public String getNextAvailable() {
        return nextAvailable;
    }

    public void setNextAvailable(String nextAvailable) {
        this.nextAvailable = nextAvailable;
    }

    public  String nextCompleted = "null";
    public  String nextSchduled = "null";
    public  String nextAvailable = "null";
    public  String nextProgress = "null";
    /* A private Constructor prevents any other
     * class from instantiating.
     */

    public ArrayList<AvailableJobModal> getAvailablejoblist() {
        return availablejoblist;
    }

    public void setAvailablejoblist(ArrayList<AvailableJobModal> availablejoblist) {
        this.availablejoblist = availablejoblist;
    }

    public ArrayList<AvailableJobModal> getSchedulejoblist() {
        return schedulejoblist;
    }

    public void setSchedulejoblist(ArrayList<AvailableJobModal> schedulejoblist) {
        this.schedulejoblist = schedulejoblist;
    }

    public ArrayList<AvailableJobModal> getCompletedjoblist() {
        return completedjoblist;
    }

    public void setCompletedjoblist(ArrayList<AvailableJobModal> completedjoblist) {
        this.completedjoblist = completedjoblist;
    }

    private Singleton(){ }

    /* Static 'instance' method */
    public static Singleton getInstance( ) {
        return singleton;
    }

    public  void doLogout(){
        completedjoblist.clear();
        schedulejoblist.clear();
        availablejoblist.clear();
        progressjoblist.clear();
        TechniciansListSinglton.getInstance().technicianModalsList.clear();
        nextCompleted = "null";
        nextSchduled = "null";
        nextAvailable = "null";
        nextProgress = "null";
        pageAvaileble = 1 ;
        pageSheduled = 1 ;
        compltedpage = 1 ;
        progresspage = 1 ;
        CurrentScheduledJobSingleTon.getInstance().setCurrentJonModal(null);
    }

    public ArrayList<AvailableJobModal> getProgressjoblist() {
        return progressjoblist;
    }

    public void setProgressjoblist(ArrayList<AvailableJobModal> progressjoblist) {
        this.progressjoblist = progressjoblist;
    }

    public int getProgresspage() {
        return progresspage;
    }

    public void setProgresspage(int progresspage) {
        this.progresspage = progresspage;
    }

    public String getNextProgress() {
        return nextProgress;
    }

    public void setNextProgress(String nextProgress) {
        this.nextProgress = nextProgress;
    }
}
