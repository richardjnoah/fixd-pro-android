package fixdpro.com.fixdpro.singleton;



import java.util.ArrayList;

import fixdpro.com.fixdpro.beans.Brands;

/**
 * Created by sahil on 20-06-2016.
 */
public class BrandNamesSingleton {
    private static BrandNamesSingleton singleton = new BrandNamesSingleton();

    /* A private Constructor prevents any other 
     * class from instantiating.
     */
    ArrayList<Brands> arrayList = new ArrayList<>();
    private BrandNamesSingleton(){ }

    /* Static 'instance' method */
    public static BrandNamesSingleton getInstance( ) {

        return singleton;
    }
    public ArrayList<Brands> getBrands(){
        return arrayList;
    }
}
