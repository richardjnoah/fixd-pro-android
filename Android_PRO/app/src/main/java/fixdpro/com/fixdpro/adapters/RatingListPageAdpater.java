package fixdpro.com.fixdpro.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.paging.listview.PagingBaseAdapter;

import java.util.ArrayList;

import fixdpro.com.fixdpro.R;
import fixdpro.com.fixdpro.beans.RatingListModal;
import fixdpro.com.fixdpro.utilites.HandlePagingResponse;
import fixdpro.com.fixdpro.utilites.Utilities;
import fixdpro.com.fixdpro.views.RatingBarView;

/**
 * Created by sahil on 10-03-2016.
 */
public class RatingListPageAdpater extends PagingBaseAdapter<RatingListModal> {
    ArrayList<RatingListModal> list;
    Activity activity;
    private static LayoutInflater inflater=null;
    public Resources res;
    RatingListModal tempValues=null;
    Typeface fontfamily;
    HandlePagingResponse handlePagingResponse ;

    public  RatingListPageAdpater(Activity  activity, ArrayList<RatingListModal> list, Resources res,HandlePagingResponse handlePagingResponse){
        this.list = list ;
        this.activity = activity;
        this.handlePagingResponse = handlePagingResponse;
        this.res = res ;
        fontfamily = Typeface.createFromAsset(activity.getAssets(), "HelveticaNeue-Thin.otf");
//
//            Log.i("", "lstheight"+lstheight);
        /***********  Layout inflator to call external xml layout () ***********/
        inflater = ( LayoutInflater )activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public int getCount() {

        if(list.size()<=0)
            return list.size();
        return list.size();
    }
    @Override
    public RatingListModal getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder{

        public TextView txtUserName, txtComments ,txtDate, txtRatingMark, txtKnowlageable,txtCourteous,txtAppearance;;
        public fixdpro.com.fixdpro.views.RatingBarView rating;

    }
    /****** Depends upon list size called for each row , Create each ListView row *****/
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.ratinglistview_item, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.rating = (RatingBarView) vi.findViewById(R.id.custom_ratingbar);
            holder.txtComments = (TextView) vi.findViewById(R.id.txtComments);
            holder.txtDate = (TextView) vi.findViewById(R.id.txtDate);
            holder.txtRatingMark = (TextView) vi.findViewById(R.id.txtRateingMark);
            holder.txtUserName = (TextView) vi.findViewById(R.id.txtUserName);
            holder.txtAppearance = (TextView) vi.findViewById(R.id.txtAppearance);
            holder.txtCourteous = (TextView) vi.findViewById(R.id.txtCourteous);
            holder.txtKnowlageable = (TextView) vi.findViewById(R.id.txtKnowlageable);
            holder.rating.setClickable(false);
            /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        }
        else
            holder=(ViewHolder)vi.getTag();

        if(list.size()<=0)
        {
//            holder.BName.setText("No list");

        }
        else
        {
            /***** Get each Model object from Arraylist ********/
            tempValues=null;
            tempValues =  list.get( position );
            holder.txtUserName.setText("Review by "+tempValues.getCustomers_first_name() + " " + tempValues.getCustomers_last_name().charAt(0) + ".");
            holder.txtComments.setText(tempValues.getComments());

            holder.txtKnowlageable.setText(tempValues.getKnowledgeable());
            holder.txtAppearance.setText(tempValues.getAppearance());
            holder.txtCourteous.setText(tempValues.getCourteous());
            if (!tempValues.getCreated_at().equals("0000-00-00 00:00:00"))
                holder.txtDate.setText(Utilities.getRatingDate(tempValues.getFinished_at()));
            else
                holder.txtDate.setVisibility(View.INVISIBLE);

            /************  Set Model values in Holder elements ***********/
            Float avd_rating  = Float.parseFloat(tempValues.getKnowledgeable()) + Float.parseFloat(tempValues.getCourteous()) + Float.parseFloat(tempValues.getAppearance()) ;
            avd_rating = avd_rating / 3 ;
            holder.rating.setStar((int)Math.round(avd_rating),true);
            holder.txtRatingMark.setText(String.format("%.1f", avd_rating));
            list.get(position).setRatings(avd_rating+"");
            if (position == list.size() - 1){
                if (handlePagingResponse != null)
                    handlePagingResponse.handleChangePage();
            }
        }
        return vi;
    }

}

