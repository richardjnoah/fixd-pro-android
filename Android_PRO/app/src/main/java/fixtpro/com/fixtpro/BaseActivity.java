package fixtpro.com.fixtpro;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import fixtpro.com.fixtpro.fragment.FragmentDrawer1;

    public class BaseActivity extends SlidingFragmentActivity {
        private FragmentDrawer1 drawerFragment;
        public BaseActivity() {

        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
    //        setContentView(R.layout.activity_base);
            setBehindContentView(R.layout.menu_frame);
            if (savedInstanceState == null) {
                FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
                drawerFragment = new FragmentDrawer1();
                t.replace(R.id.menu_frame, drawerFragment);
                t.commit();
            } else {
                drawerFragment = (FragmentDrawer1)this.getSupportFragmentManager().findFragmentById(R.id.menu_frame);
            }

            // customize the SlidingMenu
            SlidingMenu sm = getSlidingMenu();
                        DisplayMetrics displayMetrics = new DisplayMetrics();
            WindowManager windowmanager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
            windowmanager.getDefaultDisplay().getMetrics(displayMetrics);
            int deviceWidth = displayMetrics.widthPixels;
            sm.setBehindWidth(deviceWidth-Math.round(deviceWidth/3));
    //        sm.setShadowWidthRes(R.dimen.shadow_width);
    //        sm.setShadowDrawable(R.drawable.shadow);
    //        sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
    //        sm.setFadeDegree(0.35f);
            sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);


        }
    public void setNotficationCounts(){
        drawerFragment.setNotificationCounts();
    }

    }
