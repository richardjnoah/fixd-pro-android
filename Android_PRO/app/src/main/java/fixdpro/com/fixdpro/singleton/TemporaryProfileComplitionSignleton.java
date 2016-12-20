package fixdpro.com.fixdpro.singleton;

import fixdpro.com.fixdpro.beans.ProfileComplitionValuesBean;

/**
 * Created by Harwinder Paras on 11/5/2016.
 */
public class TemporaryProfileComplitionSignleton {
    ProfileComplitionValuesBean profileComplitionValuesBean = new ProfileComplitionValuesBean();
    public static TemporaryProfileComplitionSignleton singleton = new TemporaryProfileComplitionSignleton();
    public ProfileComplitionValuesBean getProfileComplitionValuesBean() {
        return profileComplitionValuesBean;
    }

    public void setProfileComplitionValuesBean(ProfileComplitionValuesBean profileComplitionValuesBean) {
        this.profileComplitionValuesBean = profileComplitionValuesBean;
    }
    public TemporaryProfileComplitionSignleton(){ }

    /* Static 'instance' method */
    public static TemporaryProfileComplitionSignleton getInstance( ) {

        return singleton;
    }
}
