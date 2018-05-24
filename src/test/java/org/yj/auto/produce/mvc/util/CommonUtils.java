
package org.yj.auto.produce.mvc.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author yejiabin
 * @date 2016-03-02
 * @desc 工具类
 */
public class CommonUtils {


    /**
     * 判断字符串是否为空
     * @param strValue
     * @return
     */
    public static boolean isStringEmpty(String strValue) {
        if(strValue == null || "".equals(strValue)) {
            return true;
        }
        return false;
    }


    public  static  String getNowDateStr(String pattern){
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        String dateStr = format.format(date);
        return  dateStr;
    }

  }
