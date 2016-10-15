package fixdpro.com.fixdpro.utilites;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fixdpro.com.fixdpro.R;

/**
 * Created by sony on 04-02-2016.
 */
public class Utilities {
    public static boolean hideKeyBoad(Context context, View view) {
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        return true;
    }
    public static SharedPreferences getSharedPreferences(Context _context){
        return _context.getSharedPreferences(Preferences.FIXIT_PRO_PREFERNCES,Context.MODE_PRIVATE);
    }
    public static long  getTimeInMilliseconds(String Time) throws ParseException {
        String input = Time;
        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.ENGLISH).parse(input);
        long milliseconds = date.getTime();
        return milliseconds;

    }
    // validating email id
    public static boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    public static byte[] convertFileToBytes(File file){
        byte[] b = new byte[(int) file.length()];
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(b);
            for (int i = 0; i < b.length; i++) {
                System.out.print((char)b[i]);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found.");
            e.printStackTrace();
        }
        catch (IOException e1) {
            System.out.println("Error Reading The File.");
            e1.printStackTrace();
        }
        return  b;
    }
    public static String getFormattedTimeSlots(String Time){
        if (Time ==  null || Time.equals("null")){
            return "";
        }
        if (Time.equals("8:00:00"))
            return "08:00AM";
        else if (Time.equals("9:00:00"))
            return "09:00AM";
        else if (Time.equals("10:00:00"))
            return "10:00AM";
        else if (Time.equals("11:00:00"))
            return "11:00AM";
        else if (Time.equals("12:00:00"))
            return "12:00PM";
        else if (Time.equals("13:00:00"))
            return "01:00PM";
        else if (Time.equals("14:00:00"))
            return "02:00PM";
        else if (Time.equals("15:00:00"))
            return "03:00PM";
        else if (Time.equals("16:00:00"))
            return "04:00PM";
        else if (Time.equals("17:00:00"))
            return "05:00PM";
        else if (Time.equals("18:00:00"))
            return "06:00PM";
        else if (Time.equals("19:00:00"))
            return "07:00PM";
        else if (Time.equals("20:00:00"))
            return "08:00PM";
        else if (Time.equals("21:00:00"))
            return "09:00PM";
        return "";
    }
    public static  String Am_PMFormat(String Time){
        if (Time ==  null || Time.equals("null")){
            return "";
        }

        final String time = Time;
        String time1 = null;
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            final Date dateObj = sdf.parse(time);
            System.out.println(dateObj);
//            System.out.println(new SimpleDateFormat("k:mm a").format(dateObj));
            final SimpleDateFormat pstFormat =  new SimpleDateFormat("k:mm a");
//            pstFormat.setTimeZone(TimeZone.getTimeZone("PST"));
            time1 = pstFormat.format(dateObj);

        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return time1;

    }

    public static String getDate(String OurDate)
    {
        try
        {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date value = formatter.parse(OurDate);

            SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM,dd yyyy hh:mm a"); //this format changeable
            dateFormatter.setTimeZone(TimeZone.getDefault());
            OurDate = dateFormatter.format(value);

            //Log.d("OurDate", OurDate);
        }
        catch (Exception e)
        {
            OurDate = "00-00-0000 00:00";
        }
        return OurDate;
    }
    public static String convertDate(String date){
        String date1 = null;
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            final Date dateObj = sdf.parse(date);
            date1  = new SimpleDateFormat("EEE, MMM d, yyyy").format(dateObj);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date1;
    }

    public static LatLng getLocationFromAddress(Context context,String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return p1;
    }
    public static String timeMedthod(long timeInteval){

        long deltaSeconds = timeInteval ;

        long deltaMiniutes = deltaSeconds/60;


        int value = 0;
        if (deltaSeconds < 60){
            return (int)deltaSeconds+"s";
        }else if (deltaMiniutes < 60){
            return (int)deltaMiniutes+"m";
        }else if (deltaMiniutes < (24*60)){
            value = (int)(deltaMiniutes/60);
            return value +"h";
        }else if (deltaMiniutes < (24 * 60 * 7)){
            value = (int)deltaMiniutes/(60 * 24);
            return value+"d";
        }else if (deltaMiniutes < (24 * 60 * 31)){
            value = (int)(deltaMiniutes/(60 * 24 * 7));
            return value+"w";
        }else if (deltaMiniutes < (24 * 60 * 365.25)){
            value = (int)(deltaMiniutes/(60 * 24 * 30));
            return value+"mo";
        }else {
            value = (int)(deltaMiniutes/(60 * 24 * 365));
            return value+"yr";
        }

    }
    public static  ArrayList<String> getStateList(){
        ArrayList arrayList = new ArrayList();
        arrayList.add("AL");arrayList.add("AK");arrayList.add("AZ");arrayList.add("AR");arrayList.add("CA");arrayList.add("CO");arrayList.add("CT");arrayList.add("DE");arrayList.add("DC");arrayList.add("FL");arrayList.add("GA");arrayList.add("HI");arrayList.add("ID");arrayList.add("IL");arrayList.add("IN");arrayList.add("IA");arrayList.add("KS");arrayList.add("KY");arrayList.add("LA");arrayList.add("ME");arrayList.add("MD");arrayList.add("MA");arrayList.add("MI");arrayList.add("MN");arrayList.add("MS");arrayList.add("MO");arrayList.add("MT");arrayList.add("NE");arrayList.add("NV");arrayList.add("NH");arrayList.add("NJ");arrayList.add("NM");arrayList.add("NY");arrayList.add("NC");arrayList.add("ND");arrayList.add("OH");arrayList.add("OK");arrayList.add("OR");arrayList.add("PA");arrayList.add("RI");arrayList.add("SC");arrayList.add("SD");arrayList.add("TN");arrayList.add("TX");arrayList.add("UT");arrayList.add("VT");arrayList.add("VA");arrayList.add("WA");arrayList.add("WV");arrayList.add("WI");arrayList.add("WY");arrayList.add("AS");arrayList.add("GU");arrayList.add("MP");arrayList.add("PR");arrayList.add("UM");arrayList.add("VI");
        return arrayList;
    }
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    /***********Copey Files from Assests to Internal/External Storage*************/
    public static void copySqltiteFromAssets(Context context,String filename,String Storingpath) {
        InputStream in = null;
        OutputStream out = null;
        try {
            Log.e("",""+Storingpath);
            in = context.getAssets().open(filename);
            File outFile = new File(Storingpath, filename);
            out = new FileOutputStream(outFile);
            copyFile(in, out);
        } catch(IOException e) {
            Log.e("tag", "Failed to copy asset file: " + filename, e);
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // NOOP
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // NOOP
                }
            }
        }

    }
    private static void copyFile(InputStream in, OutputStream out) throws IOException {

        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
    public static int getApplianceImage(String appliance) {
        if (appliance.equals("Washer"))
            return R.drawable.washer;
        else if (appliance.equals("Dryer"))
            return R.drawable.fan;
        else if (appliance.equals("Cooktop"))
            return R.drawable.cooktop;
        else if (appliance.equals("Oven"))
            return R.drawable.microware;
        else if (appliance.equals("Range Oven"))
            return R.drawable.range;
        else if (appliance.equals("Double Oven"))
            return R.drawable.microware;
        else if (appliance.equals("Range Hood"))
            return R.drawable.range;
        else if (appliance.equals("Refrigerator"))
            return R.drawable.fridge;
        else if (appliance.equals("Dishwasher"))
            return R.drawable.dishwasher;
        else if (appliance.equals("Garbage Disposal"))
            return R.drawable.garbage;
        else if (appliance.equals("Ice Maker"))
            return R.drawable.ice_maker;
        else if (appliance.equals("Stand-alone Freezer"))
            return R.drawable.freezer;
        else if (appliance.equals("Installed Microwave"))
            return R.drawable.microware;
        else if (appliance.equals("Wine Refrigerator"))
            return R.drawable.fridge;
        else if (appliance.equals("Small Refrigerator"))
            return R.drawable.fridge;
       else if (appliance.equals("Mini-Fridge"))
            return R.drawable.fridge;
        else  if (appliance.equals("Re Key"))
            return R.drawable.key_thumb;
        else  if (appliance.equals("Stack Clothes Washer and Dryer"))
            return R.drawable.washer;
        return -1;
    }
    public static int getApplianceImageByName(String appliance) {
        if (appliance.equals("Air Conditioner(Window)"))
            return R.drawable.air_conditioner;
        else if (appliance.equals("Boiler"))
            return R.drawable.boiler;
        else if (appliance.equals("Ceiling Fan"))
            return R.drawable.ceiling_fan;
        else if (appliance.equals("Central Vacuum"))
            return R.drawable.central_vacuum;
        else if (appliance.equals("Clothes Dryer"))
            return R.drawable.clothes_dryer;
        else if (appliance.equals("Clothes Washer") || appliance.equals("Stack Clothes Washer and Dryer"))
            return R.drawable.clothes_washer;
        else if (appliance.equals("Cooktop"))
            return R.drawable.cooktop;
        else if (appliance.equals("Dishwasher"))
            return R.drawable.dishwasher;
        else if (appliance.equals("Disposal"))
            return R.drawable.disposal;
        else if (appliance.equals("Ductless"))
            return R.drawable.ductless;
        else if (appliance.equals("Electrical Panel"))
            return R.drawable.electrical_panel;
        else if (appliance.equals("Faucet"))
            return R.drawable.faucet;
        else if (appliance.equals("Fireplace"))
            return R.drawable.fireplace;
        else if (appliance.equals("Freezer"))
            return R.drawable.freezer;
        else if (appliance.equals("Garage Door Opener"))
            return R.drawable.garage_door_opener;
       else if (appliance.equals("Garbage Disposal"))
            return R.drawable.garbage_disposal;
        else  if (appliance.equals("Hot Water Dispenser"))
            return R.drawable.hot_water_dispenser;
        else  if (appliance.equals("Hot Water Heater"))
            return R.drawable.hot_water_heater;
        else  if (appliance.equals("Ice Maker"))
            return R.drawable.ice_maker;
        else  if (appliance.equals("Light Fixture"))
            return R.drawable.light_fixture;
        else  if (appliance.equals("Light Switch"))
            return R.drawable.light_switch;
        else  if (appliance.equals("Microwave Oven"))
            return R.drawable.microwave_oven;
        else  if (appliance.equals("Outlet"))
            return R.drawable.outlet;
        else  if (appliance.equals("Oven"))
            return R.drawable.oven;
        else  if (appliance.equals("Package Unit"))
            return R.drawable.package_unit;
        else  if (appliance.equals("Pipes"))
            return R.drawable.pipes;
        else  if (appliance.equals("Pool & spa (All)"))
            return R.drawable.pool_spa_all;
        else  if (appliance.equals("Range"))
            return R.drawable.range;
        else  if (appliance.equals("Rangehood"))
            return R.drawable.rangehood;
        else  if (appliance.equals("Re Key"))
            return R.drawable.re_key;
        else  if (appliance.equals("Refrigerator"))
            return R.drawable.refrigerator;
        else  if (appliance.equals("Shower Drain"))
            return R.drawable.shower_drains;
        else  if (appliance.equals("Shower Head"))
            return R.drawable.shower_head;
        else  if (appliance.equals("Shower_ Tub"))
            return R.drawable.shower_tub;
        else  if (appliance.equals("Shower Drain"))
            return R.drawable.washer;
        else  if (appliance.equals("Split System"))
            return R.drawable.split_system;
        else  if (appliance.equals("Sump Pump"))
            return R.drawable.sump_pump;
        else  if (appliance.equals("Toilet"))
            return R.drawable.toilet;
        else  if (appliance.equals("Trash Compactor"))
            return R.drawable.trash_compactor;
        else  if (appliance.equals("Whirlpool"))
            return R.drawable.whirlpool;
        else  if (appliance.equals("Window Unit"))
            return R.drawable.window_unit;
        else  if (appliance.equals("Wine or Beverage Refrigerator"))
            return R.drawable.wine_or_beverage_refrigerator;
        return -1;
    }
    public static String decodeFile(String path,int DESIREDWIDTH, int DESIREDHEIGHT) {
        String strMyImagePath = null;
        Bitmap scaledBitmap = null;

        try {
            // Part 1: Decode image
            Bitmap unscaledBitmap = ScalingUtilities.decodeFile(path, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);

            if (!(unscaledBitmap.getWidth() <= DESIREDWIDTH && unscaledBitmap.getHeight() <= DESIREDHEIGHT)) {
                // Part 2: Scale image
                scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);
            } else {
                unscaledBitmap.recycle();
                return path;
            }

            // Store to tmp file

            String extr = Environment.getExternalStorageDirectory().toString();
            File mFolder = new File(extr + "/FIXD");
            if (!mFolder.exists()) {
                mFolder.mkdir();
            }

            String s = System.currentTimeMillis()+".png";

            File f = new File(mFolder.getAbsolutePath(), s);

            strMyImagePath = f.getAbsolutePath();
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {

                e.printStackTrace();
            } catch (Exception e) {

                e.printStackTrace();
            }

            scaledBitmap.recycle();
        } catch (Throwable e) {
        }

        if (strMyImagePath == null) {
            return path;
        }
        return strMyImagePath;

    }
}
