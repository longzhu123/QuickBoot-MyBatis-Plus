package org.ewlive.handler;

import lombok.extern.slf4j.Slf4j;
import org.ewlive.constants.ResultConstants;
import org.ewlive.exception.ServiceException;
import org.ewlive.result.ResultData;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常Handler
 */

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    /**
     * 业务处理异常
     * @param req
     * @param e
     * @return
     * @throws Exception
     */
    @ExceptionHandler(value = ServiceException.class)
    public Object serviceExceptionHandler(HttpServletRequest req, Exception e) throws Exception {
        ServiceException serviceException = (ServiceException) e;
        ResultData resultData = new ResultData();
        resultData.setResultCode(ResultConstants.ERROR_CODE);
        resultData.setResultMsg(serviceException.getResultMsg());
        return resultData;
    }

    /**
     * 系统异常
     * @param req
     * @param e
     * @return
     * @throws Exception
     */
    @ExceptionHandler(value = Exception.class)
    public Object systemExceptionHandler(HttpServletRequest req, Exception e) throws Exception {
        ResultData resultData = new ResultData();
        e.printStackTrace();
        resultData.setResultCode(ResultConstants.ERROR_CODE);
        resultData.setResultMsg("系统异常");
        return resultData;
    }





}
