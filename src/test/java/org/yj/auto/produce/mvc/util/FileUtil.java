package org.yj.auto.produce.mvc.util;

import java.io.File;

/**
 * Created by yangjie on 2017/8/9.
 */
public class FileUtil {


    //实体类的路径
    public static String SYS_MNG_BEAN_PATH = "";

    //Mapper的路径
    public static String SYS_MNG_MAPPER_PATH = "";

    //Mapper.xml的路径
    public static String SYS_MNG_MAPPERXML_PATH = "";

    //service的路径
    public static String SYS_MNG_SERVICE_PATH = "";

    //controller的路径
    public static String SYS_MNG_CONTROLLER_PATH = "";



    public static void initMvcPackagePath() {
        File file = new File("");
        String path = file.getAbsolutePath();

        String modelName = "\\src\\main\\java\\";
        String entityPath = PropertiesUtil.get("auto.entiy.package").replace(".", "\\\\");
        String dataSourcePath = PropertiesUtil.get("auto.mapper.package").replace(".", "\\\\");
        String servicePath = PropertiesUtil.get("auto.service.package").replace(".", "\\\\");
        String controllerPath = PropertiesUtil.get("auto.controller.package").replace(".", "\\\\");
        SYS_MNG_BEAN_PATH = path + modelName + entityPath;
        SYS_MNG_MAPPER_PATH = path + modelName+ dataSourcePath;
        SYS_MNG_MAPPERXML_PATH = path + "\\src\\main\\resources\\mapper\\";
        SYS_MNG_SERVICE_PATH = path + modelName+ servicePath;
        SYS_MNG_CONTROLLER_PATH = path + modelName + controllerPath;

        File file2 = new File(SYS_MNG_BEAN_PATH);
        File file3 = new File(SYS_MNG_MAPPER_PATH);
        File file4 = new File(SYS_MNG_MAPPERXML_PATH);
        File file5 = new File(SYS_MNG_SERVICE_PATH);
        File file6 = new File(SYS_MNG_CONTROLLER_PATH);

        if (!file2.exists()) {
            file2.mkdirs();
        }
        if (!file3.exists()) {
            file3.mkdirs();
        }
        if (!file4.exists()) {
            file4.mkdirs();
        }

        if (!file5.exists()) {
            file5.mkdirs();
        }

        if (!file6.exists()) {
            file6.mkdirs();
        }
    }


}
