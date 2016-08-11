package fixtpro.com.fixtpro.utilites.chat_utils;



import com.quickblox.core.QBSettings;

import fixtpro.com.fixtpro.FixdProApplication;

public class App extends FixdProApplication {
    private static final String TAG = App.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        ActivityLifecycle.init(this);
        QBSettings.getInstance().init(getApplicationContext(), APP_ID, AUTH_KEY, AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);
    }
}