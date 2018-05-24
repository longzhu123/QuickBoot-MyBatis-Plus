package org.yj.auto.produce.mvc.dao;

import org.yj.auto.produce.mvc.entity.AutoColumn;
import org.yj.auto.produce.mvc.entity.AutoTable;
import org.yj.auto.produce.mvc.util.CommonUtils;
import org.yj.auto.produce.mvc.util.DbUtil;
import org.yj.auto.produce.mvc.util.FileUtil;
import org.yj.auto.produce.mvc.util.PropertiesUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangjie on 2017/8/9.
 */
public class AutoTableDao {


    /**
     * 获取某个数据库所有的表名
     *
     * @return
     */
    public static List<AutoTable> getTableName(String tableName) {
        String dialect = PropertiesUtil.get("db.dialect").toLowerCase();
        List<AutoTable> autoTables = null;
        Object[] objects = null;
        String sql = "";
        if (dialect.equals("mysql")) {
            String dataBaseName = PropertiesUtil.get("autoDataBaseName");
            sql = "select table_name tableName,table_comment comment from information_schema.tables where table_schema = ? and table_name=?";
            objects = new Object[]{dataBaseName,tableName};
            autoTables = DbUtil.executeQuery(AutoTable.class, sql, objects);
        } else if (dialect.equals("oracle")) {
            sql = "select TABLE_NAME \"tableName\",COMMENTS \"comment\" from user_tab_comments where TABLE_NAME = ?";
            objects = new Object[]{tableName};
            autoTables = DbUtil.executeQuery(AutoTable.class, sql, objects);
        }
        return autoTables;
    }


    public static void autoMVC() {
        String autoTableName = PropertiesUtil.get("auto.table");
        FileUtil.initMvcPackagePath();
        autoEntity(autoTableName);
        autoMapper(autoTableName);
        autoMapperXml(autoTableName);
        autoService(autoTableName);
        autoController(autoTableName);
        getJsonByEntity(autoTableName);
    }

