package fixdpro.com.fixdpro.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import fixdpro.com.fixdpro.R;
import fixdpro.com.fixdpro.beans.AvailableJobModal;
import fixdpro.com.fixdpro.utilites.HandlePagingResponse;
import fixdpro.com.fixdpro.utilites.Utilities;

/**
 * Created by sahil on 9/5/2016.
 */
public class JobsPagingAdapter extends BaseAdapter {
    ArrayList<AvailableJobModal> list;
    Activity activity;
    private static LayoutInflater inflater=null;
    public Resources res;
    AvailableJobModal tempValues=null;
    Typeface fontfamily;
    HandlePagingResponse handlePagingResponse ;
    String state = "open";

    public  JobsPagingAdapter(Activity  activity, ArrayList<AvailableJobModal> list, Resources res,HandlePagingResponse handlePagingResponse,String state){
        this.list = list ;
        this.activity = activity;
        this.res = res ;
        fontfamily = Typeface.createFromAsset(activity.getAssets(), "HelveticaNeue-Thin.otf");
//
//            Log.i("", "lstheight"+lstheight);
        /***********  Layout inflator to call external xml layout () ***********/
        inflater = ( LayoutInflater )activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.handlePagingResponse = handlePagingResponse;
        this.state = state;
    }

    public int getCount() {

        if(list.size()<=0)
            return list.size();
        return list.size();
    }
    @Override
    public AvailableJobModal getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder{

        public TextView contactName, address, appliance_types_name_and_service_type;
        public TextView day_request_date, timeinterval;

    }
    /****** Depends upon items size called for each row , Create each ListView row *****/
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.available_jobs_item, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.contactName = (TextView) vi.findViewById(R.id.contactName);
            holder.address = (TextView) vi.findViewById(R.id.address);
            holder.appliance_types_name_and_service_type = (TextView) vi.findViewById(R.id.appliance_types_name_and_service_type);
            holder.day_request_date = (TextView) vi.findViewById(R.id.day_request_date);
            holder.timeinterval = (TextView) vi.findViewById(R.id.timeinterval);
            /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        }
        else
            holder=(ViewHolder)vi.getTag();

        if(list.size()<=0)
        {
//            holder.BName.setText("No items");

        }
        else
        {
            /***** Get each Model object from Arraylist ********/
            tempValues=null;
            tempValues =  list.get( position );
            /************  Set Model values in Holder elements ***********/
            String contactname = tempValues.getContact_name();

            holder.contactName.setText(contactname);
            String convertedDate = Utilities.convertDate(tempValues.getRequest_date());
            holder.day_request_date.setText(Utilities.convertDate(tempValues.getRequest_date()));
            holder.timeinterval.setText(tempValues.getTimeslot_name());
            if (state.equals("Open")){
                holder.address.setText(tempValues.getJob_customer_addresses_zip()+" - "+tempValues.getJob_customer_addresses_city()+","+tempValues.getJob_customer_addresses_state());

            }else {
                holder.address.setText(tempValues.getJob_customer_addresses_address()+" - "+tempValues.getJob_customer_addresses_city()+","+tempValues.getJob_customer_addresses_state());
            }

            String STR_appliance_types_name_and_service_type = "";
            HashMap<String,Integer> hashMap = new HashMap<>();
            if (tempValues.getJob_appliances_arrlist().size() == 1){
                for(int j = 0; j < tempValues.getJob_appliances_arrlist().size(); j++)
                {
                    STR_appliance_types_name_and_service_type = tempValues.getJob_appliances_arrlist().get(j).getService_name() + " " +tempValues.getJob_appliances_arrlist().get(j).getJob_appliances_service_type();
                }
            }else {
                for(int j = 0; j < tempValues.getJob_appliances_arrlist().size() ; j++)
                {
                    String key = tempValues.getJob_appliances_arrlist().get(j).getService_name() + " " +tempValues.getJob_appliances_arrlist().get(j).getJob_appliances_service_type();
                    if (hashMap.containsKey(key)){
                        hashMap.put(key,hashMap.get(key)+1);
                    }else {
                        hashMap.put(key,1);
                    }

//                    STR_appliance_types_name_and_service_type = STR_appliance_types_name_and_service_type + tempValues.getJob_appliances_arrlist().get(j).getService_name() + " " +tempValues.getJob_appliances_arrlist().get(j).getJob_appliances_service_type() +"\n";
                }
                for (String key : hashMap.keySet()) {
                    // ...
                    int value = hashMap.get(key);
                    if (value > 1){
                        STR_appliance_types_name_and_service_type = STR_appliance_types_name_and_service_type + key + "("+value+")\n" ;
                    }else {
                        STR_appliance_types_name_and_service_type = STR_appliance_types_name_and_service_type + key + "\n" ;
                    }
                }
//                STR_appliance_types_name_and_service_type = STR_appliance_types_name_and_service_type + tempValues.getJob_appliances_arrlist().get((tempValues.getJob_appliances_arrlist().size() -1)).getService_name() + " " +tempValues.getJob_appliances_arrlist().get((tempValues.getJob_appliances_arrlist().size() -1)).getJob_appliances_service_type();

            }

            holder.appliance_types_name_and_service_type.setText(STR_appliance_types_name_and_service_type.trim());
            STR_appliance_types_name_and_service_type = "";
            if (position == list.size() - 1){
                if (handlePagingResponse != null)
                handlePagingResponse.handleChangePage();
            }
        }
        return vi;
    }

}

