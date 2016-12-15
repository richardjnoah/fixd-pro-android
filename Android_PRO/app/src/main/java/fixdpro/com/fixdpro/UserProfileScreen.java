package fixdpro.com.fixdpro;

import android.support.v7.app.AppCompatActivity;

public class UserProfileScreen extends AppCompatActivity {
   /* TextView txtBack, txtDone,txtTitle , lblAddPhoto, txtCity, txtState, txtTradeskill;
    CircleImageView profile_pic;
    EditText txtFirstName, txtLastName, txtPhone, txtEmail, txtCompany, txtAddress, txtAddress2, txtZip;
    String firstname = "", lastname = "", phone = "", email = "", company = "", address = "", address2 = "", city = "", zip = "", state = "",stateAbre = "", trade_skill  = "",id = "";
    fixtpro.com.fixtpro.views.CircularImageView imgAddPhoto;
    Context _context = this;
    private static final int CAMERA_REQUEST = 1;
    private static final int GALLERY_REQUEST = 2;
    private static final int TRADE_LICENCE_NUMBER = 3;
    public String selectedImagePath=null;
    public String Path, path;
    public Uri selectedImageUri;
    private Typeface fontfamily;
    int finalHeight, finalWidth = 200;
    HashMap<String, String> finalRequestParams = null;
    File profileImageFile = null;
    private boolean isEditMode = true;
    SharedPreferences _prefs = null;
    String error_message = "";

    private Dialog dialog;
    private boolean isPro = true;
    MultipartUtility multipart = null;
    boolean isProfileCompleting = false;
    Dialog dialog1 = null;
    ArrayList<CityBeans> arrayListCityBeans  = new ArrayList<CityBeans>();
//     edit tech| register tech | edit pro | register pro
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_screen);
        _prefs = Utilities.getSharedPreferences(_context);
        setWidgets();
        *//**
         * if Intent is not null then User try to Registering else User try to Update
         *//*
        if (getIntent().getExtras() != null){
            finalRequestParams = (HashMap<String, String>) getIntent().getSerializableExtra("finalRequestParams");
            // check for is pro or Tech
            isPro = getIntent().getBooleanExtra("ispro",false);
            isEditMode = false ;
            if (_prefs.getString(Preferences.ACCOUNT_STATUS,"").equals("DEMO_PRO") || _prefs.getString(Preferences.IS_VARIFIED,"").equals("0")){
                isProfileCompleting = true;
            }
        }else {
            // check for pro or tech
            if (_prefs.getString(Preferences.ROLE, "pro").equals("pro"))
                isPro = true ;
            else
                isPro = false;
            isEditMode = true ;
        }

        fontfamily = Typeface.createFromAsset(getAssets(), "HelveticaNeue-Thin.otf");
        setListeners();
        setTypeface();
        intLayout();

    }

    private void intLayout() {
//        set Common Fields for both Pro and Technician
        txtFirstName.setText(_prefs.getString(Preferences.FIRST_NAME, ""));
        txtLastName.setText(_prefs.getString(Preferences.LAST_NAME, ""));
        txtCompany.setText(_prefs.getString(Preferences.COMPANY_NAME, ""));
        if (isProfileCompleting){
            txtTitle.setText(getString(R.string.any_correction));
        }
        *//**
         * if is technican then hide appropriate fields fron Technicians
         *//*
        if (!isPro){
            txtTradeskill.setVisibility(View.GONE);
            txtPhone.setVisibility(View.GONE);
            txtCompany.setVisibility(View.GONE);
            txtAddress.setVisibility(View.GONE);
            txtAddress2.setVisibility(View.GONE);
            txtCity.setVisibility(View.GONE);
            txtState.setVisibility(View.GONE);
            txtZip.setVisibility(View.GONE);
            if (isEditMode)
                txtEmail.setVisibility(View.GONE);
            else
                txtEmail.setEnabled(false);
        }
        getTradeSkills();
        txtPhone.setText(_prefs.getString(Preferences.PHONE, ""));
        txtEmail.setText(_prefs.getString(Preferences.EMAIL, ""));
        txtAddress.setText(_prefs.getString(Preferences.ADDRESS, ""));
        txtAddress2.setText(_prefs.getString(Preferences.ADDRESS2, ""));
        txtCity.setText(_prefs.getString(Preferences.CITY, ""));
        txtZip.setText(_prefs.getString(Preferences.ZIP, ""));
        if (_prefs.getString(Preferences.PROFILE_IMAGE,null) != null){
            Target loadtarget = null;
            if (loadtarget == null) loadtarget = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    // do something with the Bitmap
                    profile_pic.setImageBitmap(bitmap);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    profile_pic.setImageResource(R.drawable.addphoto_img);
                    Log.e("","Error");
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }

            };

            Picasso.with(this).load(_prefs.getString(Preferences.PROFILE_IMAGE,null)).into(loadtarget);
        }


    }

    private void setWidgets() {
        txtBack = (TextView) findViewById(R.id.txtBack);
        txtDone = (TextView) findViewById(R.id.txtDone);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        lblAddPhoto = (TextView) findViewById(R.id.lblChangePhoto);
        txtTradeskill = (TextView)findViewById(R.id.txtTradeskill);
        txtFirstName = (EditText) findViewById(R.id.txtFirstName);
        txtLastName = (EditText) findViewById(R.id.txtLastName);
        txtPhone = (EditText) findViewById(R.id.txtPhone);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtCompany = (EditText) findViewById(R.id.txtCompany);
        txtAddress = (EditText) findViewById(R.id.txtAddress);
        txtAddress2 = (EditText) findViewById(R.id.txtAddress2);
        txtCity = (TextView) findViewById(R.id.txtCity);
        txtState = (TextView) findViewById(R.id.txtState);
        txtZip = (EditText) findViewById(R.id.txtZip);
        profile_pic = (CircleImageView) findViewById(R.id.profile_pic);
        txtFirstName.requestFocus();
//        imgAddPhoto = (fixtpro.com.fixtpro.views.CircularImageView)findViewById(R.id.imgAddPhoto);
//        imgAddPhoto.setImageDrawable(getResources().getDrawable(R.drawable.addphoto_img));
    }

    private void setTypeface() {
        lblAddPhoto.setTypeface(fontfamily);
        txtFirstName.setTypeface(fontfamily);
        txtPhone.setTypeface(fontfamily);
        txtEmail.setTypeface(fontfamily);
        txtCompany.setTypeface(fontfamily);
        txtAddress.setTypeface(fontfamily);
        txtAddress2.setTypeface(fontfamily);
        txtCity.setTypeface(fontfamily);
        txtState.setTypeface(fontfamily);
        txtZip.setTypeface(fontfamily);
        txtLastName.setTypeface(fontfamily);
        txtBack.setTypeface(fontfamily);
        txtDone.setTypeface(fontfamily);
        txtTradeskill.setTypeface(fontfamily);
    }

    private void setListeners() {
        txtTradeskill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(_context, SelectTradeLicence.class);
                startActivityForResult(intent, TRADE_LICENCE_NUMBER);

            }
        });
        txtZip.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (v.getId() == R.id.txtZip && actionId == EditorInfo.IME_ACTION_DONE ){
                Utilities.hideKeyBoad(_context,v);
                GetApiResponseAsyncNew apiResponseAsync = new GetApiResponseAsyncNew(Constants.BASE_URL,"POST",zipResponseListener,zipExceptionListener,UserProfileScreen.this,"Getting");
                apiResponseAsync.execute(getZipRequestParams());
        }
        return true;
            }
        });
        txtCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call Zip Api
                if (arrayListCityBeans.size() == 0){
                    if (txtZip.getText().toString().trim().equals("")) {
                        showAlertDialog("Fixed!", "Please enter zip code.");
                    } else {
                        Utilities.hideKeyBoad(_context, v);
                        GetApiResponseAsyncNew apiResponseAsync = new GetApiResponseAsyncNew(Constants.BASE_URL, "POST", zipResponseListener, zipExceptionListener, UserProfileScreen.this, "Getting");
                        apiResponseAsync.execute(getZipRequestParams());
                    }
                }else {
                    showcityList();
                }

            }
        });
        txtState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showStateList();
// Call Zip Api
//                if (txtZip.getText().toString().trim().equals("")) {
//                    showAlertDialog("Fixed!", "Please enter zip code.");
//                } else {
//                    Utilities.hideKeyBoad(_context, v);
//                    GetApiResponseAsyncNew apiResponseAsync = new GetApiResponseAsyncNew(Constants.BASE_URL, "POST", zipResponseListener, zipExceptionListener, UserProfileScreen.this, "Getting");
//                    apiResponseAsync.execute(getZipRequestParams());
//                }
            }
        });
        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCamraGalleryPopUp();
            }
        });
        lblAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCamraGalleryPopUp();
            }
        });
        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txtDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstname = txtFirstName.getText().toString().trim();
                lastname = txtLastName.getText().toString().trim();
                phone = txtPhone.getText().toString().trim();
                email = txtEmail.getText().toString().trim();
                company = txtCompany.getText().toString().trim();
                address = txtAddress.getText().toString().trim();
                address2 = txtAddress2.getText().toString().trim();
                city = txtCity.getText().toString().trim();
                zip = txtZip.getText().toString().trim();

                if (firstname.length() == 0) {
                    showAlertDialog("Fixd-Pro", "Please enter the first name.");
                    return;
                } else if (lastname.length() == 0) {
                    showAlertDialog("Fixd-Pro", "Please enter the last name.");
                    return;
                } else if (email.length() == 0) {
                    showAlertDialog("Fixd-Pro", "Please enter the email address.");
                    return;
                } else if (!Utilities.isValidEmail(email)) {
                    showAlertDialog("Fixd-Pro", "Your email address seems to invalid, Please try again.");
                    return;
                }
//                else if (company.length() == 0 && (txtCompany.getVisibility() == View.VISIBLE)) {
//                    showAlertDialog("Fixd-Pro", "Please enter the company name.");
//                    return;
//                }
                else if (address.length() == 0 && (txtAddress.getVisibility() == View.VISIBLE)) {
                    showAlertDialog("Fixd-Pro", "Please enter the address.");
                    return;
                } else if (city.length() == 0 && (txtCity.getVisibility() == View.VISIBLE)) {
                    showAlertDialog("Fixd-Pro", "Please enter the city.");
                    return;
                } else if (zip.length() == 0 && (txtZip.getVisibility() == View.VISIBLE)) {
                    showAlertDialog("Fixd-Pro", "Please enter the zip code.");
                    return;
                } else if (false && !isEditMode) {
                    showAlertDialog("Fixd-Pro", "Zip code length.");
                    return;
                } else if (trade_skill.length() == 0 &&(txtTradeskill.getVisibility() == View.VISIBLE)) {
                    showAlertDialog("Fixd-Pro", "Please enter skill.");
                    return;
                }else {
//                  do it
                    Utilities.hideKeyBoad(_context, UserProfileScreen.this.getCurrentFocus());
                    *//**
                     * if user not editing then direct regiter else update user
                     *//*
                    if (!isEditMode  && isPro && !isProfileCompleting){
                        // Resgister Pro
                        registerPro();
                    }else if (!isEditMode  && !isPro && !isProfileCompleting){
                        handler.sendEmptyMessage(0);
                    }
                    else if (isProfileCompleting){
//                            complete profile
                            handler.sendEmptyMessage(3);
                    }else {
                        // is in edit mode
                        updateUser();
                    }



//                    if (!isEditMode) {
//                        if (isPro) {
//                            GetApiResponseAsync responseAsync = new GetApiResponseAsync("POST", checkIfEmailExistsListener, UserProfileScreen.this, "Loading");
//                            responseAsync.execute(getCheckEmailRequestParams());
//                        } else {
//                            handler.sendEmptyMessage(0);
//                        }
//
//                    } else {
////                        is in edit mode
//                        new AsyncTask<Void, Void, String>() {
//
//                            @Override
//                            protected String doInBackground(Void... params) {
//                                createMultiPartRequest(getProfileUpdateParameters());
//                                return null;
//                            }
//
//                            @Override
//                            protected void onPostExecute(String s) {
//                                super.onPostExecute(s);
//                                GetApiResponseAsyncMutipart getApiResponseAsync = new GetApiResponseAsyncMutipart(multipart, profileUpdateListener,exceptionListener, UserProfileScreen.this, "Updating");
//                                getApiResponseAsync.execute();
//                            }
//                        }.execute();
//                    }
                }


            }
        });


//        txtZip.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(_context, WorkingRadiusActivity.class);
//                startActivity(i);
//            }
//        });

    }
    private MultipartUtility createMultiPartRequest(HashMap<String,String> hashMap){

        try {
            multipart = new MultipartUtility(Constants.BASE_URL, Constants.CHARSET);
            for ( String key : hashMap.keySet() ) {
                multipart.addFormField(key, hashMap.get(key));
//                Log.e("key"+key,"finalRequestParams.get(key)"+finalRequestParams.get(key));
            }

                if (selectedImagePath != null){
                    multipart.addFilePart("data[technicians][profile_image]",new File(selectedImagePath));
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return multipart;
    }
    ResponseListener profileUpdateListener = new ResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("", "Response" + Response.toString());
            try {
                if (Response.getString("STATUS").equals("SUCCESS")) {
                    SharedPreferences.Editor editor = _prefs.edit();
                    editor.putString(Preferences.FIRST_NAME, firstname);
                    editor.putString(Preferences.LAST_NAME, lastname);
                    editor.putString(Preferences.EMAIL, email);
                    editor.putString(Preferences.PHONE, phone);
                    editor.putString(Preferences.CITY, city);
                    editor.putString(Preferences.ZIP, zip);
                    editor.putString(Preferences.ADDRESS, address);
                    editor.putString(Preferences.ADDRESS2, address2);
                    editor.putString(Preferences.COMPANY_NAME, company);
                    JSONObject profile_image = null ;
                    profile_image = Response.getJSONObject("RESPONSE").getJSONObject("users").getJSONObject("technicians").getJSONObject("profile_image");
                    if (!profile_image.isNull("original")){
                        String image_original =  profile_image.getString("original");
                        editor.putString(Preferences.PROFILE_IMAGE, image_original);
                    }
                    editor.commit();
                    finish();
                } else {
                    JSONObject errors = Response.getJSONObject("ERRORS");
                    Iterator<String> keys = errors.keys();
                    if (keys.hasNext()) {
                        String key = (String) keys.next();
                        error_message = errors.getString(key);
                        handler.sendEmptyMessage(2);
                    }
                }

            } catch (JSONException e) {

            }
        }
    };
    ExceptionListener exceptionListenerProRegistration = new ExceptionListener(){

        @Override
        public void handleException(int exceptionStatus) {
            handler.sendEmptyMessage(exceptionStatus);
        }
    };
    ResponseListener proRegistrationListener = new ResponseListener() {
        @Override
        public void handleResponse(JSONObject Response) {
            Log.e("", "Response" + Response.toString());
            try {
                String STATUS = Response.getString("STATUS");
                if (STATUS.equals("SUCCESS")) {
                    JSONObject RESPONSE = Response.getJSONObject("RESPONSE");
                    String Token = Response.getJSONObject("RESPONSE").getString("token");
                    String id = Response.getJSONObject("RESPONSE").getJSONObject("users").getString("id");
                    String role = Response.getJSONObject("RESPONSE").getJSONObject("users").getString("role");
                    String email = Response.getJSONObject("RESPONSE").getJSONObject("users").getString("email");
                    String phone = Response.getJSONObject("RESPONSE").getJSONObject("users").getString("phone");
                    String  has_card = Response.getJSONObject("RESPONSE").getJSONObject("users").getString("has_card");
                    String account_status = Response.getJSONObject("RESPONSE").getJSONObject("users").getString("account_status");
                    Log.e("AUTH TOKEN", Token);
                    Log.e("ROLE", role);
                    JSONObject pro_settings;
                    SharedPreferences.Editor editor = _prefs.edit();
                    editor.putString(Preferences.ID, id);
                    editor.putString(Preferences.ROLE, role);
                    editor.putString(Preferences.AUTH_TOKEN, Token);
                    editor.putString(Preferences.EMAIL, email);
                    editor.putString(Preferences.PHONE, phone);
                    editor.putString(Preferences.HAS_CARD, has_card);
                    editor.putString(Preferences.ACCOUNT_STATUS, account_status);
                    if (!Response.getJSONObject("RESPONSE").getJSONObject("users").isNull("pros")){
                        JSONObject pros = null;
                        pros = Response.getJSONObject("RESPONSE").getJSONObject("users").getJSONObject("pros");
                        String city = pros.getString("city");
                        String state = pros.getString("state");
                        String zip = pros.getString("zip");
                        String address = pros.getString("address");
                        String address2 = pros.getString("address_2");
                        String hourly_rate = pros.getString("hourly_rate");
                        String company_name = pros.getString("company_name");
                        String ein_number="" ;
                        if (!pros.isNull("ein_number"))
                        ein_number = pros.getString("ein_number");
                        String bank_name = pros.getString("bank_name");
                        String bank_routing_number = pros.getString("bank_routing_number");
                        String bank_account_number = pros.getString("bank_account_number");
                        String bank_account_type = pros.getString("bank_account_type");
                        String insurance = pros.getString("insurance");
                        String insurance_policy = pros.getString("insurance_policy");
                        String isvarified = pros.getString("verified");
                        String working_radius_miles = pros.getString("working_radius_miles");
                        String is_pre_registered = pros.getString("is_pre_registered");
                        String latitude = pros.getString("latitude");
                        String longitude = pros.getString("longitude");
                        String avg_rating = pros.getString("avg_rating");
                        editor.putString(Preferences.CITY, city);
                        editor.putString(Preferences.STATE, state);
                        editor.putString(Preferences.ZIP, zip);
                        editor.putString(Preferences.ADDRESS, address);
                        editor.putString(Preferences.ADDRESS2, address2);
                        editor.putString(Preferences.HOURLY_RATE, hourly_rate);
                        editor.putString(Preferences.COMPANY_NAME, company_name);
                        editor.putString(Preferences.EIN_NUMEBR, ein_number);
                        editor.putString(Preferences.BANK_NAME, bank_name);
                        editor.putString(Preferences.BANK_ROUTING_NUMBER, bank_routing_number);
                        editor.putString(Preferences.BANK_ACCOUNT_NUMBER, bank_account_number);
                        editor.putString(Preferences.BANK_ACCOUNT_TYPE, bank_account_type);
                        editor.putString(Preferences.INSURANCE, insurance);
                        editor.putString(Preferences.INSURANCE_POLICY, insurance_policy);
                        editor.putString(Preferences.IS_VARIFIED, isvarified);
                        editor.putString(Preferences.WORKING_RADIUS_MILES, working_radius_miles);
                        editor.putString(Preferences.IS_PRE_REGISTERED, is_pre_registered);
                        editor.putString(Preferences.LATITUDE, latitude);
                        editor.putString(Preferences.LONGITUDE, longitude);
                        editor.putString(Preferences.AVERAGE_RATING, avg_rating);
                    }
                    if (!Response.getJSONObject("RESPONSE").getJSONObject("users").isNull("services")){
                        JSONArray services = null;
                        services = Response.getJSONObject("RESPONSE").getJSONObject("users").getJSONArray("services");
                        editor.putString(Preferences.SERVICES_JSON_ARRAY, services.toString());
                    }
                    if (!Response.getJSONObject("RESPONSE").getJSONObject("users").isNull("pro_settings")) {
                        pro_settings = Response.getJSONObject("RESPONSE").getJSONObject("users").getJSONObject("pro_settings");
                        editor.putString(Preferences.SETTING_ID, pro_settings.getString("id"));
                        editor.putString(Preferences.LOCATION_SERVICE, pro_settings.getString("location_services"));
                        editor.putString(Preferences.TEXT_MESSAGING, pro_settings.getString("text_messaging"));
                        editor.putString(Preferences.AVAILABLE_JOBS_NOTIFICATION, pro_settings.getString("available_jobs_notification"));
                        editor.putString(Preferences.JOB_WON_NOTIFICATION, pro_settings.getString("job_won_notification"));
                        editor.putString(Preferences.JOB_LOST_NOTIFICATION, pro_settings.getString("job_lost_notification"));
                        editor.putString(Preferences.JOB_RESECHDULED, pro_settings.getString("job_rescheduled"));
                        editor.putString(Preferences.JOB_CANCELLED, pro_settings.getString("job_canceled"));

                    }
                    if (!Response.getJSONObject("RESPONSE").getJSONObject("users").isNull("technicians")){
                        JSONObject technicians = null;
                        technicians = Response.getJSONObject("RESPONSE").getJSONObject("users").getJSONObject("technicians");
                        String first_name = technicians.getString("first_name");
                        String last_name = technicians.getString("last_name");
                        String social_security_number = technicians.getString("social_security_number");
                        String years_in_business = technicians.getString("years_in_business");
                        String trade_license_number = technicians.getString("trade_license_number");
                        editor.putString(Preferences.FIRST_NAME, first_name);
                        editor.putString(Preferences.LAST_NAME, last_name);
                        editor.putString(Preferences.SOCIAL_SECURITY_NUMBER, social_security_number);
                        editor.putString(Preferences.YEARS_IN_BUSINESS, years_in_business);
                        editor.putString(Preferences.TRADE_LICENSE_NUMBER, trade_license_number);
                        editor.putBoolean(Preferences.ISLOGIN, true);
                        if (!Response.getJSONObject("RESPONSE").getJSONObject("users").getJSONObject("technicians").isNull("profile_image")){
                            JSONObject profile_image = null;
                            profile_image = Response.getJSONObject("RESPONSE").getJSONObject("users").getJSONObject("technicians").getJSONObject("profile_image");
                            if (profile_image.has("original")) {
                                String image_original = profile_image.getString("original");
                                editor.putString(Preferences.PROFILE_IMAGE, image_original);
                            }
                        }
                    }
                    if (!Response.getJSONObject("RESPONSE").getJSONObject("users").isNull("quickblox_accounts")){
                        JSONObject quickblox_accounts = null;
                        quickblox_accounts = Response.getJSONObject("RESPONSE").getJSONObject("users").getJSONObject("quickblox_accounts");
                        String account_id = quickblox_accounts.getString("account_id");
                        String login = quickblox_accounts.getString("login");
                        String password = quickblox_accounts.getString("qb_password");
                        editor.putString(Preferences.QB_ACCOUNT_ID, account_id);
                        editor.putString(Preferences.QB_LOGIN, login);
                        editor.putString(Preferences.QB_PASSWORD, password);
                    }
                    editor.commit();
                    handler.sendEmptyMessage(5);
                } else {
                    JSONObject errors = Response.getJSONObject("ERRORS");
                    Iterator<String> keys = errors.keys();
                    if (keys.hasNext()) {
                        String key = (String) keys.next();
                        error_message = errors.getString(key);
                    }
                    handler.sendEmptyMessage(0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    ExceptionListener exceptionListener = new ExceptionListener(){

        @Override
        public void handleException(int exceptionStatus) {
            handler.sendEmptyMessage(exceptionStatus);
        }
    };
    ResponseListener checkIfEmailExistsListener = new ResponseListener() {
        @Override
        public void handleResponse(JSONObject response) {
            Log.e("", "Response" + response.toString());
            try {
                String status = response.getString("STATUS");
                if (status.equals("SUCCESS")) {
                    JSONArray Data = response.getJSONArray("RESPONSE");
//                    Check if Phone Already Exist
                    if (Data.length() == 0) {
                        handler.sendEmptyMessage(0);
                    } else {
                        handler.sendEmptyMessage(1);
                    }
                } else {
//              Check for Errors
                    JSONObject errors = response.getJSONObject("ERRORS");
                    Iterator<String> keys = errors.keys();
                    if (keys.hasNext()) {
                        String key = (String) keys.next();
                        error_message = errors.getString(key);
                        handler.sendEmptyMessage(2);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0: {
                    if (!isEditMode) {
                            Intent intent = new Intent(_context, Add_Driver_LicScreen.class);

                            finalRequestParams.put("last_name", lastname);
                            finalRequestParams.put("first_name", firstname);
                            finalRequestParams.put("email", email);
                            intent.putExtra("finalRequestParams", finalRequestParams);
                            intent.putExtra("image_profile", selectedImagePath);
                            startActivity(intent);
                    } else {
                        finish();
                    }
                    break;
                }
                case 1: {
                    showAlertDialog("Fixd-Pro", "Email already exist.");
                    break;
                }
                case 2:{
                    if (arrayListCityBeans.size() > 1){
                        showcityList();
                    }else {
                    txtState.setText(state);
                    txtCity.setText(city);
                    }

                    break;
                }
                case 3:{
                    Intent intent = new Intent(_context, Add_Driver_LicScreen.class);
                    finalRequestParams.put("data[technicians][first_name]", firstname);
                    finalRequestParams.put("data[technicians][last_name]", lastname);
//                    finalRequestParams.put("data[users][email]", email);
//        finalRequestParams.put("data[pros][company_name]", company);
                    finalRequestParams.put("data[pros][address]", address);
//                    finalRequestParams.put("data[pros][address_2]", address2);
                    finalRequestParams.put("data[pros][city]", city);
                    finalRequestParams.put("data[pros][zip]", zip);
                    finalRequestParams.put("data[pros][state]", stateAbre);
                    finalRequestParams.put("token", _prefs.getString(Preferences.AUTH_TOKEN,""));
                    for (int i = 0 ; i < TradeSkillSingleTon.getInstance().getListChecked().size() ; i++){
                        finalRequestParams.put("data[services]["+i+"]",TradeSkillSingleTon.getInstance().getListChecked().get(i).getId()+"");
                    }
                    intent.putExtra("finalRequestParams", finalRequestParams);
                    intent.putExtra("image_profile", selectedImagePath);
                    intent.putExtra("image_profile", selectedImagePath);
                    startActivity(intent);
                    break;
                }
                case 4:{
                    showAlertDialog("Fixd-Pro",error_message);
                    txtState.setText("");
                    txtCity.setText("");
                    break;
                }case 5:{
                    Intent intent = new Intent(UserProfileScreen.this, HomeScreenNew.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    break;
                }
                case 500:{
                    showAlertDialog("Fixd-Pro", "Server Error 500.");
                }
                default: {
                    break;
                }
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if (requestCode == CAMERA_REQUEST) {
                try {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    Uri uri = data.getData();
                    Picasso.with(this).load(uri)
                            .resize(172, 172).centerCrop().into(profile_pic);
                    selectedImagePath = ImageHelper2.getRealPathFromURI(uri.toString(), _context);
//                selectedImageUri = getImageUri(this, photo);
//                if (selectedImageUri == null)
//                    return;
//                Path = ImageHelper2.compressImage(selectedImageUri, this);
//                profile_pic.setImageBitmap(ImageHelper2.decodeSampledBitmapFromFile(Path, 400, 400));
//                    profile_pic.setImageBitmap(photo);
                    lblAddPhoto.setText("Change Photo");

//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                ImageHelper2.decodeSampledBitmapFromFile(Path, 400, 200).compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                byte[] b = baos.toByteArray();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (requestCode == GALLERY_REQUEST) {
                    Uri selectedImageUri = data.getData();
                    selectedImagePath = getPath(selectedImageUri);
                    Picasso.with(this).load(selectedImageUri)
                        .resize(150, 150).centerCrop().into(profile_pic);
                    Log.e("UserProfileScreen", "Image Path : " + selectedImagePath);
//                    profile_pic.setImageURI(selectedImageUri);
                    lblAddPhoto.setText("Change Photo");
            }
             if (requestCode == TRADE_LICENCE_NUMBER) {
                Log.e("", "TRADE_LICENCE_NUMBER" + TRADE_LICENCE_NUMBER);
                setTradeSkills();
            }
        }
    }
    private void setTradeSkills(){
        ArrayList<SkillTrade> skillTrades  = TradeSkillSingleTon.getInstance().getListChecked();
        txtTradeskill.setText("");
        trade_skill = "";
        if (skillTrades.size() > 0){
//            txtTradeskill.setText(skillTrades.get(0).getTitle());
            for (int i = 0 ; i < skillTrades.size() - 1 ; i++){
                if (skillTrades.get(i).isChecked())
                    trade_skill = trade_skill + skillTrades.get(i).getTitle() + ", ";
            }
            if (skillTrades.get(skillTrades.size() - 1).isChecked())
                trade_skill = trade_skill + skillTrades.get(skillTrades.size() - 1).getTitle();
            txtTradeskill.setText(trade_skill);
        }
    }
    // get path from the gallery...
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    // get the path/....
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        Log.e("", "path............" + path);
        return Uri.parse(path);
    }

    private void showAlertDialog(String Title, String Message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                _context);
        // set title
        alertDialogBuilder.setTitle(Title);
        // set dialog message
        alertDialogBuilder
                .setMessage(Message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        dialog.cancel();
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    private void openCamera() {
        Intent camraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camraIntent, CAMERA_REQUEST);
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image*//*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GALLERY_REQUEST);
    }

    private HashMap<String, String> getCheckEmailRequestParams() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("api", "read");
        hashMap.put("object", "users");
        hashMap.put("select", "id");
        hashMap.put("where[email]", email);
        return hashMap;
    }

    private HashMap<String, String> getProfileUpdateParameters() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("api", "update");
        if (_prefs.getString(Preferences.ROLE, "").equals("pro"))
            hashMap.put("object", "pros");
        else
            hashMap.put("object", "technicians");
        hashMap.put("token", _prefs.getString(Preferences.AUTH_TOKEN, ""));
        hashMap.put("data[technicians][first_name]", firstname);
        hashMap.put("data[technicians][last_name]", lastname);
        hashMap.put("data[email]", email);
//        hashMap.put("data[pros][company_name]", company);
        hashMap.put("data[pros][address]", address);
        hashMap.put("data[pros][address_2]", address2);
        hashMap.put("data[pros][city]", city);
        hashMap.put("data[pros][zip]", zip);
        return hashMap;
    }


    private HashMap<String,String> getProRegisterParameters(){

        finalRequestParams.put("data[technicians][first_name]", firstname);
        finalRequestParams.put("data[technicians][last_name]", lastname);
        finalRequestParams.put("data[users][email]", email);
//        finalRequestParams.put("data[pros][company_name]", company);
        finalRequestParams.put("data[pros][address]", address);
        finalRequestParams.put("data[pros][address_2]", address2);
        finalRequestParams.put("data[pros][city]", city);
        finalRequestParams.put("data[pros][zip]", zip);
        finalRequestParams.put("data[pros][state]", stateAbre);
        for (int i = 0 ; i < TradeSkillSingleTon.getInstance().getListChecked().size() ; i++){
            finalRequestParams.put("data[services]["+i+"]",TradeSkillSingleTon.getInstance().getListChecked().get(i).getId()+"");
        }
       return finalRequestParams;
    }
    *//*Create Camra Gallery PopUP*//*
    private void showCamraGalleryPopUp() {
        dialog = new Dialog(_context);
        dialog = new Dialog(_context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_camra_gallery);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // set the custom dialog components - text, image and button
        ImageView img_close = (ImageView) dialog.findViewById(R.id.img_close);
        TextView txtTakePicture = (TextView) dialog.findViewById(R.id.txtTakePicture);
        TextView txtCamera = (TextView) dialog.findViewById(R.id.txtCamera);
        TextView txtGallery = (TextView) dialog.findViewById(R.id.txtGallery);
        // set the typeface...
        txtCamera.setTypeface(fontfamily);
        txtGallery.setTypeface(fontfamily);
        txtTakePicture.setTypeface(fontfamily);
        // set the click listner...
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        txtCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                openCamera();
            }
        });
        txtGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                openGallery();
            }
        });
        dialog.show();
    }
    private HashMap<String,String> getZipRequestParams() {
        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("api","read");
        hashMap.put("object", "zipcodes");
        hashMap.put("per_page", 20+"");
        hashMap.put("page", 1+"");
        hashMap.put("where[zipcode]", txtZip.getText().toString());
        return hashMap;
    }
//    @Override
//    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//        if (v.getId() == R.id.txtZip && actionId == EditorInfo.IME_ACTION_DONE ){
//            Utilities.hideKeyBoad(_context,v);
//            GetApiResponseAsyncNew apiResponseAsync = new GetApiResponseAsyncNew(Constants.BASE_URL,"POST",zipResponseListener,zipExceptionListener,UserProfileScreen.this,"Getting");
//            apiResponseAsync.execute(getZipRequestParams());
//        }
//        return true;
//    }

    IHttpResponseListener zipResponseListener = new IHttpResponseListener() {
        @Override
        public void handleResponse(JSONObject response) {
            Log.e("", "response" + response);
            try {
                if (response.getString("STATUS").equals("SUCCESS")) {
                    JSONArray results = response.getJSONObject("RESPONSE").getJSONArray("results");
                    if (results.length() == 0){
                        error_message = "Please enter valid zip code" ;
                        handler.sendEmptyMessage(4);
                    }else {
                        arrayListCityBeans.clear();
                        for (int i = 0 ; i < results.length() ; i++){
                        JSONObject jsonObject = results.getJSONObject(i);
                            CityBeans cityBeans = new CityBeans();
                            city = jsonObject.getString("cityname");
                            state = jsonObject.getString("statename");
                            stateAbre = jsonObject.getString("stateabbr");
                            id = jsonObject.getString("id");
                            cityBeans.setId(id);
                            cityBeans.setCityname(city);
                            cityBeans.setStateabbr(stateAbre);
                            cityBeans.setStatename(state);
                            arrayListCityBeans.add(cityBeans);
                        }

                        handler.sendEmptyMessage(2);
                    }

                }
                else{
                    JSONObject errors = response.getJSONObject("ERRORS");
                    Iterator<String> keys = errors.keys();
                    if (keys.hasNext()) {
                        String key = (String) keys.next();
                        error_message = errors.getString(key);
                    }
                    handler.sendEmptyMessage(1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    IHttpExceptionListener zipExceptionListener = new IHttpExceptionListener() {

        @Override
        public void handleException(String exception) {
            error_message = exception+"" ;
            handler.sendEmptyMessage(1);
        }
    };

    private void getTradeSkills(){
        String tradeSkills  = _prefs.getString(Preferences.SERVICES_JSON_ARRAY,"");
        if (tradeSkills.length() != 0){
            try {
                JSONArray serviceArray = new JSONArray(tradeSkills);
                for (int i = 0 ; i < serviceArray.length() ; i++){
                    Log.e("","");
                    for (int k = 0 ; k < TradeSkillSingleTon.getInstance().getList().size(); k++){
                        if ((TradeSkillSingleTon.getInstance().getList().get(k).getId()+"").equals(serviceArray.getString(i))){
                            TradeSkillSingleTon.getInstance().getList().get(k).setIsChecked(true);
                        }
                    }
                }
                setTradeSkills();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private void registerPro(){
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                createMultiPartRequest(getProRegisterParameters());
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                GetApiResponseAsyncMutipart getApiResponseAsync = new GetApiResponseAsyncMutipart(multipart, proRegistrationListener,exceptionListenerProRegistration, UserProfileScreen.this, "Registring");
                getApiResponseAsync.execute();
            }
        }.execute();
    }
    private void updateUser(){
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                createMultiPartRequest(getProfileUpdateParameters());
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                GetApiResponseAsyncMutipart getApiResponseAsync = new GetApiResponseAsyncMutipart(multipart, profileUpdateListener,exceptionListener, UserProfileScreen.this, "Updating");
                getApiResponseAsync.execute();
            }
        }.execute();
    }
    private void showStateList(){

        dialog1 = new Dialog(_context);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.layout_tellus_more);
        dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // set the custom dialog1 components -listview
        ListView lst_TellUsMore = (ListView) dialog1.findViewById(R.id.lst_TellUsMore);
                *//*To set the Adapter*//*
        StateListAdapter  mAdp = new StateListAdapter(Utilities.getStateList(), UserProfileScreen.this);
        lst_TellUsMore.setAdapter(mAdp);
        lst_TellUsMore.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            dialog1.dismiss();
            txtState.setText(Utilities.getStateList().get(position));
            state = Utilities.getStateList().get(position);
            }
        });
        dialog1.show();
    }

    private void showcityList(){

        dialog1 = new Dialog(_context);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.layout_tellus_more);
        dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // set the custom dialog1 components -listview
        ListView lst_TellUsMore = (ListView) dialog1.findViewById(R.id.lst_TellUsMore);
                *//*To set the Adapter*//*
        CityListAdapter mAdp = new CityListAdapter(arrayListCityBeans, UserProfileScreen.this);
        lst_TellUsMore.setAdapter(mAdp);
        lst_TellUsMore.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog1.dismiss();
                txtState.setText(arrayListCityBeans.get(position).getStateabbr());
                state = arrayListCityBeans.get(position).getStateabbr();
                txtCity.setText(arrayListCityBeans.get(position).getCityname());
                city = arrayListCityBeans.get(position).getCityname();
            }
        });
        dialog1.show();
    }*/
}
