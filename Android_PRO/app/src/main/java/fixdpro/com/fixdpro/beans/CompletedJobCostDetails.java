package fixdpro.com.fixdpro.beans;

import java.io.Serializable;
import java.util.ArrayList;

import fixdpro.com.fixdpro.adapters.ApplianceReceipt;

/**
 * Created by sahil on 9/6/2016.
 */
public class CompletedJobCostDetails implements Serializable {
    String miles_paid_amount = "0";
    String subtotal ="0";
    String total = "0";
    String fixd_cut ="0";
    String fixd_cut_percentage ="0";
    ArrayList<ApplianceReceipt> applianceReceipts = new ArrayList<ApplianceReceipt>();
}
