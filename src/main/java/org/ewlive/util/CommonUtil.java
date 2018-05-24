package org.ewlive.util;

import java.util.Collection;
import java.util.UUID;

/**
 * 公用工具类
 */
public class CommonUtil {

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

    /**
     * 判断字符串是否不为空
     * @param strValue
     * @return
     */
    public static boolean isStringNotEmpty(String strValue) {
        if(strValue != null && !"".equals(strValue)) {
            return true;
        }
        return false;
    }

    /**
     * 判断集合是否为空
     * @param colValue
     * @return
     */
    public static boolean isCollectionEmpty(Collection colValue) {
        if(colValue == null || colValue.size() == 0) {
            return true;
        }
        return false;
    }

    /**
     * 判断集合是否不为空
     * @param colValue
     * @return
     */
    public static boolean isCollectionNotEmpty(Collection colValue) {
        if(colValue != null && colValue.size() > 0) {
            return true;
        }
        return false;
    }


    /**
     * @return
     * @description 创建UUID唯一键
     */
    public static String createUUID() {
        UUID uuid = UUID.randomUUID();
        String uu = uuid.toString().replaceAll("-", "");
        return uu;
    }

}
