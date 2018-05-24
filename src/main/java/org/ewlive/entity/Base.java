package org.ewlive.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 实体类的基类
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class Base {

    /**
     * token
     */
    @TableField(exist = false)
    private  String token;

    /**
     * ids编号集合
     */
    @TableField(exist = false)
    private List<String> ids;

    /**
     * 当前第几页
     */
    @TableField(exist = false)
    private  Integer current;

    /**
     * 每页显示的记录数
     */
    @TableField(exist = false)
    private  Integer size;
}
