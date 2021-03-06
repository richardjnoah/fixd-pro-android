package fixdpro.com.fixdpro;

/**
 * Created by sahil on 8/3/2016.
 */

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;

import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;

import fixdpro.com.fixdpro.fragment.ProgressDialogFragment;
import fixdpro.com.fixdpro.utilites.chat_utils.ErrorUtils;
import fixdpro.com.fixdpro.utilites.chat_utils.SharedPreferencesUtil;
import fixdpro.com.fixdpro.utilites.chat_utils.chat.ChatHelper;
import fixdpro.com.fixdpro.utilites.chat_utils.qb.QbAuthUtils;
import fixdpro.com.fixdpro.utilites.chat_utils.qb.QbSessionStateCallback;

abstract  public class BaseActivityChat extends CoreBaseActivity implements QbSessionStateCallback {
    private static final String TAG = BaseActivity.class.getSimpleName();

    private static final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    protected ActionBar actionBar;
    protected boolean isAppSessionActive;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        actionBar = getSupportActionBar();

        boolean wasAppRestored = savedInstanceState != null;
        boolean isQbSessionActive = QbAuthUtils.isSessionActive();
        final boolean needToRestoreSession = wasAppRestored || !isQbSessionActive;
        Log.v(TAG, "wasAppRestored = " + wasAppRestored);
        Log.v(TAG, "isQbSessionActive = " + isQbSessionActive);

        // Triggering callback via Handler#post() method
        // to let child's code in onCreate() to execute first
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (needToRestoreSession) {
                    recreateChatSession();
                    isAppSessionActive = false;
                } else {
                    onSessionCreated(true);
                    isAppSessionActive = true;
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putInt("dummy_value", 0);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    protected abstract View getSnackbarAnchorView();

    protected Snackbar showErrorSnackbar(@StringRes int resId, Exception e,
                                         View.OnClickListener clickListener) {
        return ErrorUtils.showSnackbar(getSnackbarAnchorView(), resId, e,
                R.string.dlg_retry, clickListener);
    }

    private void recreateChatSession() {
        Log.d(TAG, "Need to recreate chat session");

        QBUser user = SharedPreferencesUtil.getQbUser();
        if (user == null) {
            throw new RuntimeException("User is null, can't restore session");
        }

        reloginToChat(user);
    }

    private void reloginToChat(final QBUser user) {
        ProgressDialogFragment.show(getSupportFragmentManager(), R.string.dlg_restoring_chat_session);

        ChatHelper.getInstance().login(user, new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void result, Bundle bundle) {
                Log.v(TAG, "Chat login onSuccess()");
                isAppSessionActive = true;
                onSessionCreated(true);

                ProgressDialogFragment.hide(getSupportFragmentManager());
            }

            @Override
            public void onError(QBResponseException e) {
                isAppSessionActive = false;
                ProgressDialogFragment.hide(getSupportFragmentManager());
                Log.w(TAG, "Chat login onError(): " + e);
                showErrorSnackbar(R.string.error_recreate_session, e,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                reloginToChat(user);
                            }
                        });
                onSessionCreated(false);
            }
        });
    }
}