    /**
     * 根据实体类获取对应的json字符串
     * @param autoTableName
     */
    private static String getJsonByEntity(String autoTableName) {
        String returnStr= "";
        List<AutoTable> tableList = getTableName(autoTableName);
        StringBuffer requestStr = new StringBuffer();
        StringBuffer requestDetailJsonStr = new StringBuffer();
        StringBuffer respJsonStr = new StringBuffer();
        requestStr.append("{");
        respJsonStr.append("{");
        requestDetailJsonStr.append("{");
        for (AutoTable table : tableList) {
            String tableName = table.getTableName().toLowerCase();
            List<AutoColumn> columns = getColumnsByTable(tableName);
            for (AutoColumn column : columns) {
                String isRequire = column.getNullable().equalsIgnoreCase("no")?"必填":"";
                requestDetailJsonStr.append("\r\n\t"+column.getColumnName()+":"+column.getComment()+","+"//"+isRequire);
                respJsonStr.append("\r\n\t"+column.getColumnName()+":"+column.getComment()+",");
                if(column.getDataType().equals("int") || column.getDataType().equals("float")){
                    requestStr.append("\r\n\t\""+column.getColumnName()+"\":1,");
                }else if(column.getDataType().equals("varchar")){
                    requestStr.append("\r\n\t\""+column.getColumnName()+"\":\"1\",");
                }
            }
        }


        String s = requestStr.toString().substring(0,requestStr.length()-1)+"\r\n}";
        returnStr+="<====请求参数====>\r\n";
        returnStr+=s;

        String s1 = requestDetailJsonStr.substring(requestDetailJsonStr.toString().lastIndexOf(",")+1);
        String s2 = requestDetailJsonStr.substring(0,requestDetailJsonStr.toString().lastIndexOf(","));
        returnStr+="\r\n<====请求参数说明====>\r\n";
        returnStr+=s2+s1+"\r\n}";
        returnStr+="\r\n<====响应参数====>\r\n";
        returnStr+=s;

        String s4 = respJsonStr.substring(respJsonStr.toString().lastIndexOf(",")+1);
        String s3 = respJsonStr.substring(0,respJsonStr.toString().lastIndexOf(","));
        returnStr+="\n<====响应参数说明====>\n";
        returnStr+=s3+s4+"\r\n}";


        File file = new File("api-doc/"+autoTableName+".txt");
        if(file.exists()){
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            bw.write(returnStr);
            bw.flush();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return returnStr;
    }


    /**
     * 自动生成实体类
     */
    public static void autoEntity(String autoTableName) {
        try {
            //String packagePath = PropertiesUtil.get("auto.entiy.package").replace('.', '/');
            //createDirByPackgeName(packagePath);
            List<AutoTable> tableList = getTableName(autoTableName);

            for (AutoTable table : tableList) {
                String tableName = table.getTableName().toLowerCase();
                String entityComment = getEntityComment(table, true, false, false, false);
                String str = getEntityStr(tableName, entityComment);
                File entityFile = new File(FileUtil.SYS_MNG_BEAN_PATH + "/" + getClassName(tableName) + ".java");
                if (entityFile.exists()) {
                    entityFile.delete();
                }
                entityFile.createNewFile();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileUtil.SYS_MNG_BEAN_PATH + "/" + getClassName(tableName) + ".java")));
                writer.write(str);
                writer.flush();
                writer.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 自动生成Mapper对应的java源文件
     */
    public static void autoMapper(String autoTableName) {
        try {
            List<AutoTable> tableList = getTableName(autoTableName);
            for (AutoTable table : tableList) {
                String tableName = table.getTableName().toLowerCase();
                String className = getClassName(tableName) + "Mapper";
                String entityComment = getEntityComment(table, false, false, true, false);
                String str = getMapperJavaStr(className, entityComment, table.getComment());
                File entityFile = new File(FileUtil.SYS_MNG_MAPPER_PATH + "/" + className + ".java");
                if (entityFile.exists()) {
                    entityFile.delete();
                }
                entityFile.createNewFile();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileUtil.SYS_MNG_MAPPER_PATH + "/" + className + ".java")));
                writer.write(str);
                writer.flush();
                writer.close();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 自动生成Mapper对应的xml
     */
    public static void autoMapperXml(String autoTableName) {
        try {
            List<AutoTable> tableList = getTableName(autoTableName);
            for (AutoTable table : tableList) {
                String tableName = table.getTableName();
                String className = getClassName(tableName.toLowerCase()) + "Mapper";
                File entityFile = new File(FileUtil.SYS_MNG_MAPPERXML_PATH + "/" + className + ".xml");
                if (entityFile.exists()) {
                    entityFile.delete();
                }
                entityFile.createNewFile();
                String str = getautoMapperXmlStr(className, tableName, convertDbTableComment(table.getComment()));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileUtil.SYS_MNG_MAPPERXML_PATH + "/" + className + ".xml")));
                writer.write(str);
                writer.flush();
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 自动生成Service
     */
    public static void autoService(String autoTableName) {
        try {
            List<AutoTable> tableList = getTableName(autoTableName);
            for (AutoTable table : tableList) {
                String tableName = table.getTableName().toLowerCase();
                String str = getServiceStr(tableName, getEntityComment(table, false, true, false, false), convertDbTableComment(table.getComment()));

                File entityFile = new File(FileUtil.SYS_MNG_SERVICE_PATH + "/" + getClassName(tableName) + "Service.java");
                if (entityFile.exists()) {
                    entityFile.delete();
                }
                entityFile.createNewFile();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileUtil.SYS_MNG_SERVICE_PATH + "/" + getClassName(tableName) + "Service.java")));
                writer.write(str);
                writer.flush();
                writer.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 自动生成Controller
     */
    public static void autoController(String autoTableName) {
        try {
            List<AutoTable> tableList = getTableName(autoTableName);
            for (AutoTable table : tableList) {
                String tableName = table.getTableName().toLowerCase();
                String str = getControllerStr(getClassName(tableName), convertDbTableComment(table.getComment()));
                File entityFile = new File(FileUtil.SYS_MNG_CONTROLLER_PATH + "/" + getClassName(tableName) + "Controller.java");
                if (entityFile.exists()) {
                    entityFile.delete();
                }
                entityFile.createNewFile();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileUtil.SYS_MNG_CONTROLLER_PATH + "/" + getClassName(tableName) + "Controller.java")));
                writer.write(str);
                writer.flush();
                writer.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取生成Controller的字符串
     *
     * @param className
     * @param tableComment
     * @return
     */
    public static String getControllerStr(String className, String tableComment) {

        StringBuffer searchSb = new StringBuffer();
        StringBuffer addSb = new StringBuffer();
        StringBuffer updateSb = new StringBuffer();
        StringBuffer deleteSb = new StringBuffer();
        StringBuffer getByIdSb = new StringBuffer();
        StringBuffer likeSearchSb = new StringBuffer();
        getByIdSb.append("\n\t/**\n" +
                "\t * 根据id查询" + tableComment + "\n" +
                "\t * @param request\n" +
                "\t * @return\n" +
                "\t */");


        searchSb.append("\n\t/**\n" +
                "\t * 多条件查询" + tableComment + "\n" +
                "\t * @param request\n" +
                "\t * @return\n" +
                "\t */");

        likeSearchSb.append("\n\t/**\n" +
                "\t * 模糊查询" + tableComment + "(分页)\n" +
                "\t * @param request\n" +
                "\t * @return\n" +
                "\t */");

        addSb.append("\n\t/**\n" +
                "\t * 添加" + tableComment + "\n" +
                "\t * @param request\n" +
                "\t * @return\n" +
                "\t */");
        updateSb.append("\n\t/**\n" +
                "\t * 根据id修改" + tableComment + "\n" +
                "\t * @param request\n" +
                "\t * @return\n" +
                "\t */");
        deleteSb.append("\n\t/**\n" +
                "\t * 根据ids批量删除" + tableComment + "\n" +
                "\t * @param request\n" +
                "\t * @return\n" +
                "\t */");

        String lowClassName = className.substring(0, 1).toLowerCase() + className.substring(1);
        String controllerName = className + "Controller";
        String serviceName = className + "Service";
        String lowServiceName = serviceName.substring(0, 1).toLowerCase() + serviceName.substring(1);
        StringBuffer sb = new StringBuffer();
        sb.append("package " + PropertiesUtil.get("auto.controller.package") + ";");
        sb.append("\r\nimport java.util.List;\n");
        sb.append("import org.springframework.web.bind.annotation.RequestMapping;\n" +
                "import org.springframework.web.bind.annotation.RestController;\n");
        sb.append("import javax.annotation.Resource;\n");
        sb.append("\rimport org.ewlive.result.ResultData;");
        sb.append("\r\nimport com.baomidou.mybatisplus.plugins.Page;\r");
        sb.append("import org.springframework.web.bind.annotation.RequestBody;\n");
        sb.append("import " + PropertiesUtil.get("auto.entiy.package") + "." + className + ";");
        sb.append("\r\nimport " + PropertiesUtil.get("auto.service.package") + "." + serviceName + ";");

        sb.append("\r\n\r\n");
        String dateStr = CommonUtils.getNowDateStr("yyyy/MM/dd");
        String fileInfo = "Create by " + PropertiesUtil.get("file.author") + " on " + dateStr + "";
        sb.append("/**" +
                "\r\n * " + tableComment + "Controller" +
                "\r\n * " + fileInfo +
                "\r\n */\r\n");
        sb.append("@RestController");
        sb.append("\r\n@RequestMapping(\"/" + className.substring(0,1).toLowerCase()+className.substring(1) + "\")");
        sb.append("\r\npublic class " + controllerName + "{");
        sb.append("\r\n\n\t@Resource");
        sb.append("\r\n\tprivate " + serviceName + " " + lowServiceName + ";");

        sb.append("\n" + getByIdSb);
        sb.append("\r\n\t@RequestMapping(\"/get" + className + "ById\")");
        sb.append("\r\n\tpublic ResultData<" + className + "> get" + className + "ById(@RequestBody " + className + " request){");
        sb.append("\n\t\treturn " + lowServiceName + "." + "get" + className + "ById(request);");
        sb.append("\r\n\t}");

        if(PropertiesUtil.get("auto.enable.page").equals("true")){
            sb.append("\n" + likeSearchSb);
            sb.append("\r\n\t@RequestMapping(\"/likeSearch" + className + "ByPage\")");
            sb.append("\r\n\tpublic ResultData<Page<" + className + ">> likeSearch" + className + "ByPage(@RequestBody " + className + " request){");
            sb.append("\r\n\t\treturn " + lowServiceName + "." + "likeSearch" + className + "ByPage(request);");
            sb.append("\r\n\t}");
        }else{
            sb.append("\n" + searchSb);
            sb.append("\r\n\t@RequestMapping(\"/get" + className + "ByParams\")");
            sb.append("\r\n\tpublic ResultData<List<" + className + ">> get" + className + "ByParams(@RequestBody " + className + " request){");
            sb.append("\r\n\t\treturn " + lowServiceName + "." + "get" + className + "ByParams(request);");
            sb.append("\r\n\t}");
        }




        sb.append("\n" + addSb);
        sb.append("\r\n\t@RequestMapping(\"/add" + className + "\")");
        sb.append("\r\n\tpublic ResultData add" + className + "(@RequestBody " + className + " request){");
        sb.append("\r\n\t\treturn " + lowServiceName + "." + "add" + className + "(request);");
        sb.append("\r\n\t}");

        sb.append("\n" + updateSb);
        sb.append("\r\n\t@RequestMapping(\"/update" + className + "ById\")");
        sb.append("\r\n\tpublic ResultData update" + className + "ById(@RequestBody " + className + " request){");
        sb.append("\r\n\t\treturn " + lowServiceName + "." + "update" + className + "ById(request);");
        sb.append("\r\n\t}");

        sb.append("\n" + deleteSb);
        sb.append("\r\n\t@RequestMapping(\"/deleteBatch" + className + "ByIds\")");
        sb.append("\r\n\tpublic ResultData deleteBatch" + className + "ByIds(@RequestBody " + className + " request){");
        sb.append("\r\n\t\treturn " + lowServiceName + "." + "deleteBatch" + className + "ByIds(request);");
        sb.append("\r\n\t}");

        sb.append("\n\r}");
        return sb.toString();
    }


    /**
     * 获取生成Service的字符串
     *
     * @param tableName
     * @return
     */
    public static String getServiceStr(String tableName, String serviceComment, String tableComment) {
        String className = getClassName(tableName);
        StringBuffer sb = new StringBuffer();

        StringBuffer searchSb = new StringBuffer();
        StringBuffer addSb = new StringBuffer();
        StringBuffer updateSb = new StringBuffer();
        StringBuffer deleteSb = new StringBuffer();
        StringBuffer getByIdSb = new StringBuffer();
        StringBuffer checkParamsId = new StringBuffer();
        StringBuffer checkParamsAdd = new StringBuffer();
        StringBuffer likeSearchSb = new StringBuffer();
        String mapperTemp = className + "Mapper";
        String classNameTemp = className.substring(0, 1).toLowerCase() + className.substring(1);
        sb.append("package " + PropertiesUtil.get("auto.service.package") + ";");
        sb.append("\n\nimport javax.annotation.Resource;\n" +
                "import org.springframework.stereotype.Service;");
        sb.append("\r\nimport java.util.List;");
        sb.append("\nimport org.ewlive.constants.ExceptionConstants;\n" +
                "import org.ewlive.exception.ServiceException;");
        sb.append("\r\nimport lombok.extern.slf4j.Slf4j;");
        sb.append("\r\nimport org.ewlive.result.ResultData;");
        sb.append("\r\nimport org.ewlive.util.CommonUtil;");
        sb.append("\r\nimport com.baomidou.mybatisplus.mapper.EntityWrapper;");
        sb.append("\r\nimport " + PropertiesUtil.get("auto.entiy.package") + "." + className + ";");
        sb.append("\r\nimport " + PropertiesUtil.get("auto.mapper.package") + "." + className + "Mapper;");
        sb.append("\r\nimport org.springframework.transaction.annotation.Propagation;\n" +
                "\nimport org.springframework.transaction.annotation.Transactional;");
        sb.append("\r\nimport com.baomidou.mybatisplus.plugins.Page;");
        sb.append("\r\nimport com.alibaba.fastjson.JSON;");
        sb.append("\r\n\n");
        sb.append(serviceComment);
        sb.append("\r\n@Slf4j");
        sb.append("\r\n@Service");
        sb.append("\npublic class " + className + "Service{\n");
        sb.append("\r\n\t@Resource\n\t");
        getByIdSb.append("/**\n" +
                "\t * 根据id查询" + tableComment + "\n" +
                "\t * @param request\n" +
                "\t * @return\n" +
                "\t */");

        searchSb.append("/**\n" +
                "\t * 多条件查询" + tableComment + "\n" +
                "\t * @param request\n" +
                "\t * @return\n" +
                "\t */");

        likeSearchSb.append("/**\n" +
                "\t * 模糊查询" + tableComment + "(分页)\n" +
                "\t * @param request\n" +
                "\t * @return\n" +
                "\t */");


        addSb.append("\t/**\n" +
                "\t * 添加" + tableComment + "\n" +
                "\t * @param request\n" +
                "\t * @return\n" +
                "\t */");
        updateSb.append("\t/**\n" +
                "\t * 根据id修改" + tableComment + "\n" +
                "\t * @param request\n" +
                "\t * @return\n" +
                "\t */");
        deleteSb.append("\t/**\n" +
                "\t * 根据ids批量删除" + tableComment + "\n" +
                "\t * @param request\n" +
                "\t * @return\n" +
                "\t */");

        checkParamsId.append("\t/**\n" +
                "\t * 检查参数中的id是否为空\n" +
                "\t * @param request\n" +
                "\t */");
        checkParamsAdd.append("\t/**\n" +
                "\t * 检查添加参数是否齐全\n" +
                "\t * @param request\n" +
                "\t */");

        sb.append("private " + className + "Mapper " + mapperTemp.substring(0,1).toLowerCase()+mapperTemp.substring(1) + ";\n");

        //事务的类型 (REQUIRED,SUPPORTS)
        String tranType = "REQUIRED";

        sb.append("\r\n\t" + getByIdSb);
        sb.append("\r\n\tpublic  ResultData<" + className + "> get" + className + "ById(" + className + " request){");
        sb.append("\n\t\t//检查参数Id是否为空");

        sb.append("\n\t\tcheckParamsId(request);");
        sb.append("\n\t\tlog.info(\"根据id查询"+ tableComment+":请求参数=====>\"+JSON.toJSONString(request));");
        sb.append("\n\t\tResultData<" + className + "> data= new " + "ResultData<>();");
        sb.append("\n\t\t//根据id查询" + tableComment);
        sb.append("\n\t\t" + className + " " + classNameTemp + " = " +  mapperTemp.substring(0,1).toLowerCase()+mapperTemp.substring(1) + ".selectById(request.getId());");
        sb.append("\r\n\t\tdata.setData(" + classNameTemp + ");");
        sb.append("\n\t\tlog.info(\"数据请求成功,=====>返回:\"+JSON.toJSONString(" + classNameTemp + "));");
        sb.append("\r\n\t\treturn data;");
        sb.append("\n\t}\n\n");

        if(PropertiesUtil.get("auto.enable.page").equals("true")){
            sb.append("\r\n\t"+likeSearchSb);
            sb.append("\n\tpublic ResultData<Page<"+className+">> likeSearch"+className+"ByPage("+className+" request){\n" +
                    "\t\tlog.info(\"模糊查询"+tableComment+"(分页):请求参数=====>\"+JSON.toJSONString(request));\n" +
                    "\t\tResultData<Page<"+className+">> data= new ResultData<>();\n" +
                    "\t\tPage<"+className+"> page = new Page<>(request.getCurrent(),request.getSize());\n" +
                    "\t\t//模糊查询"+tableComment+"(分页)" +
                    "\n\t\tList<" + className + "> " + classNameTemp + "List = " +  mapperTemp.substring(0,1).toLowerCase()+mapperTemp.substring(1) + ".likeSearch"+className+"ByPage(page,request);"+
                    "\n\t\tpage.setRecords("+classNameTemp+"List);\n" +
                    "\t\tdata.setData(page);\n" +
                    "\t\tlog.info(\"数据请求成功,=====>返回:\"+JSON.toJSONString(" + classNameTemp + "List));"+
                    "\n\t\treturn data;\n" +
                    "\t}\n\n");
        }else{
            sb.append("\r\n\t" + searchSb);
            sb.append("\r\n\tpublic ResultData<List<" + className + ">> get" + className + "ByParams(" + className + " request){");
            sb.append("\n\t\tlog.info(\"多条件查询" + tableComment + "信息:请求参数=====>\"+JSON.toJSONString(request));");
            sb.append("\n\t\tResultData<List<" + className + ">> data= new ResultData<>();");
            sb.append("\n\t\t//多条件查询" + tableComment + "信息");
            sb.append("\n\t\tList<" + className + "> " + classNameTemp + "List = " +  mapperTemp.substring(0,1).toLowerCase()+mapperTemp.substring(1) + ".selectList(new EntityWrapper<>(request));");
            sb.append("\n\t\tdata.setData(" + classNameTemp + "List" + ");");
            sb.append("\n\t\tlog.info(\"数据请求成功,=====>返回:\"+JSON.toJSONString(" + classNameTemp + "List));");
            sb.append("\r\n\t\treturn data;");
            sb.append("\n\t}\n\n");
        }


        sb.append("\r\n" + addSb);
        sb.append("\r\n\t@Transactional(rollbackFor = Exception.class, propagation = Propagation." + tranType + ")");
        sb.append("\n\tpublic ResultData add" + className + "(" + className + " request){");
        sb.append("\n\t\tlog.info(\"添加"+tableComment+",请求参数====>\"+JSON.toJSONString(request));");
        sb.append("\n\t\t//检查必填参数项是否空");
        sb.append("\n\t\tcheckParamsForAdd(request);");
        sb.append("\n\t\tlog.info(\"添加====>参数校验成功\");");
        sb.append("\n\t\tResultData data = new ResultData();");
        sb.append("\n\t\t//添加" + tableComment);
        sb.append("\n\t\tint i = " +  mapperTemp.substring(0,1).toLowerCase()+mapperTemp.substring(1) + ".insert(request);");
        sb.append("\n\t\tif(i == 0){" +
                "\r\n\t\t\tthrow  new ServiceException(ExceptionConstants.ADD_FAIL);" +
                "\r\n\t\t}");
        sb.append("\n\t\tlog.info(\"添加成功\");");
        sb.append("\r\n\t\treturn data;");
        sb.append("\n\t}\n\n");


        sb.append("\r\n" + updateSb);
        sb.append("\r\n\t@Transactional(rollbackFor = Exception.class, propagation = Propagation." + tranType + ")");
        sb.append("\n\tpublic ResultData update" + className + "ById(" + className + " request){");
        sb.append("\n\t\tlog.info(\"修改"+tableComment+",请求参数====>\"+JSON.toJSONString(request));");
        sb.append("\n\t\t//检查id是否为空");
        sb.append("\n\t\tcheckParamsId(request);");
        sb.append("\n\t\tlog.info(\"参数校验成功,id不为空\");");
        sb.append("\n\t\tResultData data = new ResultData();");

        sb.append("\n\t\t//根据Id修改" + tableComment);
        sb.append("\n\t\tint i = " +  mapperTemp.substring(0,1).toLowerCase()+mapperTemp.substring(1) + ".updateById(request);");
        sb.append("\n\t\tif(i == 0){" +
                "\r\n\t\t\tthrow  new ServiceException(ExceptionConstants.UPDATE_FAIL);" +
                "\r\n\t\t}");
        sb.append("\n\t\tlog.info(\"修改成功\");");
        sb.append("\r\n\t\treturn data;");
        sb.append("\n\t}\n\n");

        sb.append("\r\n" + deleteSb);
        sb.append("\r\n\t@Transactional(rollbackFor = Exception.class, propagation = Propagation." + tranType + ")");
        sb.append("\n\tpublic ResultData deleteBatch" + className + "ByIds(" + className + " request){");
        sb.append("\n\t\tlog.info(\"根据ids批量删除"+tableComment+",请求参数====>\"+JSON.toJSONString(request));");
        sb.append("\n\t\t//检查ids是否为空");
        sb.append("\n\t\tcheckParamsIds(request);");
        sb.append("\n\t\tlog.info(\"参数校验成功,ids不为空\");");
        sb.append("\n\t\tResultData data = new ResultData();");

        sb.append("\n\t\t//根据ids批量删除" + tableComment);
        sb.append("\n\t\tint i = " +  mapperTemp.substring(0,1).toLowerCase()+mapperTemp.substring(1) + ".deleteBatchIds(request.getIds());");
        sb.append("\n\t\tif(i == 0){" +
                "\r\n\t\t\tthrow  new ServiceException(ExceptionConstants.DELTE_FAIL);" +
                "\r\n\t\t}");
        sb.append("\n\t\tlog.info(\"删除成功\");");
        sb.append("\r\n\t\treturn data;");
        sb.append("\n\t}\n\n");


        sb.append(checkParamsId);
        sb.append("\n\tpublic void checkParamsId(" + className + " request){");
        sb.append("\n\t\t if(CommonUtil.isStringEmpty(request.getId())){\n" +
                "            throw  new ServiceException(ExceptionConstants.ID_NOT_NULL);\n" +
                "        }");
        sb.append("\n\t}\n\n");


        sb.append("\t/**\n" +
                "\t * 检查参数中的ids是否为空\n" +
                "\t * @param request\n" +
                "\t */\n" +
                "\tpublic void  checkParamsIds("+className+" request) {\n" +
                "\t\tif (CommonUtil.isCollectionEmpty(request.getIds())) {\n" +
                "\t\t\tthrow new ServiceException(ExceptionConstants.IDS_NOT_NULL);\n" +
                "\t\t}\n" +
                "\t}\n\n");


        List<AutoColumn> autoColumns = getColumnsByTable(tableName);
        sb.append(checkParamsAdd);
        sb.append("\n\tpublic void checkParamsForAdd(" + className + " request){");
        for (AutoColumn autoColumn : autoColumns) {
            if(!autoColumn.getColumnName().equalsIgnoreCase("id")){
                String colName = "";
                if(autoColumn.getColumnName().indexOf("_")>0){
                    String [] s =autoColumn.getColumnName().split("_");
                    for (int i = 0; i < s.length; i++) {
                        String item = s[i];
                        colName+=item.substring(0,1).toUpperCase()+item.substring(1);
                    }
                }else{
                    colName = autoColumn.getColumnName().substring(0,1).toUpperCase()+autoColumn.getColumnName().substring(1);
                }

                if(autoColumn.getNullable().equalsIgnoreCase("no")){
                    sb.append("\n\t\t//判断"+autoColumn.getComment()+"是否为空");
                    sb.append("\n\t\tif(CommonUtil.isStringEmpty(request.get"+colName+"())){\n" +
                            "            throw  new ServiceException(ExceptionConstants."+colName.toUpperCase()+"_NOT_NULL);\n" +
                            "        }");
                }
            }

        }
        sb.append("\n\t}\n\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * 获取自动生成Mapper对应的xml的字符串
     *
     * @param className
     * @param tableName
     * @param tableComment
     * @return
     */
    public static String getautoMapperXmlStr(String className, String tableName, String tableComment) {
        String userDb = PropertiesUtil.applicationProperties.getProperty("useDb");
        String entityName = className.substring(0, className.indexOf("Mapper"));
        StringBuffer sb = new StringBuffer();
        StringBuffer columnSb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
                "<mapper namespace=\"" + PropertiesUtil.get("auto.mapper.package") + "." + className + "\">");

        List<AutoColumn> columns = getColumnsByTable(tableName);
        sb.append("\r\n\r\n\t<!--" + tableComment + "基础列-->");
        sb.append("\n\t<sql id=\"Base_Column_List\">\r\n\t\t");
        columnSb.append("\r\n\t\twhere 1=1 ");
        for (AutoColumn column : columns) {
            String columnName = column.getColumnName().toLowerCase();
            String beanName= "";
            if(columnName.indexOf("_")>0){
                String [ ]s =columnName.split("_");
                for (int i = 0; i < s.length; i++) {
                    if(i==0){
                        beanName+=s[i];
                    }else{
                        beanName+=s[i].substring(0,1).toUpperCase()+s[i].substring(1);
                    }
                }
            }else{
                beanName = columnName;
            }
            sb.append(tableName+"."+columnName + ",");
            if (userDb.equals("oracle")) {
                columnSb.append("\r\n\t\t\t<if test=\"" + beanName + " != null\"> and " + columnName + "=#{" + beanName + "}</if>");
            } else if (userDb.equals("mysql")) {
                columnSb.append("\r\n\t\t<if test=\"" + beanName + " != null\"> and " + columnName + "=#{" + beanName + "}</if>");
            }

        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("\r\n\t</sql>");

        if(PropertiesUtil.get("auto.enable.page").equals("true")){
            sb.append("\r\n\n\t<!--模糊查询" + tableComment + "(分页)-->");
            sb.append("\n\t<select id=\"likeSearch" + entityName + "ByPage\"  resultType=\"" +  entityName + "\" parameterType=\""+ entityName+"" + "\">");

            sb.append("\r\n\t\tselect <include refid=\"Base_Column_List\"/> from " + tableName.toLowerCase());
            sb.append(columnSb);
            sb.append("\n\t</select>");
        }

        sb.append("\n\r</mapper>");
        return sb.toString();
    }


    /**
     * 获取Mapper.java的生成字符串
     *
     * @param className     类名
     * @param entityComment 实体类的注释
     * @return
     */
    public static String getMapperJavaStr(String className, String entityComment, String tableComment) {
        StringBuffer sb = new StringBuffer();
        StringBuffer searchSb = new StringBuffer();
        sb.append("package " + PropertiesUtil.get("auto.mapper.package") + ";");
        sb.append("\n\rimport java.util.List;");
        String entityName = className.substring(0, className.indexOf("Mapper"));
        sb.append("\n\rimport com.baomidou.mybatisplus.mapper.BaseMapper;");
        sb.append("\n\rimport com.baomidou.mybatisplus.plugins.pagination.Pagination;");
        sb.append("\nimport " + PropertiesUtil.get("auto.entiy.package") + "." + entityName + ";");
        sb.append("\n\r");
        sb.append("\n\r");
        sb.append("\n\r");
        sb.append(entityComment);
        sb.append("\r\n");
        sb.append("public interface " + className + "  extends BaseMapper<"+entityName+"> {\n");
        sb.append("\r\n");
        tableComment = convertDbTableComment(tableComment);

        if(PropertiesUtil.get("auto.enable.page").equals("true")){
            searchSb.append("    /**\n" +
                    "     * 模糊查询" + tableComment + "(分页)\n" +
                    "     * @param pagination\n" +
                    "     * @param "+entityName.toLowerCase()+"\n" +
                    "     * @return\n" +
                    "     */");

            sb.append(searchSb);
            sb.append("\r\n\tpublic List<" + entityName + "> likeSearch" + entityName + "ByPage(Pagination pagination," + entityName + " "+entityName.toLowerCase()+");");
        }
        sb.append("\n\r");
        sb.append("\r\n}");
        return sb.toString();
    }


    /**
     * 根据报名创建文件夹
     *
     * @param packagePath
     */
    public static void createDirByPackgeName(String packagePath) {
        String[] split = packagePath.split("/");
        StringBuffer sb = new StringBuffer();
        for (String s : split) {
            sb.append(s + "/");
            File file = new File(sb.toString());
            if (file.exists()) {
                file.delete();
            }
            file.mkdir();
        }

    }

    /**
     * 获取实体类的字符串
     *
     * @param tableName
     */
    public static String getEntityStr(String tableName, String entityComment) {
        StringBuffer sb = new StringBuffer();
        StringBuffer sb1 = new StringBuffer();

        List<AutoColumn> columns = getColumnsByTable(tableName);
        String entityPackage = PropertiesUtil.get("auto.entiy.package");
        String className = getClassName(tableName);

        sb.append("package " + entityPackage + ";");
        sb.append("\n\r");
        sb.append("import com.baomidou.mybatisplus.annotations.TableField;\r" +
                "import com.baomidou.mybatisplus.annotations.TableName;\r");
        sb.append("import lombok.Getter;\r" +
                "import lombok.Setter;\r\n\n");
        sb.append(entityComment + "\r\n");
        sb.append("@TableName(\""+tableName+"\")\n");
        sb.append("@Getter\n" +"@Setter\n");

        sb.append("public class " + className + " extends Base{");
        sb1.append("\n\r");
        for (AutoColumn column : columns) {
            sb.append("\n\r\n\r");
            sb.append("\t/**\n" +
                    "\t * " + column.getComment() + "\n" +
                    "\t */\n");
            String s = appendFields(column);
            sb.append(s);
        }


        sb.append(sb1);

        if (sb.indexOf("Date") != -1) {
            sb.insert(("package " + entityPackage + ";").length(), "\r\nimport java.util.Date;\r\n");
        }

        sb.append("\n\r}");
        return sb.toString();
    }



    /**
     * 拼接该类的属性
     *
     * @param column
     * @return
     */
    public static String appendFields(AutoColumn column) {
        StringBuffer sb = new StringBuffer();
        String columnName = column.getColumnName().toLowerCase();
        String columnType = column.getDataType().toLowerCase();
        String type = null;
       if (columnType.equals("int") || columnType.equals("number")) {
            type = "Integer";
        } else if (columnType.equals("char") ||
                columnType.equals("varchar") ||
                columnType.equals("nchar") ||
                columnType.equals("nvarchar") ||
                columnType.equals("varchar2") ||
                columnType.equals("nvarchar2")) {
            type = "String";
        } else if (column.getDataType().equals("datetime")) {
            type = "Date";
        } else if (column.getDataType().equals("tinyint")) {
            type = "Boolean";
        } else if (column.getDataType().equals("double")) {
            type = "Double";
        }
        String filedName = "";
       if(columnName.indexOf("_")>0){
           String [] s = columnName.split("_");
           for (int i = 0; i < s.length; i++) {
               if(i == 0){
                   filedName+=s[i];
               }else{
                   filedName+=s[i].substring(0,1).toUpperCase()+s[i].substring(1);
               }
           }
       }else {
           filedName = columnName;
       }
        sb.append("\t@TableField(\""+columnName+"\")\r\n");
        sb.append("\tprivate " + type + " " + filedName + ";");
        return sb.toString();
    }



    /**
     * 获取实体类的注释
     *
     * @param table
     * @return
     */
    public static String getEntityComment(AutoTable table, boolean isEntity, boolean isService, boolean isMapper, boolean isController) {
        String entityComment = "";
        String type = "";
        if (isService) {
            type += "Service";
        } else if (isMapper) {
            type += "Mapper";
        } else if (isController) {
            type += "Controller";
        } else if (isEntity) {
            type += "Bean";
        }
        if (!CommonUtils.isStringEmpty(table.getComment())) {
            String dateStr = CommonUtils.getNowDateStr("yyyy/MM/dd");
            String fileInfo = "Create by " + PropertiesUtil.get("file.author") + " on " + dateStr + "";
            String temp = table.getComment().replaceAll("表", type);
            if (table.getComment().indexOf("表") == -1) {

                entityComment = "/**\n" +
                        " * " + table.getComment() + type + " \n" +
                        " * " + fileInfo +
                        "\n */";

            } else {
                entityComment = "/**\n" +
                        " * " + table.getComment().replaceAll("表", type) + " \n" +
                        " * " + fileInfo +
                        " \n */";
            }
        }
        return entityComment;
    }


    /**
     * 转换数据库表的注释
     *
     * @param tableComment
     * @return
     */
    public static String convertDbTableComment(String tableComment) {
        String comment = "";
        if (!CommonUtils.isStringEmpty(tableComment)) {
            comment = tableComment.replaceAll("表", "");
        }
        return comment;
    }


    /**
     * 根据表名转成类名
     *
     * @param tableName
     * @return
     */
    public static String getClassName(String tableName) {
        StringBuffer sb = new StringBuffer();

        if(PropertiesUtil.get("auto.table.prefix.convert").equals("true")){
            tableName = tableName.substring(tableName.indexOf(PropertiesUtil.get("auto.table.prefix"))+(PropertiesUtil.get("auto.table.prefix").length()));
        }
        String[] split = tableName.split("_");
        for (String s : split) {
            String s1 = s.substring(0, 1).toUpperCase() + s.substring(1);
            sb.append(s1);
        }
        return sb.toString();
    }

    /**
     * 根据表名获取所有列
     *
     * @param tableName
     * @return
     */
    public static List<AutoColumn> getColumnsByTable(String tableName) {
        String dialect = PropertiesUtil.get("db.dialect").toLowerCase();
        List<AutoColumn> autoColumns = null;
        if (dialect.equals("mysql")) {
            String sql = "select  DATA_TYPE dataType,COLUMN_NAME columnName,column_comment comment,is_nullable nullable " +
                    "from information_schema.columns " +
                    "where table_name=? and table_schema=?";
            autoColumns = DbUtil.executeQuery(AutoColumn.class, sql, new Object[]{tableName, PropertiesUtil.get("autoDataBaseName")});
            return autoColumns;
        } else if (dialect.equals("oracle")) {
            String sql = "select column_name \"columnName\",data_type \"dataType\",'1' \"comment\",nullable from user_tab_columns where table_name=? order by column_id";
            autoColumns = DbUtil.executeQuery(AutoColumn.class, sql, new Object[]{tableName.toUpperCase()});

            String sql1 = "select column_name \"columnName\", comments \"comment\",'1' \"dataType\" from  user_col_comments\n" +
                    " where  table_name=?\n";
            List<AutoColumn> autoColumns1 = DbUtil.executeQuery(AutoColumn.class, sql1, new Object[]{tableName.toUpperCase()});

            for (AutoColumn autoColumn : autoColumns) {
                for (AutoColumn column : autoColumns1) {
                    if (autoColumn.getColumnName().equals(column.getColumnName())) {
                        autoColumn.setComment(column.getComment());
                        break;
                    }
                }
            }
        }
        return autoColumns;
    }

}
