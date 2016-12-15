package fixtpro.com.fixtpro.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

import fixdpro.com.fixdpro.R;
import fixtpro.com.fixtpro.activities.TechnicianInformation_Activity;
import fixtpro.com.fixtpro.beans.TechnicianModal;
import fixtpro.com.fixtpro.utilites.Preferences;

/**
 * Created by sony on 11-02-2016.
 */
public class TechnicianAdapter extends BaseAdapter {

    /*********** Declare Used Variables *********/
    private Activity activity;
    private ArrayList data;
    private static LayoutInflater inflater=null;
    public Resources res;
    TechnicianModal tempValues=null;
    Typeface fontfamily;


    /*************  CustomAdapter Constructor *****************/
    public TechnicianAdapter(Activity a, ArrayList d,Resources resLocal) {

        /********** Take passed values **********/
        activity = a;
        data=d;
        res = resLocal;
        fontfamily = Typeface.createFromAsset(activity.getAssets(), "HelveticaNeue-Thin.otf");
//
//            Log.i("", "lstheight"+lstheight);
        /***********  Layout inflator to call external xml layout () ***********/
        inflater = ( LayoutInflater )activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);


    }

    /******** What is the size of Passed Arraylist Size ************/
    public int getCount() {

        if(data.size()<=0)
            return data.size();
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder{

        public TextView txtTechname;
        public TextView txtPhone;
        public TextView txtEmail;
        public ImageView imgTech;
        public LinearLayout edit;
    }

    /****** Depends upon data size called for each row , Create each ListView row *****/
    public View getView(final int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        final ViewHolder holder;

        if(convertView==null){

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.technicion_item, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.txtTechname = (TextView) vi.findViewById(R.id.txtTechname);
            holder.txtPhone = (TextView) vi.findViewById(R.id.txtPhone);
            holder.txtEmail = (TextView) vi.findViewById(R.id.txtEmail);
            holder.imgTech = (ImageView) vi.findViewById(R.id.imgTech);
            holder.edit = (LinearLayout)vi.findViewById(R.id.edit);
            holder.txtTechname.setTypeface(fontfamily);
            holder.txtPhone.setTypeface(fontfamily);
            holder.txtEmail.setTypeface(fontfamily);
            /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        }
        else
            holder=(ViewHolder)vi.getTag();

        if(data.size()<=0)
        {
//            holder.BName.setText("No Data");

        }
        else
        {
            /***** Get each Model object from Arraylist ********/
            tempValues=null;
            tempValues = ( TechnicianModal ) data.get( position );
            holder.txtTechname.setText(tempValues.getFirstName() +" " +tempValues.getLastName());
            holder.txtPhone.setText(tempValues.getPhone());
            holder.txtEmail.setText(tempValues.getEmail());
            if (tempValues.getProfile_image().length() > 0)
            Picasso.with(activity).load(tempValues.getProfile_image())
                    .into(holder.imgTech);
            holder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        Intent intent = new Intent(activity, TechnicianInformation_Activity.class);{
                        intent.putExtra("isedit",true);
                        intent.putExtra("modal",tempValues);
                        intent.putExtra("position",position);
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                }
            });

//            Picasso.with(activity).load(_prefs.getString(Preferences.PROFILE_IMAGE,null)).into(loadtarget);
            /************  Set Model values in Holder elements ***********/

        }
        return vi;
    }

}

