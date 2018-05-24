package org.yj.auto.produce.mvc.util;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by yangjie on 2017/8/9.
 */
public class PropertiesUtil {


    public   static Properties properties;

    public  static  Properties applicationProperties;

    static {
        try {
           if(applicationProperties == null){
               applicationProperties = new Properties();
               applicationProperties.load(PropertiesUtil.class.getResourceAsStream("/application.properties"));
           }
           String useDb = applicationProperties.getProperty("useDb").toLowerCase();
           if(properties == null){
               properties = new Properties();
               properties.load(PropertiesUtil.class.getResourceAsStream("/"+useDb+".properties"));
           }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static   String  get(String key){
        return  properties.getProperty(key);
    }
}
