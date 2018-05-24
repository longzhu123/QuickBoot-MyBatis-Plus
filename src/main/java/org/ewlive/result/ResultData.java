package org.ewlive.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

/**
 * Web通用的响应对象
 * @param <T>
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultData<T> {

    //响应编码
    private int resultCode = 1;

    //响应信息
    private String resultMsg = "成功";

    //响应数据
    private T data;

}
