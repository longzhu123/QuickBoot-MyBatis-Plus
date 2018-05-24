package org.yj.auto.produce.mvc.entity;

/**
 * Created by yangjie on 2017/8/10.
 */
public class AutoColumn {

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 列名
     */
    private String columnName;

    /**
     * 注释
     */
    private String comment;

    /**
     * 字段是否null,  Y:为Null,N:不为Null
     */
    private String nullable;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


    public String getNullable() {
        return nullable;
    }

    public void setNullable(String nullable) {
        this.nullable = nullable;
    }
}
