package fixtpro.com.fixtpro.singleton;

/**
 * Created by sahil on 28-04-2016.
 */
public class QuickBloxSinglton {
    private static QuickBloxSinglton singleton = new QuickBloxSinglton( );

    /* A private Constructor prevents any other 
     * class from instantiating.
     */



    public static final String STICKER_API_KEY = "847b82c49db21ecec88c510e377b452c";

    public static final String USER_LOGIN = "igorquickblox44";
    public static final String USER_PASSWORD = "igorquickblox44";
    private QuickBloxSinglton(){ }

    /* Static 'instance' method */
    public static QuickBloxSinglton getInstance( ) {

        return singleton;
    }
    /* Other methods protected by singleton-ness */
    protected static void demoMethod( ) {
        System.out.println("demoMethod for singleton");
    }
}
