package fixdpro.com.fixdpro.utilites.chat_core;

/**
 * Created by sahil on 29-04-2016.
 */
public interface ApplicationSessionStateCallback {
    void onStartSessionRecreation();
    void onFinishSessionRecreation(boolean success);
}
