package org.ewlive.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * 基础异常类
 */
@Getter
@Setter
public class BaseException extends  RuntimeException{

    /**
     * 异常编码
     */
    private  int resultCode;


    /**
     * 异常信息
     */
    private  String resultMsg;

    public  BaseException(){

    }

    public  BaseException (String resultMsg){
        this.resultMsg = resultMsg;
    }

    public BaseException(int resultCode,String resultMsg){
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }

}
