package org.ewlive.exception;

/**
 * 业务处理异常类
 */
public class ServiceException extends  BaseException{

    public ServiceException(String resultMsg){
        super(resultMsg);
    }

    public ServiceException(int restultCode,String resultMsg){
        super(restultCode,resultMsg);
    }
}
